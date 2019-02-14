  Merged /lucene/dev/trunk/lucene/licenses:r1449211
  Merged /lucene/dev/trunk/lucene/memory:r1449211
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1449211
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1449211
  Merged /lucene/dev/trunk/lucene/suggest:r1449211
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1449211
  Merged /lucene/dev/trunk/lucene/analysis:r1449211
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1449211
  Merged /lucene/dev/trunk/lucene/grouping:r1449211
  Merged /lucene/dev/trunk/lucene/classification:r1449211
  Merged /lucene/dev/trunk/lucene/misc:r1449211
  Merged /lucene/dev/trunk/lucene/highlighter:r1449211
  Merged /lucene/dev/trunk/lucene/sandbox:r1449211
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1449211
  Merged /lucene/dev/trunk/lucene/codecs:r1449211
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1449211
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1449211
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1449211
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1449211
  Merged /lucene/dev/trunk/lucene/test-framework:r1449211
  Merged /lucene/dev/trunk/lucene/README.txt:r1449211
  Merged /lucene/dev/trunk/lucene/queries:r1449211
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1449211
  Merged /lucene/dev/trunk/lucene/queryparser:r1449211
  Merged /lucene/dev/trunk/lucene/facet:r1449211
  Merged /lucene/dev/trunk/lucene/demo:r1449211
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1449211
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1449211
  Merged /lucene/dev/trunk/lucene/core:r1449211
  Merged /lucene/dev/trunk/lucene/benchmark:r1449211
  Merged /lucene/dev/trunk/lucene/spatial:r1449211
  Merged /lucene/dev/trunk/lucene/build.xml:r1449211
  Merged /lucene/dev/trunk/lucene/join:r1449211
  Merged /lucene/dev/trunk/lucene/tools:r1449211
  Merged /lucene/dev/trunk/lucene/backwards:r1449211
  Merged /lucene/dev/trunk/lucene/site:r1449211
  Merged /lucene/dev/trunk/lucene:r1449211
  Merged /lucene/dev/trunk/dev-tools:r1449211
  Merged /lucene/dev/trunk/solr/site:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpcore-NOTICE.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpcore-LICENSE-ASL.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpmime-NOTICE.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpclient-LICENSE-ASL.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpclient-NOTICE.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses/httpmime-LICENSE-ASL.txt:r1449211
  Merged /lucene/dev/trunk/solr/licenses:r1449211
  Merged /lucene/dev/trunk/solr/SYSTEM_REQUIREMENTS.txt:r1449211
  Merged /lucene/dev/trunk/solr/test-framework:r1449211
  Merged /lucene/dev/trunk/solr/README.txt:r1449211
  Merged /lucene/dev/trunk/solr/webapp:r1449211
  Merged /lucene/dev/trunk/solr/testlogging.properties:r1449211
  Merged /lucene/dev/trunk/solr/cloud-dev:r1449211
  Merged /lucene/dev/trunk/solr/common-build.xml:r1449211
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1449211
  Merged /lucene/dev/trunk/solr/scripts:r1449211
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

package org.apache.solr.core;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.handler.admin.ShowFileRequestHandler;
import org.apache.solr.update.DirectUpdateHandler2;
import org.apache.solr.update.SolrIndexConfig;
import org.apache.solr.util.RefCounted;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class TestConfig extends SolrTestCaseJ4 {

  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig-termindex.xml","schema-reversed.xml");
  }

  @Test
  public void testLib() throws IOException {
    SolrResourceLoader loader = h.getCore().getResourceLoader();
    InputStream data = null;
    String[] expectedFiles = new String[] { "empty-file-main-lib.txt",
            "empty-file-a1.txt",
            "empty-file-a2.txt",
            "empty-file-b1.txt",
            "empty-file-b2.txt",
            "empty-file-c1.txt" };
    for (String f : expectedFiles) {
      data = loader.openResource(f);
      assertNotNull("Should have found file " + f, data);
      data.close();
    }
    String[] unexpectedFiles = new String[] { "empty-file-c2.txt",
            "empty-file-d2.txt" };
    for (String f : unexpectedFiles) {
      data = null;
      try {
        data = loader.openResource(f);
      } catch (Exception e) { /* :NOOP: (un)expected */ }
      assertNull("should not have been able to find " + f, data);
    }
  }

  @Test
  public void testJavaProperty() {
    // property values defined in build.xml

    String s = solrConfig.get("propTest");
    assertEquals("prefix-proptwo-suffix", s);

    s = solrConfig.get("propTest/@attr1", "default");
    assertEquals("propone-${literal}", s);

    s = solrConfig.get("propTest/@attr2", "default");
    assertEquals("default-from-config", s);

    s = solrConfig.get("propTest[@attr2='default-from-config']", "default");
    assertEquals("prefix-proptwo-suffix", s);

    NodeList nl = (NodeList) solrConfig.evaluate("propTest", XPathConstants.NODESET);
    assertEquals(1, nl.getLength());
    assertEquals("prefix-proptwo-suffix", nl.item(0).getTextContent());

    Node node = solrConfig.getNode("propTest", true);
    assertEquals("prefix-proptwo-suffix", node.getTextContent());
  }

  @Test
  public void testLucene23Upgrades() throws Exception {
    double bufferSize = solrConfig.indexConfig.ramBufferSizeMB;
    assertTrue(bufferSize + " does not equal: " + 100, bufferSize == 100);
    String mergePolicy = solrConfig.indexConfig.mergePolicyInfo.className;
    assertEquals(TieredMergePolicy.class.getName(), mergePolicy);
    String mergeSched = solrConfig.indexConfig.mergeSchedulerInfo.className;
    assertTrue(mergeSched + " is not equal to " + SolrIndexConfig.DEFAULT_MERGE_SCHEDULER_CLASSNAME, mergeSched.equals(SolrIndexConfig.DEFAULT_MERGE_SCHEDULER_CLASSNAME) == true);
  }

  // sometime if the config referes to old things, it must be replaced with new stuff
  @Test
  public void testAutomaticDeprecationSupport() {
    // make sure the "admin/file" handler is registered
    ShowFileRequestHandler handler = (ShowFileRequestHandler) h.getCore().getRequestHandler("/admin/file");
    assertTrue("file handler should have been automatically registered", handler != null);

    //System.out.println( handler.getHiddenFiles() );
    // should not contain: <gettableFiles>solrconfig.xml scheam.xml admin-extra.html</gettableFiles>
    assertFalse(handler.getHiddenFiles().contains("scheam.xml".toUpperCase(Locale.ROOT)));
    assertTrue(handler.getHiddenFiles().contains("PROTWORDS.TXT"));
  }

  @Test
  public void testTermIndexInterval() throws Exception {
    RefCounted<IndexWriter> iw = ((DirectUpdateHandler2) h.getCore()
        .getUpdateHandler()).getSolrCoreState().getIndexWriter(h.getCore());
    int interval = 0;
    try {
      IndexWriter writer = iw.get();
      interval = writer.getConfig().getTermIndexInterval();
    } finally {
      iw.decref();
    }
    assertEquals(256, interval);
  }

  @Test
  public void testTermIndexDivisor() throws Exception {
    IndexReaderFactory irf = h.getCore().getIndexReaderFactory();
    StandardIndexReaderFactory sirf = (StandardIndexReaderFactory) irf;
    assertEquals(12, sirf.termInfosIndexDivisor);
  }

  // If defaults change, add test methods to cover each version
  @Test
  public void testDefaults() throws Exception {
    SolrConfig sc = new SolrConfig(new SolrResourceLoader("solr/collection1"), "solrconfig-basic.xml", null);
    SolrIndexConfig sic = sc.indexConfig;
    assertTrue("default ramBufferSizeMB should be 100", sic.ramBufferSizeMB == 100);
    assertTrue("default useCompoundFile should be false", sic.useCompoundFile == false);
    assertTrue("default LockType should be native", sic.lockType.equals(SolrIndexConfig.LOCK_TYPE_NATIVE));
  }

}


