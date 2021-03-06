/*

   Derby - Class org.apache.derbyTesting.functionTests.tests.jdbc4.ReplicationSuite

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License
*/
package org.apache.derbyTesting.functionTests.tests.replicationTests;

import java.sql.SQLException;

import org.apache.derbyTesting.junit.BaseTestCase;

import junit.framework.Test; 
import junit.framework.TestSuite;

/**
 * Suite to run all JUnit tests in this package:
 * org.apache.derbyTesting.functionTests.tests.jdbc4
 *
 */
public class ReplicationSuite extends BaseTestCase  
{
	/**
	 * Use suite method instead.
	 */
	private ReplicationSuite(String name) {
		super(name);
	}

	public static Test suite() throws SQLException {

		TestSuite suite = new TestSuite("ReplicationSuite");

		suite.addTest(ReplicationRun_Local.suite());
        
		suite.addTest(ReplicationRun_Local_1.suite());
        
		suite.addTest(ReplicationRun_Local_StateTest_part1.suite());
		suite.addTest(ReplicationRun_Local_StateTest_part1_1.suite());
		suite.addTest(ReplicationRun_Local_StateTest_part1_2.suite());
		suite.addTest(ReplicationRun_Local_StateTest_part1_3.suite());
        
		suite.addTest(ReplicationRun_Local_StateTest_part2.suite());
        		
		// Run this separatly as it produces extra output:
        // suite.addTest(ReplicationRun_Local_showStateChange.suite());
        
        suite.addTest(ReplicationRun_Local_3_p1.suite());
        suite.addTest(ReplicationRun_Local_3_p2.suite());
        suite.addTest(ReplicationRun_Local_3_p3.suite());
        suite.addTest(ReplicationRun_Local_3_p4.suite());
        
		return suite;
	}
}
