package org.yucs.spotter.regex;

// Positive and Negative Look behind matching

import java.util.Stack;

class LookBehindExpressionToken extends Token implements TestableToken {
    final private NormalExpressionToken t;
    final private boolean positive;

    LookBehindExpressionToken(NormalExpressionToken net, boolean p) {
        t = net;
        positive = p;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        int pos = m.getTextPosition();
        m.setIterator(-1);

        // Empty stack as only matters that its string of tokens match
        Stack<Token> savedState = m.nextStack;
        m.nextStack = new Stack<>();

        boolean ret = t.match(m);

        m.nextStack = savedState;

        m.setIterator(1);
        m.setTextPosition(pos);

        if (positive) {
            if (!ret)
                return false;
        } else {
            if (ret)
                return false;
        }

        return next.match(m);
    }
}
