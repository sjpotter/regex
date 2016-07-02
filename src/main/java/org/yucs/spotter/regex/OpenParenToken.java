package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class OpenParenToken extends Token {
    final int pos;
    int text_pos = -1;

    private List<Token> alts = new LinkedList<>();

    OpenParenToken(int p) {
        pos = p;
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

        // every alternate chains to the rest of the regex after its grouping
        // so it just becomes, test every alternate
        r.closeParens.push((CloseParenToken) next);

        while (it.hasNext()) {
            Token t = it.next();
            if (r.match(t, text_pos))
                return true;
        }

        return false;
    }
}