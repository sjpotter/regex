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
    public boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();

        if (anchor == '$') {
            return text.length() == text_pos && m.match(next);
        } else if (anchor == '^') {
            return text_pos == 0 && m.match(next);
        } else if (anchor == 'b' || anchor == 'B') {
            boolean negative = false;

            if (anchor == 'B')
                negative = true;

            if (text_pos == 0) {
                if (Character.isAlphabetic(text.charAt(text_pos)))
                    return !negative && m.match(next);
            } else if (text_pos == text.length()) {
                if (Character.isAlphabetic(text.charAt(text_pos - 1))) {
                    return !negative && m.match(next);
                }
            } else {
                if ((Character.isWhitespace(text.charAt(text_pos - 1)) && Character.isAlphabetic(text.charAt(text_pos))) ||
                    (Character.isWhitespace(text.charAt(text_pos)) && Character.isAlphabetic(text.charAt(text_pos - 1)))) {
                    return !negative && m.match(next);
                }
            }

            return negative && m.match(next);
        } else {
            throw new RegexException("Unexpected ANCHOR token: " + anchor);
        }
    }
}