package org.sonar.java.cfg;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.sonar.java.model.JavaTree;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.ParenthesizedTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CFG {

  private final Block exitBlock;
  private Block currentBlock;

  /**
   * List of all blocks in order they were created.
   */
  final List<Block> blocks = new ArrayList<>();

  static class Block {
    final int id;
    final List<Tree> elements = new ArrayList<>();
    List<Block> successors = Lists.newArrayList();
    Tree terminator;

    public Block(int id) {
      this.id = id;
    }
  }

  private CFG(BlockTree tree) {
    exitBlock = createBlock();
    currentBlock = createBlock(exitBlock);
    for (StatementTree statementTree : Lists.reverse(tree.body())) {
      build(statementTree);
    }
  }

  private Block createBlock(Block successor) {
    Block result = createBlock();
    result.successors.add(successor);
    return result;
  }

  private Block createBlock() {
    Block result = new Block(blocks.size());
    blocks.add(result);
    return result;
  }

  public static CFG build(MethodTree tree) {
    Preconditions.checkArgument(tree.block() != null, "Cannot build CFG for method with no body.");
    return new CFG(tree.block());
  }

  private void build(Tree tree) {
    switch (((JavaTree) tree).getKind()) {
      case BLOCK:
        currentBlock.elements.add(tree);
        for (StatementTree statementTree : Lists.reverse(((BlockTree) tree).body())) {
          build(statementTree);
        }
        break;
      case IF_STATEMENT:
        IfStatementTree ifStatementTree = (IfStatementTree) tree;
        Block next = currentBlock;
        // process else-branch
        Block elseBlock = next;
        StatementTree elseStatement = ifStatementTree.elseStatement();
        if (elseStatement != null) {
          // if statement will create the required block.
          if (!elseStatement.is(Tree.Kind.IF_STATEMENT)) {
            currentBlock = createBlock(next);
          }
          build(elseStatement);
          elseBlock = currentBlock;
        }
        // process then-branch
        currentBlock = createBlock(next);
        build(ifStatementTree.thenStatement());
        Block thenBlock = currentBlock;
        // process condition
        currentBlock = createBranch(ifStatementTree, thenBlock, elseBlock);
        buildCondition(ifStatementTree.condition(), thenBlock, elseBlock);
        break;
    }

  }

  private void buildCondition(Tree syntaxNode, Block trueBlock, Block falseBlock) {
    switch (((JavaTree) syntaxNode).getKind()) {
      case CONDITIONAL_OR: {
        BinaryExpressionTree e = (BinaryExpressionTree) syntaxNode;
        // process RHS
        buildCondition(e.rightOperand(), trueBlock, falseBlock);
        falseBlock = currentBlock;
        // process LHS
        currentBlock = createBranch(e, trueBlock, falseBlock);
        buildCondition(e.leftOperand(), trueBlock, falseBlock);
        break;
      }
      case CONDITIONAL_AND: {
        // process RHS
        BinaryExpressionTree e = (BinaryExpressionTree) syntaxNode;
        buildCondition(e.rightOperand(), trueBlock, falseBlock);
        trueBlock = currentBlock;
        // process LHS
        currentBlock = createBranch(e, trueBlock, falseBlock);
        buildCondition(e.leftOperand(), trueBlock, falseBlock);
        break;
      }
      // Skip syntactic sugar:
      case PARENTHESIZED_EXPRESSION:
        buildCondition(((ParenthesizedTree) syntaxNode).expression(), trueBlock, falseBlock);
        break;
      default:
        build(syntaxNode);
        break;
    }
  }

  private Block createBranch(Tree terminator, Block trueBranch, Block falseBranch) {
    Block result = createBlock();
    result.terminator = terminator;
    result.successors.add(trueBranch);
    result.successors.add(falseBranch);
    return result;
  }

  public void debugTo(PrintStream out) {
    for (Block block : Lists.reverse(blocks)) {
      if (block.id != 0) {
        out.println("B" + block.id + ":");
      } else {
        out.println("B" + block.id + " (Exit) :");
      }
      int i = 0;
      for (Tree tree : block.elements) {
        out.println("  " + i + ": " + syntaxNodeToDebugString((JavaTree) tree));
        i++;
      }
      if (block.terminator != null) {
        out.println("  T: " + syntaxNodeToDebugString((JavaTree) block.terminator));
      }
      if (!block.successors.isEmpty()) {
        out.print("  Successors:");
        for (Block successor : block.successors) {
          out.print(" B" + successor.id);
        }
        out.println();
      }
    }
    out.println();
  }

  private static String syntaxNodeToDebugString(JavaTree syntaxNode) {
    StringBuilder sb = new StringBuilder(syntaxNode.getKind().name())
      .append(' ').append(Integer.toHexString(syntaxNode.hashCode()));
    switch (syntaxNode.getKind()) {
      case IDENTIFIER:
        sb.append(' ').append(((IdentifierTree) syntaxNode).identifierToken().text());
        break;
      case INT_LITERAL:
        sb.append(' ').append(((LiteralTree) syntaxNode).token().text());
        break;
    }
    return sb.toString();
  }

}
