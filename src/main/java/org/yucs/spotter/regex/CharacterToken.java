package org.yucs.spotter.regex;

class CharacterToken extends Token {
    private final CharacterClass c;

    CharacterToken(CharacterClass c) {
        super();
        this.c = c;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();

        int iterator = m.getIterator();
        if (iterator == -1) {
            if (text_pos == 0) {
                return false;
            }
            text_pos--;
        }

        if (text_pos < text.length()) {
            if (c.match(text.charAt(text_pos))) {
                if (iterator == 1)
                    m.setTextPosition(text_pos+1);
                else
                    m.setTextPosition(text_pos);
                return next.match(m);
            }
        }

        return false;
    }
}
