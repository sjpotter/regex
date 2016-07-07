package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.Stack;

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
