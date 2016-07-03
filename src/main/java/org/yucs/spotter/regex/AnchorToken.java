package org.yucs.spotter.regex;

import java.util.HashSet;

class AnchorToken extends Token {
    private final char anchor;

    AnchorToken(char a) {
        this.anchor = a;
    }

    public String toString() {
        return "Anchor: " + anchor;
    }

    @Override
    public boolean match(Regex r) throws RegexException {
        int text_pos = r.text_pos;

        if (anchor == '$') {
            return r.text.length() == text_pos && r.match(next);
        } else if (anchor == '^') {
            return text_pos == 0 && r.match(next);
        } else if (anchor == 'b' || anchor == 'B') {
            boolean negative = false;

            if (anchor == 'B')
                negative = true;

            if (text_pos == 0) {
                if (Character.isAlphabetic(r.text.charAt(text_pos)))
                    return !negative && r.match(next);
            } else if (text_pos == r.text.length()) {
                if (Character.isAlphabetic(r.text.charAt(text_pos - 1))) {
                    return !negative && r.match(next);
                }
            } else {
                if ((Character.isWhitespace(r.text.charAt(text_pos - 1)) && Character.isAlphabetic(r.text.charAt(text_pos))) ||
                    (Character.isWhitespace(r.text.charAt(text_pos)) && Character.isAlphabetic(r.text.charAt(text_pos - 1)))) {
                    return !negative && r.match(next);
                }
            }

            return negative && r.match(next);
        } else {
            throw new RegexException("Unexpected ANCHOR token: " + anchor);
        }
    }
}
