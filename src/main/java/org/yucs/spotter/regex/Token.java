package org.yucs.spotter.regex;

abstract class Token {
    Token next = null;

    public abstract boolean match(Matcher m) throws RegexException;
}