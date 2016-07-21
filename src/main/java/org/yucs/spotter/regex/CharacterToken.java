package org.yucs.spotter.regex;

// CharacterToken matches against a single character in the text based on the character class that it contains
// It moves the text forwards or backwards depending on the direction we are currently moving in the match (ex: look behinds)

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

        int iterator = m.getDirection();
        if (iterator == -1) {
            if (text_pos == 0) {
                return false;
            }
            text_pos--;
        }

        // "< text.length()" for forward movement, ">= 0" for backwards movement
        if (text_pos < text.length() && text_pos >= 0) {
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
