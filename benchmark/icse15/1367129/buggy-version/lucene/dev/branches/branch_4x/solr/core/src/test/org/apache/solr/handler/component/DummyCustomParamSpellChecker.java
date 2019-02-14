  Merged /lucene/dev/trunk/lucene/facet:r1367125
  Merged /lucene/dev/trunk/lucene/queryparser:r1367125
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1367125
  Merged /lucene/dev/trunk/lucene/analysis:r1367125
  Merged /lucene/dev/trunk/lucene/demo:r1367125
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1367125
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1367125
  Merged /lucene/dev/trunk/lucene/core:r1367125
  Merged /lucene/dev/trunk/lucene/benchmark:r1367125
  Merged /lucene/dev/trunk/lucene/grouping:r1367125
  Merged /lucene/dev/trunk/lucene/misc:r1367125
  Merged /lucene/dev/trunk/lucene/spatial:r1367125
  Merged /lucene/dev/trunk/lucene/sandbox:r1367125
  Merged /lucene/dev/trunk/lucene/highlighter:r1367125
  Merged /lucene/dev/trunk/lucene/build.xml:r1367125
  Merged /lucene/dev/trunk/lucene/join:r1367125
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1367125
  Merged /lucene/dev/trunk/lucene/tools:r1367125
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1367125
  Merged /lucene/dev/trunk/lucene/backwards:r1367125
  Merged /lucene/dev/trunk/lucene/site:r1367125
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1367125
  Merged /lucene/dev/trunk/lucene/licenses:r1367125
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1367125
  Merged /lucene/dev/trunk/lucene/memory:r1367125
  Merged /lucene/dev/trunk/lucene/test-framework:r1367125
  Merged /lucene/dev/trunk/lucene/README.txt:r1367125
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1367125
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1367125
  Merged /lucene/dev/trunk/lucene/queries:r1367125
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1367125
  Merged /lucene/dev/trunk/lucene/suggest:r1367125
  Merged /lucene/dev/trunk/lucene:r1367125
  Merged /lucene/dev/trunk/dev-tools:r1367125
  Merged /lucene/dev/trunk/solr/build.xml:r1367125
  Merged /lucene/dev/trunk/solr/NOTICE.txt:r1367125
  Merged /lucene/dev/trunk/solr/LICENSE.txt:r1367125
  Merged /lucene/dev/trunk/solr/contrib:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpclient-NOTICE.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpcore-LICENSE-ASL.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpclient-LICENSE-ASL.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpcore-NOTICE.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpmime-NOTICE.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses/httpmime-LICENSE-ASL.txt:r1367125
  Merged /lucene/dev/trunk/solr/licenses:r1367125
  Merged /lucene/dev/trunk/solr/lib:r1367125
  Merged /lucene/dev/trunk/solr/test-framework:r1367125
  Merged /lucene/dev/trunk/solr/README.txt:r1367125
  Merged /lucene/dev/trunk/solr/dev-tools:r1367125
  Merged /lucene/dev/trunk/solr/webapp:r1367125
  Merged /lucene/dev/trunk/solr/testlogging.properties:r1367125
  Merged /lucene/dev/trunk/solr/cloud-dev:r1367125
  Merged /lucene/dev/trunk/solr/common-build.xml:r1367125
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1367125
  Merged /lucene/dev/trunk/solr/scripts:r1367125
package org.apache.solr.handler.component;

import org.apache.lucene.analysis.Token;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.spelling.SolrSpellChecker;
import org.apache.solr.spelling.SpellingOptions;
import org.apache.solr.spelling.SpellingResult;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
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


/**
 * A Dummy SpellChecker for testing purposes
 *
 **/
public class DummyCustomParamSpellChecker extends SolrSpellChecker {

  @Override
  public void reload(SolrCore core, SolrIndexSearcher searcher) throws IOException {

  }

  @Override
  public void build(SolrCore core, SolrIndexSearcher searcher) throws IOException {

  }

  @Override
  public SpellingResult getSuggestions(SpellingOptions options) throws IOException {

    SpellingResult result = new SpellingResult();
    //just spit back out the results
    Iterator<String> iterator = options.customParams.getParameterNamesIterator();
    int i = 0;
    while (iterator.hasNext()){
      String name = iterator.next();
      String value = options.customParams.get(name);
      result.add(new Token(name, i++, i++),  Collections.singletonList(value));
    }    
    return result;
  }
}
