  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/client:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/update/AutoCommitTest.java:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/analysis/TestTrimFilter.java:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/analysis/TestRemoveDuplicatesTokenFilter.java:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/analysis/TestSynonymFilter.java:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/analysis/TestShingleFilterFactory.java:r1026460,1026606,1026610,1026868
  Merged /lucene/dev/trunk/solr/src/test/org/apache/solr/analysis/TestPatternTokenizerFactory.java:r1026460,1026606,1026610,1026868
package org.apache.solr.core;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.update.DirectUpdateHandler2;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLegacyMergeSchedulerPolicyConfig extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig-legacy.xml", "schema.xml");
  }

  @Test
  public void testLegacy() throws Exception {
    IndexWriter writer = new ExposeWriterHandler().getWriter();
    assertTrue(writer.getMergePolicy().getClass().getName().equals(LogDocMergePolicy.class.getName()));
    assertTrue(writer.getMergeScheduler().getClass().getName().equals(SerialMergeScheduler.class.getName()));
  }
  
  class ExposeWriterHandler extends DirectUpdateHandler2 {
    public ExposeWriterHandler() throws IOException {
      super(h.getCore());
    }

    public IndexWriter getWriter() throws IOException {
      forceOpenWriter();
      return writer;
    }
  }
}
