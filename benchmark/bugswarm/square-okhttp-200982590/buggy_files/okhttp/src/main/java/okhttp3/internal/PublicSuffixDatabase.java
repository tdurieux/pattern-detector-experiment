/*
 * Copyright (C) 2017 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.internal.platform.Platform;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

import static okhttp3.internal.Util.closeQuietly;

/**
 * A database of public suffixes provided by
 * <a href="https://publicsuffix.org/">publicsuffix.org</a>.
 */
public final class PublicSuffixDatabase {
  public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";

  private static final byte[] WILDCARD_LABEL = new byte[]{'*'};
  private static final String[] EMPTY_RULE = new String[0];

  private static final byte EXCEPTION_MARKER = '!';

  /** True after we've attempted to read the list for the first time. */
  final AtomicBoolean listRead = new AtomicBoolean(false);

  /** Used for concurrent threads reading the list for the first time. */
  final CountDownLatch readCompleteLatch = new CountDownLatch(1);

  // The lists are held as a large array of UTF-8 bytes. This is to avoid allocating lots of strings
  // that will likely never be used. Each rule is separated by '\n'. Please see the
  // PublicSuffixListGenerator class for how these lists are generated.
  // Guarded by this.
  private byte[] publicSuffixListBytes;
  private byte[] publicSuffixExceptionListBytes;

  /**
   * Returns the effective top-level domain plus one (eTLD+1) by referencing the public suffix list.
   *
   * <p>Here are some examples: <pre>{@code
   * assertEquals("google.com", getEffectiveTldPlusOne("google.com"));
   * assertEquals("google.com", getEffectiveTldPlusOne("www.google.com"));
   * assertNull(getEffectiveTldPlusOne("com"));
   * }</pre>
   */
  public String getEffectiveTldPlusOne(String domain) {
    if (domain == null) return null;

    // Confirm we're dealing with a valid domain.
    String canonicalDomain = Util.domainToAscii(domain);
    if (canonicalDomain == null) return null;

    // Convert it back to Unicode because the list uses Unicode, not punycode form.
    String unicodeDomain = IDN.toUnicode(canonicalDomain);

    // Preserve the passed in domain format (Unicode vs. punycode), but canonicalize it. This
    // satisfies the publicsuffix.org test cases, but it's unclear if this should be done generally.
    String domainToReturn = canonicalDomain.equalsIgnoreCase(domain)
        ? canonicalDomain // We were passed a punycode domain.
        : unicodeDomain; // We were passed a Unicode domain.

    String[] domainLabels = unicodeDomain.split("\\.");
    String[] ruleLabels = findMatchingRule(domainLabels);
    if (ruleLabels == null) {
      // If no rules match, the prevailing rule is '*'.
      ruleLabels = new String[]{" "};
    }

    if (domainLabels.length == ruleLabels.length && ruleLabels[0].charAt(0) != EXCEPTION_MARKER) {
      // The domain is a public suffix.
      return null;
    }

    int labelsToInclude;
    if (ruleLabels[0].charAt(0) == EXCEPTION_MARKER) {
      // Exception rules hold the effective TLD plus one.
      labelsToInclude = domainLabels.length - ruleLabels.length;
    } else {
      // Otherwise the rule is for a public suffix, so we must take one more label.
      labelsToInclude = domainLabels.length - (ruleLabels.length + 1);
    }

    String[] domainToReturnLabels = domainToReturn.split("\\.");
    StringBuilder effectiveTldPlusOne = new StringBuilder();
    for (int i = labelsToInclude; i < domainToReturnLabels.length; i++) {
      effectiveTldPlusOne.append(domainToReturnLabels[i]).append('.');
    }
    effectiveTldPlusOne.deleteCharAt(effectiveTldPlusOne.length() - 1);

    return effectiveTldPlusOne.toString();
  }

  private String[] findMatchingRule(String[] domainLabels) {
    if (!listRead.get() && listRead.compareAndSet(false, true)) {
      readTheList();
    } else {
      try {
        readCompleteLatch.await();
      } catch (InterruptedException ignored) {
      }
    }

    synchronized (this) {
      if (publicSuffixListBytes == null) {
        // We failed to read the public suffix list. This scenario should never happen, but in case
        // it does, we'll let act as though this is a no-op.
        return null;
      }
    }

    // Break apart the domain into UTF-8 labels, i.e. foo.bar.com turns into [foo, bar, com].
    byte[][] domainLabelsUtf8Bytes = new byte[domainLabels.length][];
    for (int i = 0; i < domainLabels.length; i++) {
      domainLabelsUtf8Bytes[i] = domainLabels[i].getBytes(Util.UTF_8);
    }

    // Start by looking for exact matches. We start at the leftmost label. For example, foo.bar.com
    // will look like: [foo, bar, com], [bar, com], [com]. The longest matching rule wins.
    String exactMatch = null;
    for (int i = 0; i < domainLabelsUtf8Bytes.length; i++) {
      String match = binarySearchBytes(publicSuffixListBytes, domainLabelsUtf8Bytes, i);
      if (match != null) {
        exactMatch = match;
        break;
      }
    }

    // Wildcard rules throw a wrench into the mix. They are not restricted to appear in the leftmost
    // position. An example is: *.*.foo. Therefore, we must exhaust the permutations,
    // substituting a wildcard in the various label positions. For example, on the domain
    // foo.bar.com, we'll try: [*, com], [foo, *, com], [*, bar, com], [*, *, com].
    // Note: since we've already tried an exact match, we leave out those permutations.
    String wildcardMatch = null;
    if (domainLabelsUtf8Bytes.length > 1) {
      // It seems highly unlikely (impossible?) the rightmost label will ever be a wildcard.
      byte[][] labelsWithWildcard = new byte[domainLabelsUtf8Bytes.length][];
      labelsWithWildcard[domainLabelsUtf8Bytes.length - 1] =
          domainLabelsUtf8Bytes[domainLabelsUtf8Bytes.length - 1];

      int firstLevel = domainLabelsUtf8Bytes.length - 1;
      outer: for (int currentLevel = 0; currentLevel < firstLevel; currentLevel++) {
        int numberOfWildcards = firstLevel - currentLevel;
        int permutations = 1 << numberOfWildcards;
        for (int i = 1; i < permutations; i++) {
          for (int labelPos = firstLevel - 1; labelPos >= currentLevel; labelPos--) {
            // Use the bit positions to indicate whether we should place a wildcard there. For
            // example with foo.bar.com: 01, 10, 11. The second level is position 1, third is
            // position 2, etc.
            int labelBitPos = 1 << firstLevel - 1 - labelPos;
            int useWildcard = labelBitPos & i;

            labelsWithWildcard[labelPos] = useWildcard > 0
                ? WILDCARD_LABEL
                : domainLabelsUtf8Bytes[labelPos];
          }
          String match = binarySearchBytes(publicSuffixListBytes, labelsWithWildcard, currentLevel);
          if (match != null) {
            wildcardMatch = match;
            break outer;
          }
        }
      }
    }

    // Exception rules only apply to wildcard rules, so only try it if we matched a wildcard.
    String exception = null;
    if (wildcardMatch != null) {
      for (int i = 0; i < domainLabelsUtf8Bytes.length - 1; i++) {
        String rule = binarySearchBytes(publicSuffixExceptionListBytes, domainLabelsUtf8Bytes, i);
        if (rule != null) {
          exception = rule;
          break;
        }
      }
    }

    if (exception != null) {
      // Signal we've identified an exception rule.
      exception = "!" + exception;
      return exception.split("\\.");
    }

    if (exactMatch == null && wildcardMatch == null) return null;

    String[] exactRuleLabels = exactMatch != null
        ? exactMatch.split("\\.")
        : EMPTY_RULE;

    String[] wildcardRuleLabels = wildcardMatch != null
        ? wildcardMatch.split("\\.")
        : EMPTY_RULE;

    return exactRuleLabels.length > wildcardRuleLabels.length
        ? exactRuleLabels
        : wildcardRuleLabels;
  }

  private static String binarySearchBytes(byte[] bytesToSearch, byte[][] labels, int labelIndex) {
    int low = 0;
    int high = bytesToSearch.length;
    String match = null;
    while (low < high) {
      int mid = (low + high) / 2;
      // Search for a '\n' that marks the start of a value. Don't go back past the start of the
      // array.
      while (mid > -1 && bytesToSearch[mid] != '\n') {
        mid--;
      }
      mid++;

      // Now look for the ending '\n'.
      int end = 1;
      while (bytesToSearch[mid + end] != '\n') {
        end++;
      }
      int publicSuffixLength = (mid + end) - mid;

      // Compare the bytes. Note that the file stores UTF-8 encoded bytes, so we must compare the
      // unsigned bytes.
      int compareResult;
      int currentLabelIndex = labelIndex;
      int currentLabelByteIndex = 0;
      int publicSuffixByteIndex = 0;

      boolean expectDot = false;
      while (true) {
        int byte0;
        if (expectDot) {
          byte0 = '.';
          expectDot = false;
        } else {
          byte0 = labels[currentLabelIndex][currentLabelByteIndex] & 0xff;
        }

        int byte1 = bytesToSearch[mid + publicSuffixByteIndex] & 0xff;

        compareResult = byte0 - byte1;
        if (compareResult != 0) break;

        publicSuffixByteIndex++;
        currentLabelByteIndex++;
        if (publicSuffixByteIndex == publicSuffixLength) break;

        if (labels[currentLabelIndex].length == currentLabelByteIndex) {
          // We've exhausted our current label. Either there are more labels to compare, in which
          // case we expect a dot as the next character. Otherwise, we've checked all our labels.
          if (currentLabelIndex == labels.length - 1) {
            break;
          } else {
            currentLabelIndex++;
            currentLabelByteIndex = -1;
            expectDot = true;
          }
        }
      }

      if (compareResult < 0) {
        high = mid - 1;
      } else if (compareResult > 0) {
        low = mid + end + 1;
      } else {
        // We found a match, but are the lengths equal?
        int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
        int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
        for (int i = currentLabelIndex + 1; i < labels.length; i++) {
          labelBytesLeft += labels[i].length;
        }

        if (labelBytesLeft < publicSuffixBytesLeft) {
          high = mid - 1;
        } else if (labelBytesLeft > publicSuffixBytesLeft) {
          low = mid + end + 1;
        } else {
          // Found a match.
          match = new String(bytesToSearch, mid, publicSuffixLength, Util.UTF_8);
          break;
        }
      }
    }
    return match;
  }

  private void readTheList() {
    byte[] publicSuffixListBytes = null;
    byte[] publicSuffixExceptionListBytes = null;

    InputStream is = PublicSuffixDatabase.class.getClassLoader().getResourceAsStream(
        PUBLIC_SUFFIX_RESOURCE);

    if (is != null) {
      Source source = new GzipSource(Okio.source(is));
      BufferedSource bufferedSource = null;
      try {
        bufferedSource = Okio.buffer(source);
        int totalBytes = bufferedSource.readInt();
        publicSuffixListBytes = new byte[totalBytes];
        bufferedSource.readFully(publicSuffixListBytes);

        int totalExceptionBytes = bufferedSource.readInt();
        publicSuffixExceptionListBytes = new byte[totalExceptionBytes];
        bufferedSource.readFully(publicSuffixExceptionListBytes);
      } catch (IOException e) {
        Platform.get().log(Platform.WARN, "Failed to read public suffix list", e);
        publicSuffixListBytes = null;
        publicSuffixExceptionListBytes = null;
      } finally {
        closeQuietly(bufferedSource);
      }
    }

    synchronized (this) {
      this.publicSuffixListBytes = publicSuffixListBytes;
      this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    }

    readCompleteLatch.countDown();
  }

  /** Visible for testing. */
  void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes) {
    this.publicSuffixListBytes = publicSuffixListBytes;
    this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
    listRead.set(true);
    readCompleteLatch.countDown();
  }
}
