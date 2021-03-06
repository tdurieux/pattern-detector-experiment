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
package org.apache.accumulo.core.util.shell.commands;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jline.ConsoleReader;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.iterators.IteratorUtil.IteratorScope;
import org.apache.accumulo.core.iterators.OptionDescriber;
import org.apache.accumulo.core.iterators.OptionDescriber.IteratorOptions;
import org.apache.accumulo.core.iterators.SortedKeyValueIterator;
import org.apache.accumulo.core.iterators.user.AgeOffFilter;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.iterators.user.ReqVisFilter;
import org.apache.accumulo.core.iterators.user.VersioningIterator;
import org.apache.accumulo.core.util.shell.Shell;
import org.apache.accumulo.core.util.shell.Shell.Command;
import org.apache.accumulo.core.util.shell.ShellCommandException;
import org.apache.accumulo.core.util.shell.ShellCommandException.ErrorCode;
import org.apache.accumulo.start.classloader.vfs.AccumuloVFSClassLoader;
import org.apache.accumulo.start.classloader.vfs.ContextManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.vfs2.FileSystemException;

public class SetIterCommand extends Command {
  
  private Option mincScopeOpt, majcScopeOpt, scanScopeOpt, nameOpt, priorityOpt;
  private Option aggTypeOpt, ageoffTypeOpt, regexTypeOpt, versionTypeOpt, reqvisTypeOpt, classnameTypeOpt;
  
  @Override
  public int execute(final String fullCommand, final CommandLine cl, final Shell shellState) throws AccumuloException, AccumuloSecurityException,
      TableNotFoundException, IOException, ShellCommandException {
    
    final int priority = Integer.parseInt(cl.getOptionValue(priorityOpt.getOpt()));
    
    final Map<String,String> options = new HashMap<String,String>();
    String classname = cl.getOptionValue(classnameTypeOpt.getOpt());
    if (cl.hasOption(aggTypeOpt.getOpt())) {
      Shell.log.warn("aggregators are deprecated");
      @SuppressWarnings("deprecation")
      String deprecatedClassName = org.apache.accumulo.core.iterators.AggregatingIterator.class.getName();
      classname = deprecatedClassName;
    } else if (cl.hasOption(regexTypeOpt.getOpt())) {
      classname = RegExFilter.class.getName();
    } else if (cl.hasOption(ageoffTypeOpt.getOpt())) {
      classname = AgeOffFilter.class.getName();
    } else if (cl.hasOption(versionTypeOpt.getOpt())) {
      classname = VersioningIterator.class.getName();
    } else if (cl.hasOption(reqvisTypeOpt.getOpt())) {
      classname = ReqVisFilter.class.getName();
    }
    
    ClassLoader classloader = getClassLoader(cl, shellState);

    final String name = cl.getOptionValue(nameOpt.getOpt(), setUpOptions(classloader, shellState.getReader(), classname, options));
    
    setTableProperties(cl, shellState, priority, options, classname, name);
    return 0;
  }

  private ClassLoader getClassLoader(final CommandLine cl, final Shell shellState) throws AccumuloException, TableNotFoundException, AccumuloSecurityException,
      IOException, FileSystemException {
    String classpath = null;
    Iterable<Entry<String,String>> tableProps = shellState.getConnector().tableOperations().getProperties(OptUtil.getTableOpt(cl, shellState));
    for (Entry<String,String> entry : tableProps) {
      if (entry.getKey().equals(Property.TABLE_CLASSPATH.getKey())) {
        classpath = entry.getValue();
      }
    }
    
    ClassLoader classloader;

    if (classpath != null && !classpath.equals("")) {
      shellState.getConnector().instanceOperations().getSystemConfiguration().get(Property.VFS_CONTEXT_CLASSPATH_PROPERTY.getKey() + classpath);
      
      try {
        AccumuloVFSClassLoader.getContextManager().setContextConfig(new ContextManager.DefaultContextsConfig(new Iterable<Map.Entry<String,String>>() {
          @Override
          public Iterator<Entry<String,String>> iterator() {
            try {
              return shellState.getConnector().instanceOperations().getSystemConfiguration().entrySet().iterator();
            } catch (AccumuloException e) {
              throw new RuntimeException(e);
            } catch (AccumuloSecurityException e) {
              throw new RuntimeException(e);
            }
          }
        }));
      } catch (IllegalStateException ise) {}

      classloader = AccumuloVFSClassLoader.getContextManager().getClassLoader(classpath);
    } else {
      classloader = AccumuloVFSClassLoader.getClassLoader();
    }
    return classloader;
  }
  
  protected void setTableProperties(final CommandLine cl, final Shell shellState, final int priority, final Map<String,String> options, final String classname,
      final String name) throws AccumuloException, AccumuloSecurityException, ShellCommandException, TableNotFoundException {
    // remove empty values
    
    final String tableName = OptUtil.getTableOpt(cl, shellState);

    if (!shellState.getConnector().tableOperations().testClassLoad(tableName, classname, SortedKeyValueIterator.class.getName())) {
      throw new ShellCommandException(ErrorCode.INITIALIZATION_FAILURE, "Servers are unable to load " + classname + " as type "
          + SortedKeyValueIterator.class.getName());
    }
    
    final String aggregatorClass = options.get("aggregatorClass");
    @SuppressWarnings("deprecation")
    String deprecatedAggregatorClassName = org.apache.accumulo.core.iterators.aggregation.Aggregator.class.getName();
    if (aggregatorClass != null && !shellState.getConnector().tableOperations().testClassLoad(tableName, aggregatorClass, deprecatedAggregatorClassName)) {
      throw new ShellCommandException(ErrorCode.INITIALIZATION_FAILURE, "Servers are unable to load " + aggregatorClass + " as type "
          + deprecatedAggregatorClassName);
    }
    
    for (Iterator<Entry<String,String>> i = options.entrySet().iterator(); i.hasNext();) {
      final Entry<String,String> entry = i.next();
      if (entry.getValue() == null || entry.getValue().isEmpty()) {
        i.remove();
      }
    }
    final EnumSet<IteratorScope> scopes = EnumSet.noneOf(IteratorScope.class);
    if (cl.hasOption(mincScopeOpt.getOpt())) {
      scopes.add(IteratorScope.minc);
    }
    if (cl.hasOption(majcScopeOpt.getOpt())) {
      scopes.add(IteratorScope.majc);
    }
    if (cl.hasOption(scanScopeOpt.getOpt())) {
      scopes.add(IteratorScope.scan);
    }
    if (scopes.isEmpty()) {
      throw new IllegalArgumentException("You must select at least one scope to configure");
    }
    final IteratorSetting setting = new IteratorSetting(priority, name, classname, options);
    shellState.getConnector().tableOperations().attachIterator(tableName, setting, scopes);
  }
  
  private static String setUpOptions(ClassLoader classloader, final ConsoleReader reader, final String className, final Map<String,String> options)
      throws IOException,
      ShellCommandException {
    String input;
    OptionDescriber skvi;
    Class<? extends OptionDescriber> clazz;
    try {
      clazz = classloader.loadClass(className).asSubclass(OptionDescriber.class);
      skvi = clazz.newInstance();
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (InstantiationException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (ClassCastException e) {
      throw new ShellCommandException(ErrorCode.INITIALIZATION_FAILURE, "Unable to load " + className + " as type " + OptionDescriber.class.getName()
          + "; configure with 'config' instead");
    }
    
    final IteratorOptions itopts = skvi.describeOptions();
    if (itopts.getName() == null) {
      throw new IllegalArgumentException(className + " described its default distinguishing name as null");
    }
    String shortClassName = className;
    if (className.contains(".")) {
      shortClassName = className.substring(className.lastIndexOf('.') + 1);
    }
    final Map<String,String> localOptions = new HashMap<String,String>();
    do {
      // clean up the overall options that caused things to fail
      for (String key : localOptions.keySet()) {
        options.remove(key);
      }
      localOptions.clear();
      
      reader.printString(itopts.getDescription());
      reader.printNewline();
      
      String prompt;
      if (itopts.getNamedOptions() != null) {
        for (Entry<String,String> e : itopts.getNamedOptions().entrySet()) {
          prompt = Shell.repeat("-", 10) + "> set " + shortClassName + " parameter " + e.getKey() + ", " + e.getValue() + ": ";
          reader.flushConsole();
          input = reader.readLine(prompt);
          if (input == null) {
            reader.printNewline();
            throw new IOException("Input stream closed");
          } else {
            input = new String(input);
          }
          // Places all Parameters and Values into the LocalOptions, even if the value is "".
          // This allows us to check for "" values when setting the iterators and allows us to remove
          // the parameter and value from the table property.
          localOptions.put(e.getKey(), input);
        }
      }
      
      if (itopts.getUnnamedOptionDescriptions() != null) {
        for (String desc : itopts.getUnnamedOptionDescriptions()) {
          reader.printString(Shell.repeat("-", 10) + "> entering options: " + desc + "\n");
          input = "start";
          while (true) {
            prompt = Shell.repeat("-", 10) + "> set " + shortClassName + " option (<name> <value>, hit enter to skip): ";
            
            reader.flushConsole();
            input = reader.readLine(prompt);
            if (input == null) {
              reader.printNewline();
              throw new IOException("Input stream closed");
            } else {
              input = new String(input);
            }
            
            if (input.length() == 0)
              break;
            
            String[] sa = input.split(" ", 2);
            localOptions.put(sa[0], sa[1]);
          }
        }
      }
      
      options.putAll(localOptions);
      if (!skvi.validateOptions(options))
        reader.printString("invalid options for " + clazz.getName() + "\n");
      
    } while (!skvi.validateOptions(options));
    return itopts.getName();
  }
  
  @Override
  public String description() {
    return "sets a table-specific iterator";
  }
  
  @Override
  public Options getOptions() {
    final Options o = new Options();
    
    priorityOpt = new Option("p", "priority", true, "the order in which the iterator is applied");
    priorityOpt.setArgName("pri");
    priorityOpt.setRequired(true);
    
    nameOpt = new Option("n", "name", true, "iterator to set");
    nameOpt.setArgName("itername");
    
    mincScopeOpt = new Option(IteratorScope.minc.name(), "minor-compaction", false, "applied at minor compaction");
    majcScopeOpt = new Option(IteratorScope.majc.name(), "major-compaction", false, "applied at major compaction");
    scanScopeOpt = new Option(IteratorScope.scan.name(), "scan-time", false, "applied at scan time");
    
    final OptionGroup typeGroup = new OptionGroup();
    classnameTypeOpt = new Option("class", "class-name", true, "a java class that implements SortedKeyValueIterator");
    classnameTypeOpt.setArgName("name");
    aggTypeOpt = new Option("agg", "aggregator", false, "an aggregating type");
    regexTypeOpt = new Option("regex", "regular-expression", false, "a regex matching iterator");
    versionTypeOpt = new Option("vers", "version", false, "a versioning iterator");
    reqvisTypeOpt = new Option("reqvis", "require-visibility", false, "an iterator that omits entries with empty visibilities");
    ageoffTypeOpt = new Option("ageoff", "ageoff", false, "an aging off iterator");
    
    typeGroup.addOption(classnameTypeOpt);
    typeGroup.addOption(aggTypeOpt);
    typeGroup.addOption(regexTypeOpt);
    typeGroup.addOption(versionTypeOpt);
    typeGroup.addOption(reqvisTypeOpt);
    typeGroup.addOption(ageoffTypeOpt);
    typeGroup.setRequired(true);
    
    o.addOption(OptUtil.tableOpt("table to configure iterators on"));
    o.addOption(priorityOpt);
    o.addOption(nameOpt);
    o.addOption(mincScopeOpt);
    o.addOption(majcScopeOpt);
    o.addOption(scanScopeOpt);
    o.addOptionGroup(typeGroup);
    return o;
  }
  
  @Override
  public int numArgs() {
    return 0;
  }
}
