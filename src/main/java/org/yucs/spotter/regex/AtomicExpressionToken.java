package org.yucs.spotter.regex;

import java.util.Iterator;

class AtomicExpressionToken extends ExpressionToken {
    @Override
    public boolean match(Matcher m) throws RegexException {
        Iterator<Token> it = altIterator();

        int start = m.getTextPosition();

        while (it.hasNext()) {
            Token t = it.next();
            if (t.match(m)) {
                return next.match(m);
            }

            m.setTextPosition(start);
        }

        return false;
    }

}
