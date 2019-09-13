////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Checks that class which has only private ctors
 * is declared as final. Doesn't check for classes nested in interfaces
 * or annotations, as they are always <code>final</code> there.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="FinalClass"/&gt;
 * </pre>
 * @author o_sukhodolsky
 */
public class FinalClassCheck
    extends Check {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "final.class";

    /**
     * Character separate package names full name of java class.
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /** Keeps ClassDesc objects for stack of declared classes. */
    private final Deque<ClassDesc> classes = new ArrayDeque<>();

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {TokenTypes.CLASS_DEF, TokenTypes.CTOR_DEF};
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);

        if (ast.getType() == TokenTypes.CLASS_DEF) {
            final boolean isFinal = modifiers.branchContains(TokenTypes.FINAL);
            final boolean isAbstract = modifiers.branchContains(TokenTypes.ABSTRACT);

            final String packageName = getPackageName(ast);
            final String className = ast.findFirstToken(TokenTypes.IDENT).getText();
            String outerClassFullName = null;
            if (!classes.isEmpty()) {
                outerClassFullName = classes.peek().getFullName();
            }
            final String fullClassName =
                    getFullClassName(outerClassFullName, packageName, className);

            reportNestedSubclass(ast);
            classes.push(new ClassDesc(fullClassName, isFinal, isAbstract));
        }
        // ctors in enums don't matter
        else if (!ScopeUtils.isInEnumBlock(ast)) {
            final ClassDesc desc = classes.peek();
            if (modifiers.branchContains(TokenTypes.LITERAL_PRIVATE)) {
                desc.reportPrivateCtor();
            }
            else {
                desc.reportNonPrivateCtor();
            }
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast.getType() != TokenTypes.CLASS_DEF) {
            return;
        }

        final ClassDesc desc = classes.pop();
        if (!desc.isDeclaredAsFinal()
            && !desc.isDeclaredAsAbstract()
            && desc.isWithPrivateCtor()
            && !desc.isWithNonPrivateCtor()
            && !ScopeUtils.isInInterfaceOrAnnotationBlock(ast)
            && !desc.isWithNestedSubclass()) {
            final String className =
                    desc.getFullName().substring(desc.getFullName().lastIndexOf('.') + 1);
            log(ast.getLineNo(), MSG_KEY, className);
        }
    }

    /**
     * Report to super classes that nested class extends it.
     * @param ast nested class
     */
    private void reportNestedSubclass(DetailAST ast) {
        final DetailAST classExtend = ast.findFirstToken(TokenTypes.EXTENDS_CLAUSE);
        if (classExtend != null) {
            final String superToExtend = extractFullName(classExtend);
            for (ClassDesc classDesc : classes) {
                final String superClassName = classDesc.getFullName();
                if (doesNameInExtendMatchSuperClassName(superClassName, superToExtend)) {
                    classDesc.reportNestedSubclass();
                }
            }
        }
    }

    /**
     * Checks if given super class name in extend clause match super class full name.
     * @param superClassFullName super class full name(with package)
     * @param superClassInExtendClause name in extend clause
     * @return true if given super class name in extend clause match super class full name,
     *         false otherwise
     */
    private static boolean doesNameInExtendMatchSuperClassName(String superClassFullName,
                                                               String superClassInExtendClause) {
        String superClassNormalizedName = superClassFullName;
        if (!superClassInExtendClause.contains(PACKAGE_SEPARATOR)) {
            final int beginClassNameIndex = superClassFullName.lastIndexOf(PACKAGE_SEPARATOR) + 1;
            superClassNormalizedName =
                    superClassFullName.substring(beginClassNameIndex);
        }
        return superClassNormalizedName.equals(superClassInExtendClause);
    }

    /**
     * Calculate full class name(package + class name) laying inside given
     * outer class.
     * @param outerClassFullName full name(package + class) of outer class,
     *                           null if doesnt exist
     * @param packageName package name, empty string on default package
     * @param className class name
     * @return full class name(package + class name)
     */
    private static String getFullClassName(String outerClassFullName,
                                           String packageName, String className) {
        String fullClassName;

        if (outerClassFullName == null) {
            if (packageName.isEmpty()) {
                fullClassName = className;
            }
            else {
                fullClassName = packageName + PACKAGE_SEPARATOR + className;
            }
        }
        else {
            fullClassName = outerClassFullName + PACKAGE_SEPARATOR + className;
        }
        return fullClassName;
    }

    /**
     * Get package name of given ast, on default package
     * returns empty string.
     * @param ast ast
     * @return package name
     */
    private String getPackageName(DetailAST ast) {
        DetailAST traversalAst = ast;

        while (traversalAst.getParent() != null) {
            traversalAst = traversalAst.getParent();
        }

        while (traversalAst.getPreviousSibling() != null) {
            traversalAst = traversalAst.getPreviousSibling();
        }

        String packageName;
        if (traversalAst.getType() == TokenTypes.PACKAGE_DEF) {
            packageName = extractFullName(traversalAst);
        }
        else {
            packageName = "";
        }

        return packageName;
    }

    /**
     * Get name of class(with full package if specified) in extend clause.
     * @param classExtend extend clause to extract class name
     * @return super class name
     */
    private String extractFullName(DetailAST classExtend) {
        String className;

        if (classExtend.findFirstToken(TokenTypes.IDENT) == null) {
            // Name specified with packages, have to traverse DOT
            final DetailAST firstChild = classExtend.findFirstToken(TokenTypes.DOT);
            List<String> fullNameParts = new LinkedList<>();

            fullNameParts.add(0, firstChild.findFirstToken(TokenTypes.IDENT).getText());
            DetailAST traverse = firstChild.findFirstToken(TokenTypes.DOT);
            while (traverse != null) {
                fullNameParts.add(0, traverse.findFirstToken(TokenTypes.IDENT).getText());
                traverse = traverse.findFirstToken(TokenTypes.DOT);
            }
            className = StringUtils.join(fullNameParts, PACKAGE_SEPARATOR);
        }
        else {
            className = classExtend.findFirstToken(TokenTypes.IDENT).getText();
        }

        return className;
    }

    /** Maintains information about class' ctors. */
    private static final class ClassDesc {
        /** Full class name(with package). */
        private final String fullName;

        /** Is class declared as final. */
        private final boolean declaredAsFinal;

        /** Is class declared as abstract. */
        private final boolean declaredAsAbstract;

        /** Does class have non-private ctors. */
        private boolean withNonPrivateCtor;

        /** Does class have private ctors. */
        private boolean withPrivateCtor;

        /** Does class have nested subclass. */
        private boolean withNestedSubclass;

        /**
         *  Create a new ClassDesc instance.
         *  @param fullName full class name(with package)
         *  @param declaredAsFinal indicates if the
         *         class declared as final
         *  @param declaredAsAbstract indicates if the
         *         class declared as abstract
         */
        ClassDesc(String fullName, boolean declaredAsFinal, boolean declaredAsAbstract) {
            this.fullName = fullName;
            this.declaredAsFinal = declaredAsFinal;
            this.declaredAsAbstract = declaredAsAbstract;
        }

        /**
         * Get full class name.
         * @return full class name
         */
        private String getFullName() {
            return fullName;
        }

        /** Adds private ctor. */
        private void reportPrivateCtor() {
            withPrivateCtor = true;
        }

        /** Adds non-private ctor. */
        private void reportNonPrivateCtor() {
            withNonPrivateCtor = true;
        }

        /** Adds nested subclass. */
        private void reportNestedSubclass() {
            withNestedSubclass = true;
        }

        /**
         *  Does class have private ctors.
         *  @return true if class has private ctors
         */
        private boolean isWithPrivateCtor() {
            return withPrivateCtor;
        }

        /**
         *  Does class have non-private ctors.
         *  @return true if class has non-private ctors
         */
        private boolean isWithNonPrivateCtor() {
            return withNonPrivateCtor;
        }

        /**
         * Does class have nested subclass.
         * @return true if class has nested subclass
         */
        private boolean isWithNestedSubclass() {
            return withNestedSubclass;
        }

        /**
         *  Is class declared as final.
         *  @return true if class is declared as final
         */
        private boolean isDeclaredAsFinal() {
            return declaredAsFinal;
        }

        /**
         *  Is class declared as abstract.
         *  @return true if class is declared as final
         */
        private boolean isDeclaredAsAbstract() {
            return declaredAsAbstract;
        }
    }
}
