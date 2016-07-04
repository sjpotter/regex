package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// An Expression is a set of alternates
// An alternate is a list of tokens that are matched in order
// For an expression to be match, one of its alternates has to match
// Expressions are currently always captured

class ExpressionToken extends Token {
    final private int pos;
    final private List<Token> alts = new LinkedList<>();

    ExpressionToken(int p) {
        super();

        pos = p;

    }

    void addAlt(Token t) {
        alts.add(t);
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        Iterator<Token> it = alts.iterator();

        int start = m.getTextPosition();

        while (it.hasNext()) {
            Token t = it.next();
            if (t.match(m)) {
                m.pushGroup(pos, m.getText().substring(start, m.getTextPosition()));
                if (next.match(m))
                    return true;

                //failed, reset text position for next alternate
                m.setTextPosition(start);
                m.popGroup(pos);
            }
        }

        return false;
    }

    @Override
    int captureGroup() {
        return pos;
    }
}