package org.yucs.spotter.regex;

import java.util.Stack;

// Positive and Negative Lookahead matching

class LookAheadExpressionToken extends Token implements TestableToken {
    final private NormalExpressionToken t;
    final private boolean positive;

    LookAheadExpressionToken(NormalExpressionToken net, boolean p) {
        t = net;
        positive = p;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        int pos = m.getTextPosition();

        // Empty stack as only matters that its string of tokens match
        Stack<Token> savedState = m.saveAndResetNextStack();

        boolean ret = t.match(m);

        m.restoreNextStack(savedState);
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