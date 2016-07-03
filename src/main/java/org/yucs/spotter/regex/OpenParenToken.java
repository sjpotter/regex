package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class OpenParenToken extends Token {
    final int pos;

    @SuppressWarnings({"FieldCanBeLocal", "unused"}) // might need it later
    final private CloseParenToken matched;

    final private List<Token> alts = new LinkedList<>();

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
    public boolean match(Matcher m) throws RegexException {
        Iterator<Token> it = alts.iterator();

        m.setParenPosition(pos, m.getTextPosition());

        while (it.hasNext()) {
            Token t = it.next();
            if (m.match(t))
                if (m.match(next))
                    return true;
        }

        return false;
    }
}