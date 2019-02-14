  Merged /lucene/dev/trunk/lucene/spatial:r1542436
  Merged /lucene/dev/trunk/lucene/build.xml:r1542436
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1542436
  Merged /lucene/dev/trunk/lucene/codecs:r1542436
  Merged /lucene/dev/trunk/lucene/tools:r1542436
  Merged /lucene/dev/trunk/lucene/backwards:r1542436
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1542436
  Merged /lucene/dev/trunk/lucene/test-framework:r1542436
  Merged /lucene/dev/trunk/lucene/README.txt:r1542436
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1542436
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1542436
  Merged /lucene/dev/trunk/lucene/suggest:r1542436
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1542436
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1542436
  Merged /lucene/dev/trunk/lucene/demo:r1542436
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1542436
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1542436
  Merged /lucene/dev/trunk/lucene/core:r1542436
  Merged /lucene/dev/trunk/lucene/highlighter:r1542436
  Merged /lucene/dev/trunk/lucene/sandbox:r1542436
  Merged /lucene/dev/trunk/lucene/join:r1542436
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1542436
  Merged /lucene/dev/trunk/lucene/site:r1542436
  Merged /lucene/dev/trunk/lucene/replicator:r1542436
  Merged /lucene/dev/trunk/lucene/licenses:r1542436
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1542436
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1542436
  Merged /lucene/dev/trunk/lucene/memory:r1542436
  Merged /lucene/dev/trunk/lucene/ivy-versions.properties:r1542436
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1542436
  Merged /lucene/dev/trunk/lucene/queries:r1542436
  Merged /lucene/dev/trunk/lucene/queryparser:r1542436
  Merged /lucene/dev/trunk/lucene/facet:r1542436
  Merged /lucene/dev/trunk/lucene/expressions:r1542436
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1542436
  Merged /lucene/dev/trunk/lucene/analysis:r1542436
  Merged /lucene/dev/trunk/lucene/grouping:r1542436
  Merged /lucene/dev/trunk/lucene/benchmark:r1542436
  Merged /lucene/dev/trunk/lucene/misc:r1542436
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1542436
  Merged /lucene/dev/trunk/lucene/classification/src:r1542436
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1542436
  Merged /lucene/dev/trunk/lucene/classification:r1542436
  Merged /lucene/dev/trunk/lucene:r1542436
  Merged /lucene/dev/trunk/dev-tools:r1542436
  Merged /lucene/dev/trunk/solr/test-framework:r1542436
  Merged /lucene/dev/trunk/solr/README.txt:r1542436
  Merged /lucene/dev/trunk/solr/webapp:r1542436
  Merged /lucene/dev/trunk/solr/cloud-dev:r1542436
  Merged /lucene/dev/trunk/solr/common-build.xml:r1542436
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1542436
  Merged /lucene/dev/trunk/solr/scripts:r1542436
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

package org.apache.solr.schema;

import com.carrotsearch.randomizedtesting.rules.SystemPropertiesRestoreRule;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import java.io.File;
import java.util.ArrayList;

public class ModifyConfFileTest extends SolrTestCaseJ4 {
  private File solrHomeDirectory = new File(TEMP_DIR, this.getClass().getName());
  @Rule
  public TestRule solrTestRules = RuleChain.outerRule(new SystemPropertiesRestoreRule());

  private CoreContainer init() throws Exception {
    System.setProperty("solr.test.sys.prop1", "propone");
    System.setProperty("solr.test.sys.prop2", "proptwo");

    if (solrHomeDirectory.exists()) {
      FileUtils.deleteDirectory(solrHomeDirectory);
    }
    assertTrue("Failed to mkdirs workDir", solrHomeDirectory.mkdirs());

    copySolrHomeToTemp(solrHomeDirectory, "core1", true);
    FileUtils.write(new File(new File(solrHomeDirectory, "core1"), "core.properties"), "", Charsets.UTF_8.toString());
    final CoreContainer cores = new CoreContainer(solrHomeDirectory.getAbsolutePath());
    cores.load();
    return cores;
  }

  @Test
  public void testConfigWrite() throws Exception {

    final CoreContainer cc = init();
    try {
      //final CoreAdminHandler admin = new CoreAdminHandler(cc);

      SolrCore core = cc.getCore("core1");
      SolrQueryResponse rsp = new SolrQueryResponse();
      SolrRequestHandler handler = core.getRequestHandler("/admin/file");

      ModifiableSolrParams params = params("file","schema.xml", "op","write");
      core.execute(handler, new LocalSolrQueryRequest(core, params), rsp);
      assertEquals(rsp.getException().getMessage(), "Input stream list was null for admin file write operation.");

      params = params("op", "write", "stream.body", "Testing rewrite of schema.xml file.");
      core.execute(handler, new LocalSolrQueryRequest(core, params), rsp);
      assertEquals(rsp.getException().getMessage(), "No file name specified for write operation.");


      params = params("op", "write", "file", "bogus.txt");
      core.execute(handler, new LocalSolrQueryRequest(core, params), rsp);
      assertEquals(rsp.getException().getMessage(), "Can not access: bogus.txt");

      ArrayList<ContentStream> streams = new ArrayList<ContentStream>( 2 );
      streams.add( new ContentStreamBase.StringStream( "Testing rewrite of schema.xml file." ) );
      //streams.add( new ContentStreamBase.StringStream( "there" ) );

      params = params("op", "write", "file", "schema.xml", "stream.body", "Testing rewrite of schema.xml file.");
      LocalSolrQueryRequest locReq = new LocalSolrQueryRequest(core, params);
      locReq.setContentStreams(streams);
      core.execute(handler, locReq, rsp);

      String contents = FileUtils.readFileToString(new File(core.getCoreDescriptor().getInstanceDir(), "conf/schema.xml"));
      assertEquals("Schema contents should have changed!", "Testing rewrite of schema.xml file.", contents);

      streams.add(new ContentStreamBase.StringStream("This should barf"));
      locReq = new LocalSolrQueryRequest(core, params);
      locReq.setContentStreams(streams);
      core.execute(handler, locReq, rsp);
      assertEquals(rsp.getException().getMessage(), "More than one input stream was found for admin file write operation.");

      streams.clear();
      streams.add(new ContentStreamBase.StringStream("Some bogus stuff for a test."));
      params = params("op", "write", "file", "velocity/test.vm");
      locReq = new LocalSolrQueryRequest(core, params);
      locReq.setContentStreams(streams);
      core.execute(handler, locReq, rsp);
      contents = FileUtils.readFileToString(new File(core.getCoreDescriptor().getInstanceDir(),
          "conf/velocity/test.vm"));
      assertEquals("Schema contents should have changed!", "Some bogus stuff for a test.", contents);

      core.close();
    } finally {
      cc.shutdown();
      if (solrHomeDirectory.exists()) {
        FileUtils.deleteDirectory(solrHomeDirectory);
      }
    }

  }
}
