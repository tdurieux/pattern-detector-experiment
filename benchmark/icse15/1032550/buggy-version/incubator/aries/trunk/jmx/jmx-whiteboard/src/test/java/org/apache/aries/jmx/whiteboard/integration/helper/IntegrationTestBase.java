/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jmx.whiteboard.integration.helper;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class IntegrationTestBase {

    // the name of the system property providing the bundle file to be installed
    // and tested
    protected static final String BUNDLE_JAR_SYS_PROP = "project.bundle.file";

    // the default bundle jar file name
    protected static final String BUNDLE_JAR_DEFAULT = "target/jmx-whiteboard.jar";

    // the JVM option to set to enable remote debugging
    protected static final String DEBUG_VM_OPTION = "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=30303";

    // the actual JVM option set, extensions may implement a static
    // initializer overwriting this value to have the configuration()
    // method include it when starting the OSGi framework JVM
    protected static String paxRunnerVmOption = null;

    private static MBeanServer staticServer;

    private MBeanServer server;

    @Inject
    protected BundleContext bundleContext;

    private ServiceRegistration staticServerRegistration;

    protected static final String PROP_NAME = "theValue";

    protected static final Dictionary<String, String> theConfig;

    static {
        theConfig = new Hashtable<String, String>();
        theConfig.put(PROP_NAME, PROP_NAME);
    }

    @org.ops4j.pax.exam.junit.Configuration
    public static Option[] configuration() {
        final String bundleFileName = System.getProperty(BUNDLE_JAR_SYS_PROP,
            BUNDLE_JAR_DEFAULT);
        final File bundleFile = new File(bundleFileName);
        if (!bundleFile.canRead()) {
            throw new IllegalArgumentException("Cannot read from bundle file "
                + bundleFileName + " specified in the " + BUNDLE_JAR_SYS_PROP
                + " system property");
        }

        final Option[] base = options(
            provision(
                CoreOptions.bundle(bundleFile.toURI().toString()),
                mavenBundle("org.ops4j.pax.swissbox",
                    "pax-swissbox-tinybundles", "1.0.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin",
                    "1.2.8"), mavenBundle("org.slf4j", "slf4j-api", "1.5.2"),
                mavenBundle("org.slf4j", "slf4j-simple", "1.5.2")),
            waitForFrameworkStartup());
        final Option vmOption = (paxRunnerVmOption != null)
                ? PaxRunnerOptions.vmOption(paxRunnerVmOption)
                : null;
        return OptionUtils.combine(base, vmOption);
    }

    @Before
    public void setUp() {
        staticServerRegistration = registerMBeanServer(getStaticMBeanServer());
    }

    @After
    public void tearDown() {
        staticServerRegistration.unregister();
    }

    protected MBeanServer getStaticMBeanServer() {
        if (staticServer == null) {
            staticServer = MBeanServerFactory.createMBeanServer("StaticServerDomain");
        }
        return staticServer;
    }

    protected MBeanServer getMBeanServer() {
        return server;
    }

    protected MBeanServer getOrCreateMBeanServer() {
        if (server == null) {
            server = MBeanServerFactory.createMBeanServer("DynamicServerDomain");
        }
        return server;
    }

    protected void dropMBeanServer() {
        if (server != null) {
            MBeanServerFactory.releaseMBeanServer(server);
            server = null;
        }
    }

    protected ServiceRegistration registerMBeanServer(final MBeanServer server) {
        return registerService(MBeanServer.class.getName(), server, null);
    }

    protected ServiceRegistration registerService(final String clazz,
            final Object service, final String objectName) {
        Hashtable<String, String> properties;
        if (objectName != null) {
            properties = new Hashtable<String, String>();
            properties.put("jmx.objectname", objectName);
        } else {
            properties = null;
        }
        return bundleContext.registerService(clazz, service, properties);
    }

    protected void assertRegistered(final MBeanServer server,
            final ObjectName objectName) {
        try {
            ObjectInstance instance = server.getObjectInstance(objectName);
            TestCase.assertNotNull(instance);
            TestCase.assertEquals(objectName, instance.getObjectName());
        } catch (InstanceNotFoundException nfe) {
            TestCase.fail("Expected instance of " + objectName
                + " registered with MBeanServer");
        }
    }

    protected void assertNotRegistered(final MBeanServer server,
            final ObjectName objectName) {
        try {
            server.getObjectInstance(objectName);
            TestCase.fail("Unexpected instance of " + objectName
                + " registered with MBeanServer");
        } catch (InstanceNotFoundException nfe) {
            // expected, ignore
        }
    }

}
