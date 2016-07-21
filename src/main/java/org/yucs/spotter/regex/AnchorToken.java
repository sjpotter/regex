package org.yucs.spotter.regex;

// Anchor Tokens make sure the position we are in the text corresponds to some rule
// ^ - we are at text_pos = 0,
// $ - we have text_pos = text.length() (i.e. one beyond the last character, aka matched everything in text)
// \b and \B at a word break or not at a word break (ex: between 2 alphabetic characters or not)

class AnchorToken extends Token {
    private final char anchor;

    AnchorToken(char a) {
        super();
        this.anchor = a;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();

        switch (anchor) {
            case '$': // end of text anchor, continues matching as can e a lookbehind at this point
                return text_pos == text.length() && next.match(m);

            case '^': // start of text anchor
                return 0 == text_pos && next.match(m);

            case 'b': // word break anchor
            case 'B': { // negative word break anchors
                boolean negative = false;
                if (anchor == 'B')
                    negative = true;

                if (text_pos == 0) {
                    if (Character.isAlphabetic(text.charAt(text_pos)))
                        return !negative && next.match(m);
                } else if (text_pos == text.length()) {
                    if (Character.isAlphabetic(text.charAt(text_pos - 1))) {
                        return !negative && next.match(m);
                    }
                } else {
                    if ((Character.isWhitespace(text.charAt(text_pos - 1)) && Character.isAlphabetic(text.charAt(text_pos))) ||
                            (Character.isWhitespace(text.charAt(text_pos)) && Character.isAlphabetic(text.charAt(text_pos - 1)))) {
                        return !negative && next.match(m);
                    }
                }

                return negative && next.match(m);
            }

            default:
                throw new RegexException("Unexpected ANCHOR token: " + anchor);
        }
    }
}