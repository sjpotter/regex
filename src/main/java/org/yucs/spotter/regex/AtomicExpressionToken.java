package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.Stack;

// AtomicGroups are alternates where if an alternate matches, even if it fails later, the rest aren't tried

class AtomicExpressionToken extends ExpressionToken {
    @Override
    boolean match(Matcher m) throws RegexException {
        Iterator<Token> it = altIterator();
        int start = m.getTextPosition();

        while (it.hasNext()) {
            Stack<Token> savedStack = m.nextStack;
            m.nextStack = new Stack<>();

            Token t = it.next();
            boolean ret = t.match(m);

            if (ret) {
                m.nextStack = savedStack;
                return next.match(m);
            }

            m.nextStack = savedStack;
            m.setTextPosition(start);
        }

        return false;
    }
}
