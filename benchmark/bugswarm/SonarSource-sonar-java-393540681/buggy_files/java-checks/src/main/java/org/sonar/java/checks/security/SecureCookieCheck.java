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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.matcher.TypeCriteria;
import org.sonar.java.model.LiteralUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "S2092")
public class SecureCookieCheck extends IssuableSubscriptionVisitor {
  private static final String SERVLET_COOKIE = "javax.servlet.http.Cookie";
  private static final String NET_HTTP_COOKIE = "java.net.HttpCookie";
  private static final String JAX_RS_COOKIE = "javax.ws.rs.core.Cookie";
  private static final String JAX_RS_NEW_COOKIE = "javax.ws.rs.core.NewCookie";
  private static final String SHIRO_SIMPLE_COOKIE = "org.apache.shiro.web.servlet.SimpleCookie";
  private static final String SPRING_SAVED_COOKIE = "org.springframework.security.web.savedrequest.SavedCookie";
  private static final String PLAY_COOKIE = "play.mvc.Http$Cookie";
  private static final String PLAY_COOKIE_BUILDER = "play.mvc.Http$CookieBuilder";
  private static final List<String> COOKIES = Arrays.asList(SERVLET_COOKIE,
      NET_HTTP_COOKIE,
      JAX_RS_COOKIE,
      JAX_RS_NEW_COOKIE,
      SHIRO_SIMPLE_COOKIE,
      SPRING_SAVED_COOKIE,
      PLAY_COOKIE);

  private static final String CONSTRUCTOR = "<init>";
  private static final String JAVA_LANG_STRING = "java.lang.String";
  private static final String JAVA_UTIL_DATE = "java.util.Date";
  private static final String INT = "int";
  private static final String BOOLEAN = "boolean";

  private static final List<MethodMatcher> CONSTRUCTORS_WITH_SECURE_PARAM_LAST = Arrays.asList(
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAX_RS_COOKIE, JAVA_LANG_STRING, INT, BOOLEAN),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, INT, JAVA_LANG_STRING, INT, BOOLEAN),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, INT, BOOLEAN)
  );

  private static final List<MethodMatcher> CONSTRUCTORS_WITH_SECURE_PARAM_BEFORE_LAST = Arrays.asList(
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAX_RS_COOKIE, JAVA_LANG_STRING, INT, JAVA_UTIL_DATE, BOOLEAN, BOOLEAN),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, INT, JAVA_LANG_STRING, INT, JAVA_UTIL_DATE, BOOLEAN, BOOLEAN),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(JAX_RS_NEW_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, INT, BOOLEAN, BOOLEAN),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(SPRING_SAVED_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, INT, JAVA_LANG_STRING, BOOLEAN, INT),
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(PLAY_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, "java.lang.Integer", JAVA_LANG_STRING, JAVA_LANG_STRING, BOOLEAN, BOOLEAN)
  );

  private static final List<MethodMatcher> CONSTRUCTORS_WITH_SECURE_PARAM_BEFORE_BEFORE_LAST = Arrays.asList(
    MethodMatcher.create()
      .typeDefinition(TypeCriteria.subtypeOf(PLAY_COOKIE)).name(CONSTRUCTOR)
      .parameters(JAVA_LANG_STRING, JAVA_LANG_STRING, "java.lang.Integer", JAVA_LANG_STRING, JAVA_LANG_STRING, BOOLEAN, BOOLEAN, "play.mvc.Http.Cookie.SameSite")
  );

  private List<Symbol.VariableSymbol> unsecuredCookies = Lists.newArrayList();

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.VARIABLE, Tree.Kind.METHOD_INVOCATION);
  }

  @Override
  public void scanFile(JavaFileScannerContext context) {
    unsecuredCookies.clear();
    super.scanFile(context);
    for (Symbol.VariableSymbol unsecuredCookie : unsecuredCookies) {
      reportIssue(unsecuredCookie.declaration().simpleName(), "Add the \"secure\" attribute to this cookie");
    }
  }

  @Override
  public void visitNode(Tree tree) {
    if (hasSemantic()) {
      if (tree.is(Tree.Kind.VARIABLE)) {
        VariableTree variableTree = (VariableTree) tree;
        addToUnsecuredCookies(variableTree);
      } else if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
        MethodInvocationTree mit = (MethodInvocationTree) tree;
        checkSecureCall(mit);
      }
    }
  }

  private void addToUnsecuredCookies(VariableTree variableTree) {
    Type type = variableTree.type().symbolType();
    //Ignore field variables
    if (COOKIES.stream().anyMatch(type::is)
        && isConstructorInitialized(variableTree)
        && variableTree.symbol().isVariableSymbol()
        && variableTree.symbol().owner().isMethodSymbol()) {
      NewClassTree constructor = (NewClassTree) variableTree.initializer();
      Symbol.VariableSymbol variableSymbol = (Symbol.VariableSymbol) variableTree.symbol();
      if (isSecureParamTrue(constructor)) {
      }
      else {
        unsecuredCookies.add(variableSymbol);
      }
    }
  }

  private void checkSecureCall(MethodInvocationTree mit) {
    if (isSetSecureCall(mit) && mit.methodSelect().is(Tree.Kind.MEMBER_SELECT)) {
      MemberSelectExpressionTree mse = (MemberSelectExpressionTree) mit.methodSelect();
      if (mse.expression().is(Tree.Kind.IDENTIFIER)) {
        Symbol reference = ((IdentifierTree) mse.expression()).symbol();
        unsecuredCookies.remove(reference);
      }
    }
  }

  private static boolean isConstructorInitialized(VariableTree variableTree) {
    ExpressionTree initializer = variableTree.initializer();
    return initializer != null && initializer.is(Tree.Kind.NEW_CLASS);
  }

  private static boolean isSecureParamTrue(NewClassTree newClassTree) {
    ExpressionTree secureArgument = null;
    if (CONSTRUCTORS_WITH_SECURE_PARAM_LAST.stream().anyMatch(m -> m.matches(newClassTree))) {
      Arguments arguments = newClassTree.arguments();
      secureArgument = arguments.get(arguments.size() - 1);
    } else if (CONSTRUCTORS_WITH_SECURE_PARAM_BEFORE_LAST.stream().anyMatch(m -> m.matches(newClassTree))) {
      Arguments arguments = newClassTree.arguments();
      secureArgument = arguments.get(arguments.size() - 2);
    } else if (CONSTRUCTORS_WITH_SECURE_PARAM_BEFORE_BEFORE_LAST.stream().anyMatch(m -> m.matches(newClassTree))) {
      Arguments arguments = newClassTree.arguments();
      secureArgument = arguments.get(arguments.size() - 3);
    }
    if (secureArgument != null) {
      return LiteralUtils.isTrue(secureArgument);
    }
    return false;
  }

  private static boolean isSetSecureCall(MethodInvocationTree mit) {
    Symbol methodSymbol = mit.symbol();
    boolean hasArityOne = mit.arguments().size() == 1;
    if (hasArityOne && isCallSiteCookie(methodSymbol)) {
      ExpressionTree expressionTree = mit.arguments().get(0);
      if (LiteralUtils.isFalse(expressionTree)) {
        return false;
      }
      return "setSecure".equals(getIdentifier(mit).name());
    }
    return false;
  }

  private static boolean isCallSiteCookie(Symbol methodSymbol) {
    return methodSymbol.isMethodSymbol() && COOKIES.stream().anyMatch(methodSymbol.owner().type()::is);
  }

  private static IdentifierTree getIdentifier(MethodInvocationTree mit) {
    IdentifierTree id;
    if (mit.methodSelect().is(Tree.Kind.IDENTIFIER)) {
      id = (IdentifierTree) mit.methodSelect();
    } else {
      id = ((MemberSelectExpressionTree) mit.methodSelect()).identifier();
    }
    return id;
  }
}
