package org.yucs.spotter.regex;

import java.util.Stack;

class AnchorToken extends Token {
    private final char anchor;

    AnchorToken(char a) {
        this.anchor = a;
    }

    public String toString() {
        return "Anchor: " + anchor;
    }

    @Override
    public boolean match(Regex r, String text, int text_pos, Stack<CloseParenToken> closeParen) throws RegexException {
        if (anchor == '$') {
            return text.length() == text_pos && r.match(next, text, text_pos, closeParen);
        } else if (anchor == '^') {
            return text_pos == 0 && r.match(next, text, text_pos, closeParen);
        } else {
            throw new RegexException("Unexpected ANCHOR token: " + anchor);
        }

    }
}
