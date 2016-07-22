package org.yucs.spotter.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class QuantifierGreedyToken extends QuantifierToken {
    private Set<Integer> seen;

    QuantifierGreedyToken(Quantifier q, Token t) {
        this(q.min, q.max, t, false, null);
    }

    private QuantifierGreedyToken(int min, int max, Token t, boolean clone, Set<Integer> seen) {
        super(min, max, t, clone);
        this.seen = seen;
    }

    @Override
    boolean maxQuantifierStrategy(Matcher m) throws RegexException {
        if (!clone || seen == null)
            seen = new HashSet<>();

        int startPos = m.getTextPosition();

        if (clone) {
            if (seen.contains(startPos)) {
                return next.match(m);
            }
        }

        seen.add(startPos);

        Stack<Token> savedState = m.saveThenPushNextStack(cloneDecrement());

        // try to match quantified token greedily, if greedily fails, go to next;
        boolean ret = t.match(m);
        if (!ret) {
            m.restoreNextStack(savedState);
            m.setTextPosition(startPos);
            return next.match(m);
        }

        return true;
    }

    @Override
    QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierGreedyToken(decrementMin(), decrementMax(), t, true, seen);
        qt.next = this.next;

        return qt;
    }
}
