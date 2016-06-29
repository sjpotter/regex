package org.yucs.spotter.regex;

class Token {
    private enum tokenType { ANCHOR, CHARACTER_CLASS }

    final CharacterClass c;
    final Quantifier q;
    final char anchor;
    private final tokenType type;
    Token next = null;

    private Token(char a) {
        type = tokenType.ANCHOR;
        this.c = null;
        this.q = null;
        this.anchor = a;
    }

    private Token(CharacterClass c, Quantifier q) {
        type = tokenType.CHARACTER_CLASS;
        this.c = c;
        this.q = q;
        anchor = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case ANCHOR:
                sb.append("Anchor: ").append(anchor);
                break;
            case CHARACTER_CLASS:
                if (c == null) throw new AssertionError();
                sb.append("CharacterClass: ").append(c.toString()).append(", ");
                if (q != null) {
                    sb.append("Quantifier: min = ").append(q.min).append(" max = ").append(q.max);
                } else {
                    sb.append("Quantifier: null");
                }
                break;
        }
        return sb.toString();
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

        CharacterClassWrapper ccw = CharacterClassWrapper.getCharacterClass(regex, regex_pos);
        regex_pos = ccw.regex_pos;
        QuantifierWrapper qw = QuantifierWrapper.parse(regex, regex_pos);
        Quantifier q = null;
        if (qw != null) {
            regex_pos = qw.regex_pos;
            q = qw.q;
        }

        Token t = new Token(ccw.c, q);
        t.next = tokenize(regex, regex_pos);

        return t;
    }

}