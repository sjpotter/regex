package org.yucs.spotter.regex;

import java.util.Stack;

class CloseParenToken extends Token {

    OpenParenToken matched;

    @Override
    public boolean match(Regex r, String text, int text_pos, Stack<CloseParenToken> closeParen) throws RegexException {
        r.recordGroup(matched.pos, text, matched.text_pos, text_pos);

        return r.match(next, text, text_pos, closeParen);
    }
}
