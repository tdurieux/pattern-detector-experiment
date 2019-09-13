package com.puppycrawl.tools.checkstyle.checks.whitespace.separatorwrap;

class InputSeparatorWrapEllipsis {

    public void testMethodWithGoodWrapping(String...
            parameters) {

    }

    public void testMethodWithBadWrapping(String
            ...parameters) {

    }

}

