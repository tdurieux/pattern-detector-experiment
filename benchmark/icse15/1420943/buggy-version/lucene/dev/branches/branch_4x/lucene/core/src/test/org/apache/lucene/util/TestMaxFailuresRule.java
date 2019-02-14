  Merged /lucene/dev/trunk/solr/test-framework:r1420941
  Merged /lucene/dev/trunk/solr/README.txt:r1420941
  Merged /lucene/dev/trunk/solr/webapp:r1420941
  Merged /lucene/dev/trunk/solr/testlogging.properties:r1420941
  Merged /lucene/dev/trunk/solr/cloud-dev:r1420941
  Merged /lucene/dev/trunk/solr/common-build.xml:r1420941
  Merged /lucene/dev/trunk/solr/CHANGES.txt:r1420941
  Merged /lucene/dev/trunk/solr/scripts:r1420941
  Merged /lucene/dev/trunk/solr/core:r1420941
  Merged /lucene/dev/trunk/solr/solrj:r1420941
  Merged /lucene/dev/trunk/solr/example:r1420941
  Merged /lucene/dev/trunk/solr/build.xml:r1420941
  Merged /lucene/dev/trunk/solr/NOTICE.txt:r1420941
  Merged /lucene/dev/trunk/solr/LICENSE.txt:r1420941
  Merged /lucene/dev/trunk/solr/contrib:r1420941
  Merged /lucene/dev/trunk/solr/site:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpcore-NOTICE.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpmime-NOTICE.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpclient-LICENSE-ASL.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpmime-LICENSE-ASL.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpcore-LICENSE-ASL.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses/httpclient-NOTICE.txt:r1420941
  Merged /lucene/dev/trunk/solr/licenses:r1420941
  Merged /lucene/dev/trunk/solr/SYSTEM_REQUIREMENTS.txt:r1420941
  Merged /lucene/dev/trunk/solr:r1420941
  Merged /lucene/dev/trunk/lucene/common-build.xml:r1420941
  Merged /lucene/dev/trunk/lucene/demo:r1420941
  Merged /lucene/dev/trunk/lucene/CHANGES.txt:r1420941
package org.apache.lucene.util;

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

import org.apache.lucene.util.junitcompat.WithNestedTests;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.carrotsearch.randomizedtesting.SysGlobals;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import com.carrotsearch.randomizedtesting.rules.SystemPropertiesInvariantRule;
import com.carrotsearch.randomizedtesting.rules.SystemPropertiesRestoreRule;

/**
 * @see TestRuleIgnoreAfterMaxFailures
 * @see SystemPropertiesInvariantRule
 */
public class TestMaxFailuresRule extends WithNestedTests {
  @Rule
  public SystemPropertiesRestoreRule restoreSysProps = new SystemPropertiesRestoreRule();

  public TestMaxFailuresRule() {
    super(true);
  }

  public static class Nested extends WithNestedTests.AbstractNestedTest {
    @Repeat(iterations = 500)
    public void testFailSometimes() {
      assertFalse(random().nextInt(5) == 0);
    }
  }

  @Test
  public void testMaxFailures() {
    int maxFailures = LuceneTestCase.ignoreAfterMaxFailures.maxFailures;
    int failuresSoFar = LuceneTestCase.ignoreAfterMaxFailures.failuresSoFar;
    System.clearProperty(SysGlobals.SYSPROP_ITERATIONS());
    try {
      LuceneTestCase.ignoreAfterMaxFailures.maxFailures = 2;
      LuceneTestCase.ignoreAfterMaxFailures.failuresSoFar = 0;

      JUnitCore core = new JUnitCore();
      final StringBuilder results = new StringBuilder();
      core.addListener(new RunListener() {
        char lastTest;

        @Override
        public void testStarted(Description description) throws Exception {
          lastTest = 'S'; // success.
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
          lastTest = 'A'; // assumption failure.
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
          lastTest = 'F'; // failure
        }

        @Override
        public void testFinished(Description description) throws Exception {
          results.append(lastTest);
        }
      });

      Result result = core.run(Nested.class);
      Assert.assertEquals(500, result.getRunCount());
      Assert.assertEquals(0, result.getIgnoreCount());
      Assert.assertEquals(2, result.getFailureCount());

      // Make sure we had exactly two failures followed by assumption-failures
      // resulting from ignored tests.
      Assert.assertTrue(results.toString(), 
          results.toString().matches("(S*F){2}A+"));
    } finally {
      LuceneTestCase.ignoreAfterMaxFailures.maxFailures = maxFailures;
      LuceneTestCase.ignoreAfterMaxFailures.failuresSoFar = failuresSoFar;
    }
  }
}
