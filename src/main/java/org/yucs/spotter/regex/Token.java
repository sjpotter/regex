package org.yucs.spotter.regex;

abstract class Token {
    Token next = null;

    abstract boolean match(Matcher m) throws RegexException;

    Token() {
        next = NullToken.Instance;
    }

    int captureGroup() {
        return -1;
    }
}