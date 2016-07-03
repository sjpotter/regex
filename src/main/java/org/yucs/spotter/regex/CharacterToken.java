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
    public boolean match(Regex r) throws RegexException {
        if (r.text_pos < r.text.length()) {
            if (c.match(r.text.charAt(r.text_pos))) {
                r.text_pos++;
                return r.match(next);
            }
        }

        return false;
    }
}
