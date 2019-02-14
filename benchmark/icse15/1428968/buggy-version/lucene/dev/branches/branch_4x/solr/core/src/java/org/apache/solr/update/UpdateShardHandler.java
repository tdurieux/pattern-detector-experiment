  Merged /lucene/dev/trunk/lucene/spatial:r1428963
  Merged /lucene/dev/trunk/lucene/build.xml:r1428963
  Merged /lucene/dev/trunk/lucene/join:r1428963
  Merged /lucene/dev/trunk/lucene/tools:r1428963
  Merged /lucene/dev/trunk/lucene/backwards:r1428963
  Merged /lucene/dev/trunk/lucene/site:r1428963
  Merged /lucene/dev/trunk/lucene/licenses:r1428963
  Merged /lucene/dev/trunk/lucene/memory:r1428963
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1428963
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1428963
  Merged /lucene/dev/trunk/lucene/suggest:r1428963
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1428963
  Merged /lucene/dev/trunk/lucene/analysis:r1428963
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1428963
  Merged /lucene/dev/trunk/lucene/grouping:r1428963
  Merged /lucene/dev/trunk/lucene/misc:r1428963
  Merged /lucene/dev/trunk/lucene/sandbox:r1428963
  Merged /lucene/dev/trunk/lucene/highlighter:r1428963
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1428963
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1428963
  Merged /lucene/dev/trunk/lucene/codecs:r1428963
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1428963
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1428963
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1428963
  Merged /lucene/dev/trunk/lucene/test-framework:r1428963
  Merged /lucene/dev/trunk/lucene/README.txt:r1428963
  Merged /lucene/dev/trunk/lucene/queries:r1428963
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1428963
  Merged /lucene/dev/trunk/lucene/queryparser:r1428963
  Merged /lucene/dev/trunk/lucene/facet:r1428963
  Merged /lucene/dev/trunk/lucene/demo:r1428963
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1428963
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1428963
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1428963
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1428963
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1428963
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1428963
  Merged /lucene/dev/trunk/lucene/core:r1428963
  Merged /lucene/dev/trunk/lucene/benchmark:r1428963
  Merged /lucene/dev/trunk/lucene:r1428963
  Merged /lucene/dev/trunk/dev-tools:r1428963
  Merged /lucene/dev/trunk/solr/test-framework:r1428963
  Merged /lucene/dev/trunk/solr/README.txt:r1428963
  Merged /lucene/dev/trunk/solr/webapp:r1428963
  Merged /lucene/dev/trunk/solr/testlogging.properties:r1428963
  Merged /lucene/dev/trunk/solr/cloud-dev:r1428963
  Merged /lucene/dev/trunk/solr/common-build.xml:r1428963
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1428963
  Merged /lucene/dev/trunk/solr/scripts:r1428963
package org.apache.solr.update;

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

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.ExecutorUtil;
import org.apache.solr.util.DefaultSolrThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateShardHandler {
  
  private static Logger log = LoggerFactory.getLogger(UpdateShardHandler.class);
  
  private ThreadPoolExecutor cmdDistribExecutor = new ThreadPoolExecutor(0,
      Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
      new DefaultSolrThreadFactory("cmdDistribExecutor"));
  
  private final HttpClient client;

  public UpdateShardHandler(int distribUpdateConnTimeout, int distribUpdateSoTimeout) {
    ModifiableSolrParams params = new ModifiableSolrParams();
    params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 500);
    params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 16);
    params.set(HttpClientUtil.PROP_SO_TIMEOUT, distribUpdateConnTimeout);
    params.set(HttpClientUtil.PROP_CONNECTION_TIMEOUT, distribUpdateSoTimeout);
    client = HttpClientUtil.createClient(params);
  }
  
  
  public HttpClient getHttpClient() {
    return client;
  }
  
  public ThreadPoolExecutor getCmdDistribExecutor() {
    return cmdDistribExecutor;
  }

  public void close() {
    try {
      ExecutorUtil.shutdownNowAndAwaitTermination(cmdDistribExecutor);
    } catch (Throwable e) {
      SolrException.log(log, e);
    }
    client.getConnectionManager().shutdown();
  }
}
