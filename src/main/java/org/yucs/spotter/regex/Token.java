package org.yucs.spotter.regex;

abstract class Token {
    Token next = null;

    public abstract boolean match(Regex r, int text_pos) throws RegexException;
}