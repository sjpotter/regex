package org.yucs.spotter.regex;

class AnchorToken extends Token {
    private final char anchor;

    AnchorToken(char a) {
        this.anchor = a;
    }

    public String toString() {
        return "Anchor: " + anchor;
    }

    @Override
    public boolean match(Regex r, int text_pos) throws RegexException {
        if (anchor == '$') {
            return r.text.length() == text_pos && r.match(next, text_pos);
        } else if (anchor == '^') {
            return text_pos == 0 && r.match(next, text_pos);
        } else {
            throw new RegexException("Unexpected ANCHOR token: " + anchor);
        }
    }
}
