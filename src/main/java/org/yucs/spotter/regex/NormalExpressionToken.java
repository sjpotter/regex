package org.yucs.spotter.regex;

import java.util.Iterator;

// An Expression is a set of alternates
// An alternate is a list of tokens that are matched in order
// For an expression to be match, one of its alternates has to match
// Expressions are currently always captured

class NormalExpressionToken extends ExpressionToken {
    final private int pos;
    final private boolean capturing;

    NormalExpressionToken(int p) {
        pos = p;

        capturing = p != -1;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        return internalMatch(m, true);
    }

    boolean matchNoFollow(Matcher m) throws RegexException {
        return internalMatch(m, false);
    }

    @Override
    int captureGroup() {
        return pos;
    }

    private boolean internalMatch(Matcher m, boolean goNext) throws RegexException {
        Iterator<Token> it = altIterator();

        int start = m.getTextPosition();

        while (it.hasNext()) {
            Token t = it.next();
            if (t.match(m)) {
                if (capturing)
                    m.pushGroup(pos, m.getText().substring(start, m.getTextPosition()));
                if (!goNext)
                    return true;

                if (next.match(m))
                    return true;

                //failed, remove captured group
                if (capturing)
                    m.popGroup(pos);
            }
            // failed, reset text position for next alternate
            m.setTextPosition(start);
        }

        return false;
    }
}