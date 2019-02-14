  Merged /lucene/dev/trunk/lucene/grouping:r1525935
  Merged /lucene/dev/trunk/lucene/benchmark:r1525935
  Merged /lucene/dev/trunk/lucene/misc:r1525935
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1525935
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1525935
  Merged /lucene/dev/trunk/lucene/classification/src:r1525935
  Merged /lucene/dev/trunk/lucene/classification:r1525935
  Merged /lucene/dev/trunk/lucene/spatial:r1525935
  Merged /lucene/dev/trunk/lucene/build.xml:r1525935
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1525935
  Merged /lucene/dev/trunk/lucene/codecs:r1525935
  Merged /lucene/dev/trunk/lucene/tools:r1525935
  Merged /lucene/dev/trunk/lucene/backwards:r1525935
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1525935
  Merged /lucene/dev/trunk/lucene/test-framework:r1525935
  Merged /lucene/dev/trunk/lucene/README.txt:r1525935
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1525935
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1525935
  Merged /lucene/dev/trunk/lucene/suggest:r1525935
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1525935
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1525935
  Merged /lucene/dev/trunk/lucene/demo:r1525935
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1525935
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1525935
  Merged /lucene/dev/trunk/lucene/core:r1525935
  Merged /lucene/dev/trunk/lucene/highlighter:r1525935
  Merged /lucene/dev/trunk/lucene/sandbox:r1525935
  Merged /lucene/dev/trunk/lucene/join:r1525935
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1525935
  Merged /lucene/dev/trunk/lucene/replicator:r1525935
  Merged /lucene/dev/trunk/lucene/site:r1525935
  Merged /lucene/dev/trunk/lucene/licenses:r1525935
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1525935
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1525935
  Merged /lucene/dev/trunk/lucene/memory:r1525935
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1525935
  Merged /lucene/dev/trunk/lucene/queries:r1525935
  Merged /lucene/dev/trunk/lucene/queryparser:r1525935
  Merged /lucene/dev/trunk/lucene/facet:r1525935
  Merged /lucene/dev/trunk/lucene/expressions:r1525935
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1525935
  Merged /lucene/dev/trunk/lucene/analysis:r1525935
  Merged /lucene/dev/trunk/lucene:r1525935
  Merged /lucene/dev/trunk/dev-tools:r1525935
  Merged /lucene/dev/trunk/solr/test-framework:r1525935
  Merged /lucene/dev/trunk/solr/README.txt:r1525935
  Merged /lucene/dev/trunk/solr/webapp:r1525935
  Merged /lucene/dev/trunk/solr/cloud-dev:r1525935
  Merged /lucene/dev/trunk/solr/common-build.xml:r1525935
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1525935
  Merged /lucene/dev/trunk/solr/scripts:r1525935
  Merged /lucene/dev/trunk/solr/core/src/test/org/apache/solr/core/TestConfig.java:r1525935
package org.apache.solr.core;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.Test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
public class TestImplicitCoreProperties extends SolrTestCaseJ4 {

  public static final String SOLRXML =
      "<solr><cores><core name=\"collection1\" instanceDir=\"collection1\" config=\"solrconfig-implicitproperties.xml\"/></cores></solr>";

  @Test
  public void testImplicitPropertiesAreSubstitutedInSolrConfig() {

    CoreContainer cc = createCoreContainer(TEST_HOME(), SOLRXML);
    try {
      cc.load();
      assertQ(req("q", "*:*")
              , "//str[@name='dummy1'][.='collection1']"
              , "//str[@name='dummy2'][.='data/']"
              , "//str[@name='dummy3'][.='solrconfig-implicitproperties.xml']"
              , "//str[@name='dummy4'][.='schema.xml']"
              , "//str[@name='dummy5'][.='false']"
              );
    }
    finally {
      cc.shutdown();
    }

  }

}
