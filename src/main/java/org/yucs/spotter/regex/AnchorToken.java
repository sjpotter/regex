package org.yucs.spotter.regex;

class AnchorToken extends Token {
    private final char anchor;

    AnchorToken(char a) {
        super();
        this.anchor = a;
    }

    public String toString() {
        return "Anchor: " + anchor;
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();

        switch (anchor) {
            case '$':
                return text_pos == text.length() && next.match(m);

            case '^':
                return 0 == text_pos && next.match(m);

            case 'b':
            case 'B': {
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