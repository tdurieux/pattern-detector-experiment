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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.PackageDeclarationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

@Rule(key = "S4605")
public class SpringBeansShouldBeAccessibleCheck extends IssuableSubscriptionVisitor {

  final Set<String> componentScanPackageNames = Sets.newHashSet();
  final Map<String, List<ClassTree>> springBeansPerPackage = Maps.newHashMap();

  private static final String[] SPRING_BEAN_ANNOTATIONS = {
      "org.springframework.stereotype.Component",
          "org.springframework.stereotype.Service",
          "org.springframework.stereotype.Repository",
          "org.springframework.stereotype.Controller",
          "org.springframework.web.bind.annotation.RestController"
  };

  private static final String SPRING_BOOT_APP = "org.springframework.boot.autoconfigure.SpringBootApplication";
  private static final String COMPONENT_SCAN = "org.springframework.context.annotation.ComponentScan";

  @Override
  public List<Kind> nodesToVisit() {
    return Collections.singletonList(Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    if(!hasSemantic()) {
      return;
    }
    ClassTree classTree = (ClassTree) tree;
    if (isClassTreeAnnotatedWith(classTree,
        "org.springframework.stereotype.Controller",
        "org.springframework.stereotype.Repository",
        "org.springframework.stereotype.Service")) {
      System.out.println(classTree);
    }
    // TODO must refactor this
    String packageName = "";
    List<SymbolMetadata.AnnotationInstance> a = classTree.symbol().metadata().annotations();

    if (classTree.parent().is(Tree.Kind.COMPILATION_UNIT)) {
      CompilationUnitTree cTree = (CompilationUnitTree) classTree.parent();
      PackageDeclarationTree packageDeclarationTree = cTree.packageDeclaration();
      if (packageDeclarationTree != null) {
        if (packageDeclarationTree.packageName() != null && packageDeclarationTree.packageName().is(Kind.MEMBER_SELECT)) {
          packageName = ((MemberSelectExpressionTree) packageDeclarationTree.packageName()).identifier().name();
        } else if (packageDeclarationTree.packageName().is(Kind.IDENTIFIER)) {
          packageName = ((IdentifierTree) packageDeclarationTree.packageName()).name();
        }
      }
    }

    // TODO problem: annotations.get(0).annotationType().symbolType() == "unknown"
    if (isClassTreeAnnotatedWith(classTree, SPRING_BEAN_ANNOTATIONS)) {
      if (packageName != "") {
        List<ClassTree> beansInPackage = springBeansPerPackage.get(packageName);
        if (beansInPackage == null) {
          beansInPackage = new ArrayList<>();
        }
        beansInPackage.add(classTree);
      }
    } else if (isClassTreeAnnotatedWith(classTree, SPRING_BOOT_APP)) {
      componentScanPackageNames.add(packageName);
    } else if (isClassTreeAnnotatedWith(classTree, COMPONENT_SCAN)) {
      componentScanPackageNames.add(packageName);
    }
  }

  private static boolean isClassTreeAnnotatedWith(ClassTree classTree, String... annotationName) {
    return Arrays.stream(annotationName).anyMatch(annotation -> classTree.symbol().metadata().isAnnotatedWith(annotation));
  }
}
