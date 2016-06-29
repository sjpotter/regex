package org.yucs.spotter.regex;

class Token {
    final CharacterClass c;
    final Quantifier q;
    final char anchor;
    Token next = null;

    private Token(char a) {
        this.c = null;
        this.q = null;
        this.anchor = a;
    }

    private Token(CharacterClass c, Quantifier q) {
        this.c = c;
        this.q = q;
        anchor = 0;
    }

    static Token tokenize(String regex, int regex_pos) throws Exception {
        if (regex_pos == regex.length()) {
            return null;
        }

        if (regex_pos == 0 && regex.charAt(regex_pos) == '^') {
            Token t = new Token('^');
            t.next = tokenize(regex, regex_pos + 1);
            return t;
        }

        if (regex_pos + 1 == regex.length() && regex.charAt(regex_pos) == '$') {
            return new Token('$');
        }

        CharacterClassWrapper w = CharacterClassWrapper.getCharacterClass(regex, regex_pos);
        regex_pos = w.regex_pos;
        Quantifier q = Quantifier.parse(regex, regex_pos);
        if (q != null) {
            regex_pos = q.regex_pos;
        }

        Token t = new Token(w.c, q);
        t.next = tokenize(regex, regex_pos);

        return t;
    }

}