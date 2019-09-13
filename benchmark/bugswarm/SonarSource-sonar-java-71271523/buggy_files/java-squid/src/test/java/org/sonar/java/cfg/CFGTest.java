package org.sonar.java.cfg;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.sonar.java.ast.parser.JavaParser;
import org.sonar.java.parser.sslr.ActionParser;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import static org.fest.assertions.Assertions.assertThat;

public class CFGTest {

  public static final ActionParser parser = JavaParser.createParser(Charsets.UTF_8);

  private static CFG buildCFG(String methodCode) {
    CompilationUnitTree cut = (CompilationUnitTree) parser.parse("class A { "+methodCode+" }");
    MethodTree tree = ((MethodTree) ((ClassTree) cut.types().get(0)).members().get(0));
    return CFG.build(tree);
  }

  @Test
  public void simplest_cfg() throws Exception {
    CFG cfg = buildCFG("void fun() {}");
    assertThat(cfg.blocks).hasSize(2);
    cfg = buildCFG("void fun() { bar();}");
    assertThat(cfg.blocks).hasSize(2);
    cfg = buildCFG("void fun() { bar();qix();baz();}");
    assertThat(cfg.blocks).hasSize(2);
  }

  @Test
  public void cfg_if_statement() throws Exception {
    CFG cfg = buildCFG("void fun() {if(a) { foo(); } }");
    assertThat(cfg.blocks).hasSize(4);
    assertThat(successors(cfg.blocks.get(1))).containsOnly(0);
    assertThat(successors(cfg.blocks.get(2))).containsOnly(1);
    assertThat(successors(cfg.blocks.get(3))).containsOnly(1, 2);
    assertThat(cfg.blocks.get(3).terminator).isNotNull();
    assertThat(cfg.blocks.get(3).elements).isEmpty();
    assertThat(cfg.blocks.get(3).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();

    cfg = buildCFG("void fun() {if(a) { foo(); } else { bar(); } }");
    assertThat(cfg.blocks).hasSize(5);
    assertThat(successors(cfg.blocks.get(1))).containsOnly(0);
    assertThat(successors(cfg.blocks.get(2))).containsOnly(1);
    assertThat(successors(cfg.blocks.get(3))).containsOnly(1);
    assertThat(successors(cfg.blocks.get(4))).containsOnly(2, 3);
    assertThat(cfg.blocks.get(4).terminator).isNotNull();
    assertThat(cfg.blocks.get(4).elements).isEmpty();
    assertThat(cfg.blocks.get(3).elements).hasSize(1);
    assertThat(cfg.blocks.get(2).elements).hasSize(1);
    assertThat(cfg.blocks.get(4).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();

    cfg = buildCFG("void fun() {\nif(a) {\n foo(); \n } else if(b) {\n bar();\n } }");
    assertThat(cfg.blocks).hasSize(6);
    assertThat(cfg.blocks.get(5).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();
    assertThat(cfg.blocks.get(3).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();
  }

  @Test
  public void conditional_expression() throws Exception {
    CFG cfg = buildCFG("void fun() {if(a || b) { foo(); } }");
    assertThat(cfg.blocks).hasSize(5);
    assertThat(cfg.blocks.get(4).terminator.is(Tree.Kind.CONDITIONAL_OR)).isTrue();
    assertThat(cfg.blocks.get(3).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();

    cfg = buildCFG("void fun() {if((a && b)) { foo(); } }");
    assertThat(cfg.blocks).hasSize(5);
    assertThat(cfg.blocks.get(4).terminator.is(Tree.Kind.CONDITIONAL_AND)).isTrue();
    assertThat(cfg.blocks.get(3).terminator.is(Tree.Kind.IF_STATEMENT)).isTrue();
  }

  private static int[] successors(CFG.Block block) {
    int[] successors = new int[block.successors.size()];
    for (int i = 0; i < block.successors.size(); i++) {
      successors[i] = block.successors.get(i).id;
    }
    return successors;
  }




}
