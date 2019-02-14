  Merged /lucene/dev/trunk/lucene/suggest:r1514494
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1514494
  Merged /lucene/dev/trunk/lucene/analysis:r1514494
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1514494
  Merged /lucene/dev/trunk/lucene/grouping:r1514494
  Merged /lucene/dev/trunk/lucene/misc:r1514494
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1514494
  Merged /lucene/dev/trunk/lucene/classification/src:r1514494
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1514494
  Merged /lucene/dev/trunk/lucene/classification:r1514494
  Merged /lucene/dev/trunk/lucene/highlighter:r1514494
  Merged /lucene/dev/trunk/lucene/sandbox:r1514494
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1514494
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1514494
  Merged /lucene/dev/trunk/lucene/codecs:r1514494
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1514494
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1514494
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1514494
  Merged /lucene/dev/trunk/lucene/test-framework:r1514494
  Merged /lucene/dev/trunk/lucene/README.txt:r1514494
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1514494
  Merged /lucene/dev/trunk/lucene/queries:r1514494
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1514494
  Merged /lucene/dev/trunk/lucene/facet:r1514494
  Merged /lucene/dev/trunk/lucene/queryparser:r1514494
  Merged /lucene/dev/trunk/lucene/demo:r1514494
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1514494
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1514494
  Merged /lucene/dev/trunk/lucene/core:r1514494
  Merged /lucene/dev/trunk/lucene/benchmark:r1514494
  Merged /lucene/dev/trunk/lucene/spatial:r1514494
  Merged /lucene/dev/trunk/lucene/build.xml:r1514494
  Merged /lucene/dev/trunk/lucene/join:r1514494
  Merged /lucene/dev/trunk/lucene/tools:r1514494
  Merged /lucene/dev/trunk/lucene/backwards:r1514494
  Merged /lucene/dev/trunk/lucene/replicator:r1514494
  Merged /lucene/dev/trunk/lucene/site:r1514494
  Merged /lucene/dev/trunk/lucene/licenses:r1514494
  Merged /lucene/dev/trunk/lucene/memory:r1514494
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1514494
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1514494
  Merged /lucene/dev/trunk/lucene:r1514494
  Merged /lucene/dev/trunk/dev-tools:r1514494
  Merged /lucene/dev/trunk/solr/SYSTEM_REQUIREMENTS.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpcore-NOTICE.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpmime-LICENSE-ASL.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpclient-NOTICE.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpmime-NOTICE.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpclient-LICENSE-ASL.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses/httpcore-LICENSE-ASL.txt:r1514494
  Merged /lucene/dev/trunk/solr/licenses:r1514494
  Merged /lucene/dev/trunk/solr/test-framework:r1514494
  Merged /lucene/dev/trunk/solr/README.txt:r1514494
  Merged /lucene/dev/trunk/solr/webapp:r1514494
  Merged /lucene/dev/trunk/solr/cloud-dev:r1514494
  Merged /lucene/dev/trunk/solr/common-build.xml:r1514494
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1514494
  Merged /lucene/dev/trunk/solr/scripts:r1514494
  Merged /lucene/dev/trunk/solr/core/src/test/org/apache/solr/core/TestConfig.java:r1514494
package org.apache.solr.search;

/**
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
/**
 * Thrown by {@link EarlyTerminatingCollector} when the maximum to abort
 * the scoring / collection process early, when the specified maximum number
 * of documents were collected.
 */
public class EarlyTerminatingCollectorException extends RuntimeException {
  private static final long serialVersionUID = 5939241340763428118L;  
  private int numberScanned;
  private int numberCollected;
  
  public EarlyTerminatingCollectorException(int numberCollected, int numberScanned) {
    assert numberCollected <= numberScanned : numberCollected+"<="+numberScanned;
    assert 0 < numberCollected;
    assert 0 < numberScanned;

    this.numberCollected = numberCollected;
    this.numberScanned = numberScanned;
  }
  /**
   * The total number of documents in the index that were "scanned" by 
   * the index when collecting the {@see #getNumberCollected()} documents 
   * that triggered this exception.
   * <p>
   * This number represents the sum of:
   * </p>
   * <ul>
   *  <li>The total number of documents in all AtomicReaders
   *      that were fully exhausted during collection
   *  </li>
   *  <li>The id of the last doc collected in the last AtomicReader
   *      consulted during collection.
   *  </li>
   * </ul>
   **/
  public int getNumberScanned() {
    return numberScanned;
  }
  /**
   * The number of documents collected that resulted in early termination
   */
  public int getNumberCollected() {
    return numberCollected;
  }
}
