package org.sonar.java.checks.helpers;

import java.util.function.Function;
import javax.annotation.CheckForNull;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;

public class IdentifierUtils {
  private IdentifierUtils() {
    // This class only contains static methods
  }

  @CheckForNull
  public static <T> T getValue(ExpressionTree expression, Function<ExpressionTree,T> resolver) {
    T value = resolver.apply(expression);
    if (value == null && expression.is(Tree.Kind.IDENTIFIER)) {
      ExpressionTree last = ReassignmentFinder.getClosestReassignmentOrDeclarationExpression(expression, ((IdentifierTree) expression).symbol());
      value = last == null || last == expression ? null : getValue(last, resolver);
    }
    return value;
  }
}
