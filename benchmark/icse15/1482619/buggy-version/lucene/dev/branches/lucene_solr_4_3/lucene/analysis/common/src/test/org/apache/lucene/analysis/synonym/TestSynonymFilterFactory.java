  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/CHANGES.txt:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1482474
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1482474
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1482527
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1482474
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/core:r1482527
  Merged /lucene/dev/trunk/lucene/core:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/sandbox:r1482527
  Merged /lucene/dev/trunk/lucene/sandbox:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/highlighter:r1482527
  Merged /lucene/dev/trunk/lucene/highlighter:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/join:r1482527
  Merged /lucene/dev/trunk/lucene/join:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/LICENSE.txt:r1482527
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/site:r1482527
  Merged /lucene/dev/trunk/lucene/site:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/licenses:r1482527
  Merged /lucene/dev/trunk/lucene/licenses:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/SYSTEM_REQUIREMENTS.txt:r1482527
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1482474
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/MIGRATE.txt:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/memory:r1482527
  Merged /lucene/dev/trunk/lucene/memory:r1482474
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/queries:r1482527
  Merged /lucene/dev/trunk/lucene/queries:r1482474
  Merged /lucene/dev/trunk/lucene/queryparser:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/queryparser:r1482527
  Merged /lucene/dev/trunk/lucene/facet:r1482474
  Merged /lucene/dev/branches/branch_4x/lucene/facet:r1482527
  Merged /lucene/dev/branches/branch_4x/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1482527
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1482474
package org.apache.lucene.analysis.synonym;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.util.BaseTokenStreamFactoryTestCase;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.StringMockResourceLoader;
import org.apache.lucene.util.Version;

public class TestSynonymFilterFactory extends BaseTokenStreamFactoryTestCase {
  /** test that we can parse and use the solr syn file */
  public void testSynonyms() throws Exception {
    Reader reader = new StringReader("GB");
    TokenStream stream = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
    stream = tokenFilterFactory("Synonym", "synonyms", "synonyms.txt").create(stream);
    assertTrue(stream instanceof SynonymFilter);
    assertTokenStreamContents(stream, 
        new String[] { "GB", "gib", "gigabyte", "gigabytes" },
        new int[] { 1, 0, 0, 0 });
  }
  
  /** test that we can parse and use the solr syn file, with the old impl
   * @deprecated Remove this test in Lucene 5.0 */
  @Deprecated
  public void testSynonymsOld() throws Exception {
    Reader reader = new StringReader("GB");
    TokenStream stream = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
    stream = tokenFilterFactory("Synonym", Version.LUCENE_33, new ClasspathResourceLoader(getClass()),
        "synonyms", "synonyms.txt").create(stream);
    assertTrue(stream instanceof SlowSynonymFilter);
    assertTokenStreamContents(stream, 
        new String[] { "GB", "gib", "gigabyte", "gigabytes" },
        new int[] { 1, 0, 0, 0 });
  }
  
  /** test multiword offsets with the old impl
   * @deprecated Remove this test in Lucene 5.0 */
  @Deprecated
  public void testMultiwordOffsetsOld() throws Exception {
    Reader reader = new StringReader("national hockey league");
    TokenStream stream = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
    stream = tokenFilterFactory("Synonym", Version.LUCENE_33, new StringMockResourceLoader("national hockey league, nhl"),
        "synonyms", "synonyms.txt").create(stream);
    // WTF?
    assertTokenStreamContents(stream, 
        new String[] { "national", "nhl", "hockey", "league" },
        new int[] { 0, 0, 0, 0 },
        new int[] { 22, 22, 22, 22 },
        new int[] { 1, 0, 1, 1 });
  }
  
  /** if the synonyms are completely empty, test that we still analyze correctly */
  public void testEmptySynonyms() throws Exception {
    Reader reader = new StringReader("GB");
    TokenStream stream = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
    stream = tokenFilterFactory("Synonym", TEST_VERSION_CURRENT, 
        new StringMockResourceLoader(""), // empty file!
        "synonyms", "synonyms.txt").create(stream);
    assertTokenStreamContents(stream, new String[] { "GB" });
  }
  
  /** Test that bogus arguments result in exception */
  public void testBogusArguments() throws Exception {
    try {
      tokenFilterFactory("Synonym", 
          "synonyms", "synonyms.txt", 
          "bogusArg", "bogusValue");
      fail();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
  }
}
