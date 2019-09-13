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
package org.sonar.java.checks.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.CheckForNull;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import org.sonar.check.Rule;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import static org.sonar.java.checks.helpers.ConstantUtils.resolveAsBooleanConstant;
import static org.sonar.java.checks.helpers.ConstantUtils.resolveAsStringConstant;
import static org.sonar.java.matcher.TypeCriteria.subtypeOf;

@Rule(key = "S2756")
public class XmlExternalEntityProcessingCheck extends IssuableSubscriptionVisitor {

  private static final String STAX_FACTORY_CLASS_NAME = XMLInputFactory.class.getName();
  private static final String SAX_PARSER_FACTORY_CLASS_NAME = SAXParserFactory.class.getName();

  private final Map<MethodMatcher, Supplier<XxeValidator>> xxeValidatorsByMethodMatcher = xxeValidatorsByMethodMatcher();

  private static Map<MethodMatcher, Supplier<XxeValidator>> xxeValidatorsByMethodMatcher() {
    Map<MethodMatcher, Supplier<XxeValidator>> map = new HashMap<>();
    map.put(
      MethodMatcher.create().typeDefinition(STAX_FACTORY_CLASS_NAME).name("newInstance").withAnyParameters(),
      StaxXxeValidator::new);
    map.put(
      MethodMatcher.create().typeDefinition(SAX_PARSER_FACTORY_CLASS_NAME).name("newInstance").withAnyParameters(),
      SaxParserXxeValidator::new);
    return map;
  }

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Collections.singletonList(Kind.METHOD_INVOCATION);
  }

  @Override
  public void visitNode(Tree tree) {
    MethodInvocationTree methodInvocation = (MethodInvocationTree) tree;
    xxeValidatorsByMethodMatcher.forEach((matcher, validatorSupplier) -> {
      if (matcher.matches(methodInvocation)) {
        MethodTree enclosingMethod = enclosingMethod(methodInvocation);
        if (enclosingMethod != null) {
          XxeValidator xxeValidator = validatorSupplier.get();
          enclosingMethod.accept(xxeValidator);
          if (!xxeValidator.isExternalEntityProcessingDisabled()) {
            reportIssue(methodInvocation.methodSelect(), "Disable external entity (XXE) processing.");
          }
        }
      }
    });
  }

  @CheckForNull
  private static MethodTree enclosingMethod(Tree tree) {
    Tree parent = tree.parent();
    while (!parent.is(Kind.CLASS, Kind.METHOD)) {
      parent = parent.parent();
    }
    if (parent.is(Kind.CLASS)) {
      return null;
    }
    return (MethodTree) parent;
  }

  private abstract static class XxeValidator extends BaseTreeVisitor {

    abstract boolean isExternalEntityProcessingDisabled();

  }

  private static class StaxXxeValidator extends XxeValidator {

    private static final MethodMatcher SET_PROPERTY =
      MethodMatcher.create()
        .typeDefinition(subtypeOf(STAX_FACTORY_CLASS_NAME))
        .name("setProperty")
        .parameters("java.lang.String", "java.lang.Object");

    private boolean isExternalEntityProcessingDisabled = false;

    @Override
    boolean isExternalEntityProcessingDisabled() {
      return isExternalEntityProcessingDisabled;
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree methodInvocation) {
      Arguments arguments = methodInvocation.arguments();
      if (SET_PROPERTY.matches(methodInvocation)) {
        String propertyName = resolveAsStringConstant(arguments.get(0));
        if (XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES.equals(propertyName) || XMLInputFactory.SUPPORT_DTD.equals(propertyName)) {
          ExpressionTree propertyValue = arguments.get(1);
          if (Boolean.FALSE.equals(resolveAsBooleanConstant(propertyValue))) {
            isExternalEntityProcessingDisabled = true;
          }
        }
      }
      super.visitMethodInvocation(methodInvocation);
    }
  }

  private static class SaxParserXxeValidator extends XxeValidator {

    private static final MethodMatcher SET_FEATURE =
      MethodMatcher.create()
        .typeDefinition(subtypeOf(SAX_PARSER_FACTORY_CLASS_NAME))
        .name("setFeature")
        .parameters("java.lang.String", "boolean");

    private boolean isExternalEntityProcessingDisabled = false;

    @Override
    boolean isExternalEntityProcessingDisabled() {
      return isExternalEntityProcessingDisabled;
    }

    @Override
    public void visitMethodInvocation(MethodInvocationTree methodInvocation) {
      Arguments arguments = methodInvocation.arguments();
      if (SET_FEATURE.matches(methodInvocation)) {
        String featureName = resolveAsStringConstant(arguments.get(0));
        if (XMLConstants.FEATURE_SECURE_PROCESSING.equals(featureName)) {
          ExpressionTree propertyValue = arguments.get(1);
          if (Boolean.TRUE.equals(resolveAsBooleanConstant(propertyValue))) {
            isExternalEntityProcessingDisabled = true;
          }
        }
      }
      super.visitMethodInvocation(methodInvocation);
    }
  }

}
