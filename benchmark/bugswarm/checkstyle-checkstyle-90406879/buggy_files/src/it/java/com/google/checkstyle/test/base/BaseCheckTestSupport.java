////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.google.checkstyle.test.base;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AbstractViolationReporter;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

public class BaseCheckTestSupport {
    private static final Pattern warnPattern = CommonUtils
            .createPattern(".*[ ]*//[ ]*warn[ ]*|/[*]warn[*]/");
    
    private static final String XML_NAME = "/google_checks.xml";

    private static Configuration configuration = null;

    protected final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    static {
        try {
            configuration = ConfigurationLoader.loadConfiguration(XML_NAME, new PropertiesExpander(
                    System.getProperties()));
        }
        catch (final CheckstyleException e) {
            System.out.println("Error loading configuration file");
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

    protected static DefaultConfiguration createCheckConfig(Class<?> clazz) {
        return new DefaultConfiguration(clazz.getName());
    }

    protected Checker createChecker(Configuration checkConfig)
            throws Exception {
        final DefaultConfiguration dc = createCheckerConfig(checkConfig);
        final Checker checker = new Checker();
        // make sure the tests always run with english error messages
        // so the tests don't fail in supported locales like german
        final Locale locale = Locale.ENGLISH;
        checker.setLocaleCountry(locale.getCountry());
        checker.setLocaleLanguage(locale.getLanguage());
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        checker.configure(dc);
        checker.addListener(new BriefLogger(stream));
        return checker;
    }

    protected DefaultConfiguration createCheckerConfig(Configuration config) {
        final DefaultConfiguration dc =
                new DefaultConfiguration("configuration");
        final DefaultConfiguration twConf = createCheckConfig(TreeWalker.class);
        // make sure that the tests always run with this charset
        dc.addAttribute("charset", "iso-8859-1");
        dc.addChild(twConf);
        twConf.addChild(config);
        return dc;
    }

    protected String getPath(String fileName) throws IOException {
        return new File("src/it/resources/com/google/checkstyle/test/" + fileName)
                .getCanonicalPath();
    }

    protected void verify(Configuration aConfig, String aFileName, String[] aExpected,
            Integer... aWarnsExpected) throws Exception {
        verify(createChecker(aConfig), aFileName, aFileName, aExpected, aWarnsExpected);
    }

    protected void verify(Checker aC, String aFileName, String[] aExpected,
            Integer... aWarnsExpected) throws Exception {
        verify(aC, aFileName, aFileName, aExpected, aWarnsExpected);
    }

    private void verify(Checker aC,
            String aProcessedFilename,
            String aMessageFileName,
            String[] aExpected, Integer... aWarnsExpected)
        throws Exception {
        verify(aC,
            new File[] {new File(aProcessedFilename)},
            aMessageFileName, aExpected, aWarnsExpected);
    }

    protected void verify(Checker aC,
            File[] aProcessedFiles,
            String aMessageFileName,
            String[] aExpected,
            Integer... aWarnsExpected)
        throws Exception {
        stream.flush();
        final List<File> theFiles = Lists.newArrayList();
        Collections.addAll(theFiles, aProcessedFiles);
        final int errs = aC.process(theFiles);

        // process each of the lines
        final ByteArrayInputStream localStream =
            new ByteArrayInputStream(stream.toByteArray());
        try (final LineNumberReader lnr = new LineNumberReader(
                new InputStreamReader(localStream, StandardCharsets.UTF_8))) {

            for (int i = 0; i < aExpected.length; i++) {
                final String expected = aMessageFileName + ":" + aExpected[i];
                final String actual = lnr.readLine();
                assertEquals("error message " + i, expected, actual);
                String parseInt = removeDeviceFromPathOnWindows(actual);
                parseInt = parseInt.substring(parseInt.indexOf(':') + 1);
                parseInt = parseInt.substring(0, parseInt.indexOf(':'));
                final int lineNumber = Integer.parseInt(parseInt);
                Integer integer = 0;
                if (Arrays.asList(aWarnsExpected).contains(lineNumber)) {
                    integer = lineNumber;
                }
                assertEquals("error message " + i, (long) integer, lineNumber);
            }

            assertEquals("unexpected output: " + lnr.readLine(),
                    aExpected.length, errs);
        }
        aC.destroy();
    }

    /**
     * Gets the check message 'as is' from appropriate 'messages.properties'
     * file.
     *
     * @param messageKey the key of message in 'messages.properties' file.
     * @param arguments  the arguments of message in 'messages.properties' file.
     */
    protected String getCheckMessage(Class<? extends AbstractViolationReporter> aClass,
            String messageKey, Object... arguments) {
        final Properties pr = new Properties();
        try {
            pr.load(aClass.getResourceAsStream("messages.properties"));
        }
        catch (IOException e) {
            return null;
        }
        final MessageFormat formatter = new MessageFormat(pr.getProperty(messageKey),
                Locale.ROOT);
        return formatter.format(arguments);
    }

    /**
     * Gets the check message 'as is' from appropriate 'messages.properties' file.
     * @param messageKey the key of message in 'messages.properties' file.
     * @param arguments the arguments of message in 'messages.properties' file.
     */
    protected String getCheckMessage(Map<String, String> messages, String messageKey,
            Object... arguments) {
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            if (messageKey.equals(entry.getKey())) {
                final MessageFormat formatter = new MessageFormat(entry.getValue(), Locale.ROOT);
                return formatter.format(arguments);
            }
        }
        return null;
    }

    protected static Configuration getCheckConfig(String checkName) {
        Configuration result = null;
        for (Configuration currentConfig : configuration.getChildren()) {
            if ("TreeWalker".equals(currentConfig.getName())) {
                for (Configuration checkConfig : currentConfig.getChildren()) {
                    if (checkName.equals(checkConfig.getName())) {
                        result = checkConfig;
                        break;
                    }
                }
            }
            else if (checkName.equals(currentConfig.getName())) {
                result = currentConfig;
                break;
            }
        }
        return result;
    }

    private static String removeDeviceFromPathOnWindows(String path) {
        final String os = System.getProperty("os.name", "Unix");
        if (os.startsWith("Windows")) {
            return path.substring(path.indexOf(':') + 1);
        }
        return path;
    }

    protected static Integer[] getLinesWithWarn(String fileName) throws IOException {
        final List<Integer> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            int lineNumber = 1;
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (warnPattern.matcher(line).find()) {
                    result.add(lineNumber);
                }
                lineNumber++;
            }
        }
        return result.toArray(new Integer[result.size()]);
    }
}
