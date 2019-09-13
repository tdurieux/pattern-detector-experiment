package org.sonar.php.tree.impl.statement;

import com.google.common.collect.Iterators;
import org.sonar.php.tree.impl.PHPTree;
import org.sonar.php.tree.impl.lexical.InternalSyntaxToken;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.php.api.tree.statement.ElseClauseTree;
import org.sonar.plugins.php.api.tree.statement.StatementTree;
import org.sonar.plugins.php.api.visitors.TreeVisitor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ElseClauseTreeImpl extends PHPTree implements ElseClauseTree {

  private final Kind KIND;

  private final InternalSyntaxToken elseToken;
  private final InternalSyntaxToken colonToken;
  private final List<StatementTree> statement;

  public ElseClauseTreeImpl(InternalSyntaxToken elseToken, StatementTree statement) {
    this.KIND = Kind.ELSE_CLAUSE;
    this.elseToken = elseToken;
    this.statement = Collections.singletonList(statement);
    this.colonToken = null;
  }

  @Override
  public SyntaxToken elseToken() {
    return elseToken;
  }

  @Nullable
  @Override
  public SyntaxToken colonToken() {
    return colonToken;
  }

  @Override
  public List<StatementTree> statement() {
    return statement;
  }

  @Override
  public Kind getKind() {
    return KIND;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.concat(
        Iterators.forArray(elseToken, colonToken),
        statement.iterator()
    );
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitElseClause(this);
  }
}
