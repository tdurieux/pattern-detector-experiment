  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1488365
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1488365
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1488365
  Merged /lucene/dev/trunk/lucene/test-framework:r1488365
  Merged /lucene/dev/trunk/lucene/README.txt:r1488365
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1488365
  Merged /lucene/dev/trunk/lucene/queries:r1488365
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1488365
  Merged /lucene/dev/trunk/lucene/facet:r1488365
  Merged /lucene/dev/trunk/lucene/queryparser:r1488365
  Merged /lucene/dev/trunk/lucene/demo:r1488365
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1488365
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1488365
  Merged /lucene/dev/trunk/lucene/core:r1488365
  Merged /lucene/dev/trunk/lucene/benchmark:r1488365
  Merged /lucene/dev/trunk/lucene/spatial:r1488365
  Merged /lucene/dev/trunk/lucene/build.xml:r1488365
  Merged /lucene/dev/trunk/lucene/join:r1488365
  Merged /lucene/dev/trunk/lucene/tools:r1488365
  Merged /lucene/dev/trunk/lucene/backwards:r1488365
  Merged /lucene/dev/trunk/lucene/site:r1488365
  Merged /lucene/dev/trunk/lucene/licenses:r1488365
  Merged /lucene/dev/trunk/lucene/memory:r1488365
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1488365
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1488365
  Merged /lucene/dev/trunk/lucene/suggest:r1488365
  Merged /lucene/dev/trunk/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1488365
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1488365
  Merged /lucene/dev/trunk/lucene/analysis:r1488365
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1488365
  Merged /lucene/dev/trunk/lucene/grouping:r1488365
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1488365
  Merged /lucene/dev/trunk/lucene/classification/src:r1488365
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1488365
  Merged /lucene/dev/trunk/lucene/classification:r1488365
  Merged /lucene/dev/trunk/lucene/misc:r1488365
  Merged /lucene/dev/trunk/lucene/sandbox:r1488365
  Merged /lucene/dev/trunk/lucene/highlighter:r1488365
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1488365
  Merged /lucene/dev/trunk/lucene/codecs:r1488365
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1488365
  Merged /lucene/dev/trunk/lucene:r1488365
  Merged /lucene/dev/trunk/dev-tools:r1488365
  Merged /lucene/dev/trunk/solr/scripts:r1488365
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
package org.apache.solr.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.UUID;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.JavaBinCodec;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.response.BinaryQueryResponseWriter;
import org.apache.solr.response.BinaryResponseWriter.Resolver;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.ReturnFields;
import org.apache.solr.search.SolrReturnFields;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;

/**
 * Test for BinaryResponseWriter
 *
 *
 * @since solr 1.4
 */
public class TestBinaryResponseWriter extends AbstractSolrTestCase {

  
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig.xml", "schema12.xml");
  }

  /**
   * Tests known types implementation by asserting correct encoding/decoding of UUIDField
   */
  public void testUUID() throws Exception {
    String s = UUID.randomUUID().toString().toLowerCase(Locale.ROOT);
    assertU(adoc("id", "101", "uuid", s));
    assertU(commit());
    LocalSolrQueryRequest req = lrf.makeRequest("q", "*:*");
    SolrQueryResponse rsp = h.queryAndResponse(req.getParams().get(CommonParams.QT), req);
    BinaryQueryResponseWriter writer = (BinaryQueryResponseWriter) h.getCore().getQueryResponseWriter("javabin");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writer.write(baos, req, rsp);
    NamedList res = (NamedList) new JavaBinCodec().unmarshal(new ByteArrayInputStream(baos.toByteArray()));
    SolrDocumentList docs = (SolrDocumentList) res.get("response");
    for (Object doc : docs) {
      SolrDocument document = (SolrDocument) doc;
      assertEquals("Returned object must be a string", "java.lang.String", document.getFieldValue("uuid").getClass().getName());
      assertEquals("Wrong UUID string returned", s, document.getFieldValue("uuid"));
    }

    req.close();
  }

  public void testResolverSolrDocumentPartialFields() throws Exception {
    LocalSolrQueryRequest req = lrf.makeRequest("q", "*:*",
                                                "fl", "id,xxx,ddd_s"); 
    SolrDocument in = new SolrDocument();
    in.addField("id", 345);
    in.addField("aaa_s", "aaa");
    in.addField("bbb_s", "bbb");
    in.addField("ccc_s", "ccc");
    in.addField("ddd_s", "ddd");
    in.addField("eee_s", "eee");    

    Resolver r = new Resolver(req, new SolrReturnFields(req));
    Object o = r.resolve(in, new JavaBinCodec());

    assertNotNull("obj is null", o);
    assertTrue("obj is not doc", o instanceof SolrDocument);

    SolrDocument out = (SolrDocument) o;
    assertTrue("id not found", out.getFieldNames().contains("id"));
    assertTrue("ddd_s not found", out.getFieldNames().contains("ddd_s"));
    assertEquals("Wrong number of fields found", 
                 2, out.getFieldNames().size());
    req.close();

  }

}
