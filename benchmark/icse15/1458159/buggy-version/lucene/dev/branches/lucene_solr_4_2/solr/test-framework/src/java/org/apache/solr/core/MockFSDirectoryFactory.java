  Merged /lucene/dev/trunk/lucene/core:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/core:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/benchmark:r1457812
  Merged /lucene/dev/trunk/lucene/benchmark:r1457784
  Merged /lucene/dev/trunk/lucene/spatial:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/spatial:r1457812
  Merged /lucene/dev/trunk/lucene/build.xml:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/build.xml:r1457812
  Merged /lucene/dev/trunk/lucene/join:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/join:r1457812
  Merged /lucene/dev/trunk/lucene/tools:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/tools:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/backwards:r1457812
  Merged /lucene/dev/trunk/lucene/backwards:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/site:r1457812
  Merged /lucene/dev/trunk/lucene/site:r1457784
  Merged /lucene/dev/trunk/lucene/licenses:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/licenses:r1457812
  Merged /lucene/dev/trunk/lucene/memory:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/memory:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/JRE_VERSION_MIGRATION.txt:r1457812
  Merged /lucene/dev/trunk/lucene/JRE_VERSION_MIGRATION.txt:r1457784
  Merged /lucene/dev/trunk/lucene/BUILD.txt:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/BUILD.txt:r1457812
  Merged /lucene/dev/trunk/lucene/suggest:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/suggest:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1457812
  Merged /lucene/dev/trunk/lucene/analysis/icu/src/java/org/apache/lucene/collation/ICUCollationKeyFilterFactory.java:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/analysis:r1457812
  Merged /lucene/dev/trunk/lucene/analysis:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/CHANGES.txt:r1457812
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/grouping:r1457812
  Merged /lucene/dev/trunk/lucene/grouping:r1457784
  Merged /lucene/dev/trunk/lucene/classification/src:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/classification/src:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/classification/build.xml:r1457812
  Merged /lucene/dev/trunk/lucene/classification/build.xml:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/classification/ivy.xml:r1457812
  Merged /lucene/dev/trunk/lucene/classification/ivy.xml:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/classification:r1457812
  Merged /lucene/dev/trunk/lucene/classification:r1457784
  Merged /lucene/dev/trunk/lucene/misc:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/misc:r1457812
  Merged /lucene/dev/trunk/lucene/sandbox:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/sandbox:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/highlighter:r1457812
  Merged /lucene/dev/trunk/lucene/highlighter:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/NOTICE.txt:r1457812
  Merged /lucene/dev/trunk/lucene/NOTICE.txt:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/codecs:r1457812
  Merged /lucene/dev/trunk/lucene/codecs:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/LICENSE.txt:r1457812
  Merged /lucene/dev/trunk/lucene/LICENSE.txt:r1457784
  Merged /lucene/dev/trunk/lucene/ivy-settings.xml:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/ivy-settings.xml:r1457812
  Merged /lucene/dev/branches/branch_4x/lucene/SYSTEM_REQUIREMENTS.txt:r1457812
  Merged /lucene/dev/trunk/lucene/SYSTEM_REQUIREMENTS.txt:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene/MIGRATE.txt:r1457812
  Merged /lucene/dev/trunk/lucene/MIGRATE.txt:r1457784
  Merged /lucene/dev/branches/branch_4x/lucene:r1457812
  Merged /lucene/dev/trunk/lucene:r1457784
  Merged /lucene/dev/branches/branch_4x/dev-tools:r1457812
  Merged /lucene/dev/trunk/dev-tools:r1457784
package org.apache.solr.core;

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

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MockDirectoryWrapper;
import org.apache.lucene.util.LuceneTestCase;

/**
 * Opens a directory with {@link LuceneTestCase#newFSDirectory(File)}
 */
public class MockFSDirectoryFactory extends StandardDirectoryFactory {

  @Override
  public Directory create(String path, DirContext dirContext) throws IOException {
    Directory dir = LuceneTestCase.newFSDirectory(new File(path));
    // we can't currently do this check because of how
    // Solr has to reboot a new Directory sometimes when replicating
    // or rolling back - the old directory is closed and the following
    // test assumes it can open an IndexWriter when that happens - we
    // have a new Directory for the same dir and still an open IW at 
    // this point
    if (dir instanceof MockDirectoryWrapper) {
      ((MockDirectoryWrapper)dir).setAssertNoUnrefencedFilesOnClose(false);
    }
    return dir;
  }
  
  @Override
  public boolean isAbsolute(String path) {
    // TODO: kind of a hack - we don't know what the delegate is, so
    // we treat it as file based since this works on most ephem impls
    return new File(path).isAbsolute();
  }
}
