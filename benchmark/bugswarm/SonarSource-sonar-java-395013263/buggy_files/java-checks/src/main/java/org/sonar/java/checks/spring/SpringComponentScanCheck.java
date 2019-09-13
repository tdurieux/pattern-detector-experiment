package org.sonar.java.checks.spring;

import java.util.Collections;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "S4603")
public class SpringComponentScanCheck extends IssuableSubscriptionVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return Collections.singletonList(Tree.Kind.ANNOTATION);
  }

  @Override
  public void visitNode(Tree tree) {
    AnnotationTree annotation = (AnnotationTree) tree;
    Type type = annotation.symbolType();
    if (type.is("org.springframework.context.annotation.ComponentScan") ||
      type.is("org.springframework.boot.autoconfigure.SpringBootApplication")) {
      reportIssue(annotation.annotationType(), "Consider replacing \"@" + type.name() +
        "\" by a list of beans imported with @Import to speed-up the start-up of the application.");
    }
  }
}
