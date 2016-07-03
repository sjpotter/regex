package org.yucs.spotter.regex;

class CharacterToken extends Token {
    private final CharacterClass c;

    CharacterToken(CharacterClass c) {
        this.c = c;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (c == null) throw new AssertionError();

        return "CharacterClass: " + c.toString();
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();

        if (text_pos < text.length()) {
            if (c.match(text.charAt(text_pos))) {
                m.setTextPosition(text_pos+1);
                return m.match(next);
            }
        }

        return false;
    }
}
