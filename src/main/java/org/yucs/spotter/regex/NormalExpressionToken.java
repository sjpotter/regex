package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.Stack;

// An Expression is a set of alternates
// An alternate is a list of tokens that are matched in order
// For an expression to be match, one of its alternates has to match
// Expressions are currently always captured

class NormalExpressionToken extends ExpressionToken {
    @Override
    boolean match(Matcher m) throws RegexException {
        return internalMatch(m, true);
    }

    boolean matchNoFollow(Matcher m) throws RegexException {
        return internalMatch(m, false);
    }

    private boolean internalMatch(Matcher m, boolean goNext) throws RegexException {
        Iterator<Token> it = altIterator();

        int start = m.getTextPosition();

        while (it.hasNext()) {
            Stack<Token> savedStack = m.saveNextStack();

            if (goNext)
                m.pushNextStack(next);

            Token t = it.next();
            if (t.match(m))
                return true;

            m.restoreNextStack(savedStack);
            m.setTextPosition(start);
        }

        return false;
    }
}