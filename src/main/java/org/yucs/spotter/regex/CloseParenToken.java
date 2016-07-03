package org.yucs.spotter.regex;

class CloseParenToken extends Token {

    OpenParenToken matched;

    private final boolean capturing;

    @SuppressWarnings("WeakerAccess") // TODO: will eventually have non capturing parens
    CloseParenToken(boolean capturing) {
        this.capturing = capturing;
    }

    CloseParenToken() {
        this(true);
    }

    @Override
    public boolean match(Regex r) throws RegexException {
        if (capturing)
            r.recordGroup(matched.pos, matched.text_pos, r.text_pos);

        return r.match(next);
    }
}
