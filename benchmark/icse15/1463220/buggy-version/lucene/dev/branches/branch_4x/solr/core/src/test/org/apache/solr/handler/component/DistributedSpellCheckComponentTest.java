  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1463219
package org.apache.solr.handler.component;

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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.lucene.util.LuceneTestCase.Slow;
import org.apache.solr.BaseDistributedSearchTestCase;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SpellingParams;
import org.apache.solr.common.util.NamedList;
import org.junit.BeforeClass;

/**
 * Test for SpellCheckComponent's distributed querying
 *
 * @since solr 1.5
 *
 * @see org.apache.solr.handler.component.SpellCheckComponent
 */
@Slow
public class DistributedSpellCheckComponentTest extends BaseDistributedSearchTestCase {
  
  private String requestHandlerName;
  private String reqHandlerWithWordbreak;
  
  public DistributedSpellCheckComponentTest()
  {
    //fixShardCount=true;
    //shardCount=2;
    //stress=0;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    useFactory(null); // need an FS factory
  }

  @Override
  public void setUp() throws Exception {
    if(random().nextBoolean()) {
      requestHandlerName = "spellCheckCompRH";
      reqHandlerWithWordbreak = "spellCheckWithWordbreak";      
    } else {
      requestHandlerName = "spellCheckCompRH_Direct";
      reqHandlerWithWordbreak = "spellCheckWithWordbreak_Direct";
    }  
    super.setUp();
  }
  
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }
  
  private void q(Object... q) throws Exception {
    final ModifiableSolrParams params = new ModifiableSolrParams();

    for (int i = 0; i < q.length; i += 2) {
      params.add(q[i].toString(), q[i + 1].toString());
    }

    controlClient.query(params);

    // query a random server
    params.set("shards", shards);
    int which = r.nextInt(clients.size());
    SolrServer client = clients.get(which);
    client.query(params);
  }
  
  @Override
  public void validateControlData(QueryResponse control) throws Exception
  {    
    NamedList nl = control.getResponse();
    NamedList sc = (NamedList) nl.get("spellcheck");
    NamedList sug = (NamedList) sc.get("suggestions");
    if(sug.size()==0) {
      Assert.fail("Control data did not return any suggestions.");
    }
  }
  
  @Override
  public void doTest() throws Exception {
    del("*:*");
    index(id, "1", "lowerfilt", "toyota");
    index(id, "2", "lowerfilt", "chevrolet");
    index(id, "3", "lowerfilt", "suzuki");
    index(id, "4", "lowerfilt", "ford");
    index(id, "5", "lowerfilt", "ferrari");
    index(id, "6", "lowerfilt", "jaguar");
    index(id, "7", "lowerfilt", "mclaren");
    index(id, "8", "lowerfilt", "sonata");
    index(id, "9", "lowerfilt", "The quick red fox jumped over the lazy brown dogs.");
    index(id, "10", "lowerfilt", "blue");
    index(id, "12", "lowerfilt", "glue");
    index(id, "13", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "14", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "15", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "16", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "17", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "18", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "19", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "20", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "21", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "22", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "23", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "24", "lowerfilt", "The quote red fox jumped over the lazy brown dogs.");
    index(id, "25", "lowerfilt", "rod fix");
    commit();

    handle.clear();
    handle.put("QTime", SKIPVAL);
    handle.put("timestamp", SKIPVAL);
    handle.put("maxScore", SKIPVAL);
    // we care only about the spellcheck results
    handle.put("response", SKIP);
        
    q("q", "*:*", "spellcheck", "true", SpellingParams.SPELLCHECK_BUILD, "true", "qt", "spellCheckCompRH", "shards.qt", "spellCheckCompRH");
    
    query("q", "*:*", "fl", "id,lowerfilt", "spellcheck.q","toyata", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName);
    query("q", "*:*", "fl", "id,lowerfilt", "spellcheck.q","toyata", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true");
    query("q", "*:*", "fl", "id,lowerfilt", "spellcheck.q","bluo", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true", SpellingParams.SPELLCHECK_COUNT, "4");
    query("q", "The quick reb fox jumped over the lazy brown dogs", "fl", "id,lowerfilt", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true", SpellingParams.SPELLCHECK_COUNT, "4", SpellingParams.SPELLCHECK_COLLATE, "true");

    query("q", "lowerfilt:(+quock +reb)", "fl", "id,lowerfilt", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true", SpellingParams.SPELLCHECK_COUNT, "10", SpellingParams.SPELLCHECK_COLLATE, "true", SpellingParams.SPELLCHECK_MAX_COLLATION_TRIES, "10", SpellingParams.SPELLCHECK_MAX_COLLATIONS, "10", SpellingParams.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "true");
    query("q", "lowerfilt:(+quock +reb)", "fl", "id,lowerfilt", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true", SpellingParams.SPELLCHECK_COUNT, "10", SpellingParams.SPELLCHECK_COLLATE, "true", SpellingParams.SPELLCHECK_MAX_COLLATION_TRIES, "10", SpellingParams.SPELLCHECK_MAX_COLLATIONS, "10", SpellingParams.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "false");
    query("q", "lowerfilt:(+quock +reb)", "fl", "id,lowerfilt", "spellcheck", "true", "qt", requestHandlerName, "shards.qt", requestHandlerName, SpellingParams.SPELLCHECK_EXTENDED_RESULTS, "true", SpellingParams.SPELLCHECK_COUNT, "10", SpellingParams.SPELLCHECK_COLLATE, "true", SpellingParams.SPELLCHECK_MAX_COLLATION_TRIES, "0", SpellingParams.SPELLCHECK_MAX_COLLATIONS, "1", SpellingParams.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "false");
  
    query("q", "lowerfilt:(\"quote red fox\")", "fl", "id,lowerfilt", "spellcheck", "true", "qt", "spellCheckCompRH", "shards.qt", "spellCheckCompRH", SpellCheckComponent.SPELLCHECK_EXTENDED_RESULTS, "true", SpellCheckComponent.SPELLCHECK_COUNT, "10", SpellCheckComponent.SPELLCHECK_COLLATE, "true", SpellCheckComponent.SPELLCHECK_MAX_COLLATION_TRIES, "10", SpellCheckComponent.SPELLCHECK_MAX_COLLATIONS, "1", SpellCheckComponent.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "true", SpellCheckComponent.SPELLCHECK_ALTERNATIVE_TERM_COUNT, "5", SpellCheckComponent.SPELLCHECK_MAX_RESULTS_FOR_SUGGEST, "10");
    query("q", "lowerfilt:(\"rod fix\")", "fl", "id,lowerfilt", "spellcheck", "true", "qt", "spellCheckCompRH", "shards.qt", "spellCheckCompRH", SpellCheckComponent.SPELLCHECK_EXTENDED_RESULTS, "true", SpellCheckComponent.SPELLCHECK_COUNT, "10", SpellCheckComponent.SPELLCHECK_COLLATE, "true", SpellCheckComponent.SPELLCHECK_MAX_COLLATION_TRIES, "10", SpellCheckComponent.SPELLCHECK_MAX_COLLATIONS, "1", SpellCheckComponent.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "true", SpellCheckComponent.SPELLCHECK_ALTERNATIVE_TERM_COUNT, "5", SpellCheckComponent.SPELLCHECK_MAX_RESULTS_FOR_SUGGEST, "10");
  
    query("q", "lowerfilt:(+quock +redfox +jum +ped)", "fl", "id,lowerfilt", "spellcheck", "true", "qt", reqHandlerWithWordbreak, "shards.qt", reqHandlerWithWordbreak, SpellCheckComponent.SPELLCHECK_EXTENDED_RESULTS, "true", SpellCheckComponent.SPELLCHECK_COUNT, "10", SpellCheckComponent.SPELLCHECK_COLLATE, "true", SpellCheckComponent.SPELLCHECK_MAX_COLLATION_TRIES, "0", SpellCheckComponent.SPELLCHECK_MAX_COLLATIONS, "1", SpellCheckComponent.SPELLCHECK_COLLATE_EXTENDED_RESULTS, "true");
  }
}
