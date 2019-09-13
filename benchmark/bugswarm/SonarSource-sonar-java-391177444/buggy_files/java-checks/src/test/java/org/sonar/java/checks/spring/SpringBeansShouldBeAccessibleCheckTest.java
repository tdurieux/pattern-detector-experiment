/*
 * SonarQube Java
 * Copyright (C) 2012-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks.spring;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.typed.ActionParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.java.SonarComponents;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.ast.parser.JavaParser;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.java.checks.verifier.JavaCheckVerifier;
import org.sonar.java.model.JavaVersionImpl;
import org.sonar.java.model.VisitorsBridge;
import org.sonar.java.model.VisitorsBridgeForTests;
import org.sonar.java.se.SymbolicExecutionMode;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaVersion;
import org.sonar.plugins.java.api.tree.Tree;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringBeansShouldBeAccessibleCheckTest {

  private static final String DEFAULT_TEST_JARS_DIRECTORY = "target/test-jars";

  @Test
  public void classicTest() {
    //JavaCheckVerifier.verify("src/test/files/checks/spring/SpringComponentWithNonAutowiredMembersCheck.java", new SpringBeansShouldBeAccessibleCheck());
    JavaCheckVerifier.verify("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/ComponentScan/ComponentScan.java", new SpringBeansShouldBeAccessibleCheck());
  }

  @Test
  public void testComponentScan() {
    List<File> classPath = new ArrayList<>();

    classPath =  getFilesRecursively(Paths.get(DEFAULT_TEST_JARS_DIRECTORY), new String[] {"jar", "zip"});

    classPath.add(new File("target/test-classes"));
    SpringBeansShouldBeAccessibleCheck check = new SpringBeansShouldBeAccessibleCheck();

    List<File> filesToScan = Arrays.asList(new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/ComponentScan/A.java"),
        new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/ComponentScan/B.java"),
        new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/ComponentScan/ComponentScan.java"));
    SonarComponents sonarComponents = CheckVerifier.sonarComponents(filesToScan.get(0));
    VisitorsBridge vb = new VisitorsBridge(ImmutableList.of(check), classPath, sonarComponents, SymbolicExecutionMode.DISABLED);
    vb.setJavaVersion(new JavaVersionImpl());
    JavaAstScanner astScanner = new JavaAstScanner(JavaParser.createParser(), null);
    astScanner.setVisitorBridge(vb);
    astScanner.scan(filesToScan);

    assertThat(check.componentScanPackageNames).isNotEmpty();
    assertThat(check.springBeansPerPackage).isNotEmpty();
  }
//
//  @Test
//  public void testSpringBootApplication() {
//    SpringBeansShouldBeAccessibleCheck check = new SpringBeansShouldBeAccessibleCheck();
//    JavaAstScanner.scanSingleFileForTests(new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/SpringBootApplication/Ko.java"), new VisitorsBridge(check));
//    JavaAstScanner.scanSingleFileForTests(new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/SpringBootApplication/Ok.java"), new VisitorsBridge(check));
//    JavaAstScanner.scanSingleFileForTests(new File("src/test/files/checks/spring/SpringBeansShouldBeAccessibleCheck/SpringBootApplication/SpringBoot.java"), new VisitorsBridge(check));
//
//    assertThat(check.springBeansPerPackage).isNotEmpty();
//    assertThat(check.componentScanPackageNames).isNotEmpty();

//  }

  static List<File> getFilesRecursively(Path root, final String[] extensions) {
    final List<File> files = new ArrayList<>();

    FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
        for (String extension : extensions) {
          if (filePath.toString().endsWith("." + extension)) {
            files.add(filePath.toFile());
            break;
          }
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }
    };

    try {
      Files.walkFileTree(root, visitor);
    } catch (IOException e) {
      // we already ignore errors in the visitor
    }

    return files;
  }
}
