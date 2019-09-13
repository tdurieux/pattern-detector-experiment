package org.sonar.java.checks.security;

import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import org.sonar.check.Rule;
import org.sonar.java.checks.methods.AbstractMethodDetection;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.model.LiteralUtils;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import static org.sonar.java.checks.helpers.ConstantUtils.resolveAsStringConstant;
import static org.sonar.java.matcher.TypeCriteria.subtypeOf;

@Rule(key = "S4435")
public class SecureXmlTransformerCheck extends AbstractMethodDetection {

  public static final String TRANSFORMER_FACTORY_CLASS_NAME = "javax.xml.transform.TransformerFactory";

  @Override
  protected List<MethodMatcher> getMethodInvocationMatchers() {
    return Collections.singletonList(
      MethodMatcher.create()
        .typeDefinition(TRANSFORMER_FACTORY_CLASS_NAME)
        .name("newInstance")
        .withAnyParameters());
  }

  @Override
  protected void onMethodInvocationFound(MethodInvocationTree mit) {
    Tree enclosingMethod = enclosingMethod(mit);
    MethodBodyVisitor visitor = new MethodBodyVisitor();
    enclosingMethod.accept(visitor);
    if (!visitor.foundCallsToSecuringMethods()) {
      reportIssue(mit.methodSelect(), "Secure this \"Transformer\" by either disabling external DTDs or enabling secure processing.");
    }
  }

  private static Tree enclosingMethod(Tree tree) {
    Tree parent = tree.parent();
    while (!parent.is(Kind.CLASS, Kind.METHOD)) {
      parent = parent.parent();
    }
    if (parent.is(Kind.CLASS)) {
      return null;
    }
    return parent;
  }

  private static class MethodBodyVisitor extends BaseTreeVisitor {

    private static final MethodMatcher SET_FEATURE =
      MethodMatcher.create()
        .typeDefinition(subtypeOf(TRANSFORMER_FACTORY_CLASS_NAME))
        .name("setFeature")
        .parameters("java.lang.String", "boolean");

    private boolean foundCallToSetFeature = false;

    @Override
    public void visitMethodInvocation(MethodInvocationTree methodInvocation) {
      Arguments arguments = methodInvocation.arguments();

      if (SET_FEATURE.matches(methodInvocation)
        && XMLConstants.FEATURE_SECURE_PROCESSING.equals(resolveAsStringConstant(arguments.get(0)))
        && LiteralUtils.isTrue(arguments.get(1))) {

        foundCallToSetFeature = true;
      }

      super.visitMethodInvocation(methodInvocation);
    }

    private boolean foundCallsToSecuringMethods() {
      return foundCallToSetFeature;
    }
  }
}
