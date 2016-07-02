package org.yucs.spotter.regex;

class CloseParenToken extends Token {

    OpenParenToken matched;

    @Override
    public boolean match(Regex r, int text_pos) throws RegexException {
        r.recordGroup(matched.pos, matched.text_pos, text_pos);

        return r.match(next, text_pos);
    }
}
