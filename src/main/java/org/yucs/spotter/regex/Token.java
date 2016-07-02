package org.yucs.spotter.regex;

import java.util.Stack;

abstract class Token {
    Token next = null;

    public abstract boolean match(Regex r, String text, int text_pos, Stack<CloseParenToken> closeParen) throws RegexException;
}