package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class OpenParenToken extends Token {
    final int pos;
    int text_pos = -1;
    private final CloseParenToken matched;

    private List<Token> alts = new LinkedList<>();

    OpenParenToken(int p, CloseParenToken cp) {
        pos = p;
        matched = cp;
    }

    void addAlt(Token t) {
        alts.add(t);
    }

    // TODO: Unsure how to display
    public String toString() { return "Group"; }

    @Override
    public boolean match(Regex r, int text_pos) throws RegexException {
        Iterator<Token> it = alts.iterator();

        this.text_pos = text_pos;

        r.closeParens.push(matched);

        while (it.hasNext()) {
            Token t = it.next();
            if (r.match(t, text_pos))
                return true;
        }

        return false;
    }
}