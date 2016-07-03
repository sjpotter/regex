package org.yucs.spotter.regex;

@SuppressWarnings("SameParameterValue")
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
    public boolean match(Matcher m) throws RegexException {
        int text_pos = m.getTextPosition();
        if (capturing)
            m.recordGroup(matched.pos, text_pos);

        return m.match(next);
    }
}
