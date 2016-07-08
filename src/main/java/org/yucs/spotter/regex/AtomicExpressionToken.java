package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.Stack;

/*
 * AtomicGroups are alternates where if an alternate matches, even if it fails later, the rest aren't tried, i.e. no
 * backtracking once an alternate matches
 */

class AtomicExpressionToken extends ExpressionToken {
    @Override
    boolean match(Matcher m) throws RegexException {
        Iterator<Token> it = altIterator();
        int start = m.getTextPosition();

        while (it.hasNext()) {
            // We do this, as we only want to test a single alternate at a time,

            Stack<Token> savedStack = m.saveAndResetNextStack();

            Token t = it.next();

            boolean ret = t.match(m);

            if (ret) {
                // once a full alternate matches (i.e. with no nextStack tokens), we don't try any other.
                // so once an alternate returns true, we restore the nextStack and continue with next
                m.restoreNextStack(savedStack);
                return next.match(m);
            }

            m.restoreNextStack(savedStack);
            m.setTextPosition(start);
        }

        return false;
    }
}
