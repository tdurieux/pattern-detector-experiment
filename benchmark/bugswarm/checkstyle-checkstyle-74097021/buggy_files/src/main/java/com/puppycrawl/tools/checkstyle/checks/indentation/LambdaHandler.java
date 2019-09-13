////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Handler for lambda statements.
 *
 * @author pietern
 */
public class LambdaHandler extends AbstractExpressionHandler {
    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param ast           the abstract syntax tree
     * @param parent        the parent handler
     */
    public LambdaHandler(IndentationCheck indentCheck,
                         DetailAST ast, AbstractExpressionHandler parent) {
        super(indentCheck, "lambda", ast, parent);
    }

    @Override
    public IndentLevel suggestedChildLevel(AbstractExpressionHandler child) {
        if (child instanceof SlistHandler) {
            return getLevel();
        }

        return super.suggestedChildLevel(child);
    }

    @Override
    protected IndentLevel getLevelImpl() {
        // If the start of the lambda isn't the first element on the line,
        // use the start of the line as the reference indentation level.
        final DetailAST firstChild = getMainAst().getFirstChild();
        if (getLineStart(firstChild) != firstChild.getColumnNo()) {
            return new IndentLevel(getLineStart(firstChild));
        }

        // The start of the lambda isn't the first element on the line;
        // assume line wrapping with respect to its parent.
        final int parentLineStart = getLineStart(getParent().getMainAst());
        return new IndentLevel(parentLineStart + getIndentCheck().getLineWrappingIndentation());
    }

    @Override
    public void checkIndentation() {
        // Nothing to do here: all checking is done in parents and children.
    }
}
