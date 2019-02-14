  Merged /lucene/dev/trunk/lucene/test-framework:r1477621
  Merged /lucene/dev/trunk/lucene/README.txt:r1477621
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1477621
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1477621
  Merged /lucene/dev/trunk/lucene/suggest:r1477621
  Merged /lucene/dev/trunk/lucene/module-build.xml:r1477621
  Merged /lucene/dev/trunk/lucene/demo:r1477621
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1477621
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortDocValues.java:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTotalHitCountCollector.java:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSort.java:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestSortRandom.java:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/search/TestTopFieldCollector.java:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.cfs.zip:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.cfs.zip:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.nocfs.zip:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/index.40.optimized.nocfs.zip:r1477621
  Merged /lucene/dev/trunk/lucene/core/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java:r1477621
  Merged /lucene/dev/trunk/lucene/core:r1477621
  Merged /lucene/dev/trunk/lucene/highlighter:r1477621
  Merged /lucene/dev/trunk/lucene/sandbox:r1477621
  Merged /lucene/dev/trunk/lucene/join:r1477621
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1477621
  Merged /lucene/dev/trunk/lucene/site:r1477621
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1477621
  Merged /lucene/dev/trunk/lucene/licenses:r1477621
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1477621
  Merged /lucene/dev/trunk/lucene/memory:r1477621
  Merged /lucene/dev/trunk/lucene/queries/src/test/org/apache/lucene/queries/function/TestFunctionQuerySort.java:r1477621
  Merged /lucene/dev/trunk/lucene/queries:r1477621
  Merged /lucene/dev/trunk/lucene/queryparser:r1477621
  Merged /lucene/dev/trunk/lucene/facet:r1477621
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1477621
  Merged /lucene/dev/trunk/lucene/analysis:r1477621
  Merged /lucene/dev/trunk/lucene/grouping:r1477621
  Merged /lucene/dev/trunk/lucene/benchmark:r1477621
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1477621
  Merged /lucene/dev/trunk/lucene/classification/src:r1477621
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1477621
  Merged /lucene/dev/trunk/lucene/classification:r1477621
  Merged /lucene/dev/trunk/lucene/misc:r1477621
  Merged /lucene/dev/trunk/lucene/spatial:r1477621
  Merged /lucene/dev/trunk/lucene/build.xml:r1477621
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1477621
  Merged /lucene/dev/trunk/lucene/codecs:r1477621
  Merged /lucene/dev/trunk/lucene/tools:r1477621
  Merged /lucene/dev/trunk/lucene/backwards:r1477621
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1477621
  Merged /lucene/dev/trunk/lucene:r1477621
  Merged /lucene/dev/trunk/dev-tools:r1477621
  Merged /lucene/dev/trunk/solr/build.xml:r1477621
  Merged /lucene/dev/trunk/solr/NOTICE.txt:r1477621
  Merged /lucene/dev/trunk/solr/LICENSE.txt:r1477621
  Merged /lucene/dev/trunk/solr/contrib:r1477621
  Merged /lucene/dev/trunk/solr/site:r1477621
  Merged /lucene/dev/trunk/solr/SYSTEM_REQUIREMENTS.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpcore-LICENSE-ASL.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpclient-NOTICE.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpclient-LICENSE-ASL.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpcore-NOTICE.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpmime-NOTICE.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses/httpmime-LICENSE-ASL.txt:r1477621
  Merged /lucene/dev/trunk/solr/licenses:r1477621
  Merged /lucene/dev/trunk/solr/test-framework:r1477621
  Merged /lucene/dev/trunk/solr/README.txt:r1477621
  Merged /lucene/dev/trunk/solr/webapp:r1477621
  Merged /lucene/dev/trunk/solr/cloud-dev:r1477621
  Merged /lucene/dev/trunk/solr/common-build.xml:r1477621
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1477621
  Merged /lucene/dev/trunk/solr/scripts:r1477621
  Merged /lucene/dev/trunk/solr/core/src/test/org/apache/solr/core/TestConfig.java:r1477621
package org.apache.solr.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.solr.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class SolrCoreDiscoverer {
  protected static Logger log = LoggerFactory.getLogger(SolrCoreDiscoverer.class);
  
  public final static String CORE_PROP_FILE = "core.properties";
  
  public Map<String, CoreDescriptor> discover(CoreContainer container, File root) throws IOException {
    Map<String, CoreDescriptor> coreDescriptorMap = new HashMap<String, CoreDescriptor>();

    walkFromHere(root, container, coreDescriptorMap);
    
    return coreDescriptorMap;
  }
  
  // Basic recursive tree walking, looking for "core.properties" files. Once one is found, we'll stop going any
  // deeper in the tree.
  //
  private void walkFromHere(File file, CoreContainer container, Map<String,CoreDescriptor> coreDescriptorMap)
      throws IOException {
    log.info("Looking for cores in " + file.getCanonicalPath());
    if (! file.exists()) return;

    for (File childFile : file.listFiles()) {
      // This is a little tricky, we are asking if core.properties exists in a child directory of the directory passed
      // in. In other words we're looking for core.properties in the grandchild directories of the parameter passed
      // in. That allows us to gracefully top recursing deep but continue looking wide.
      File propFile = new File(childFile, CORE_PROP_FILE);
      if (propFile.exists()) { // Stop looking after processing this file!
        addCore(container, childFile, propFile, coreDescriptorMap);
        continue; // Go on to the sibling directory, don't descend any deeper.
      }
      if (childFile.isDirectory()) {
        walkFromHere(childFile, container, coreDescriptorMap);
      }
    }
  }
  
  private void addCore(CoreContainer container, File childFile, File propFile, Map<String,CoreDescriptor> coreDescriptorMap) throws IOException {
    log.info("Discovered properties file {}, adding to cores", propFile.getAbsolutePath());
    Properties propsOrig = new Properties();
    InputStream is = new FileInputStream(propFile);
    try {
      propsOrig.load(is);
    } finally {
      IOUtils.closeQuietly(is);
    }

    Properties props = new Properties();
    for (String prop : propsOrig.stringPropertyNames()) {
      props.put(prop, PropertiesUtil.substituteProperty(propsOrig.getProperty(prop), null));
    }

    // Too much of the code depends on this value being here, but it is NOT supported in discovery mode, so
    // ignore it if present in the core.properties file.
    System.out.println("SET INST DIR:" + childFile.getPath());
    props.setProperty(CoreDescriptor.CORE_INSTDIR, childFile.getPath());

    if (props.getProperty(CoreDescriptor.CORE_NAME) == null) {
      // Should default to this directory
      props.setProperty(CoreDescriptor.CORE_NAME, childFile.getName());
    }
    CoreDescriptor desc = new CoreDescriptor(container, props);
    coreDescriptorMap.put(desc.getName(), desc);
  }
}
