package org.yucs.spotter.regex;

import java.util.Set;
import java.util.Stack;

class QuantifierNonGreedyToken extends QuantifierToken {
    QuantifierNonGreedyToken(Quantifier q, Token t) {
        this(q.min, q.max, t, false, null);
    }

    private QuantifierNonGreedyToken(int min, int max, Token t, boolean clone, Set<Integer> seen) {
        super(min, max, t, clone, seen);
    }

    @Override
    boolean maxQuantifierStrategy(Matcher m) throws RegexException {
        int startPos = m.getTextPosition();

        Stack<Token> savedState = m.saveNextStack();

        // try to match next, as not greedy, if that fails, try to match quantified token once before trying again
        boolean ret = next.match(m);
        if (!ret) {
            if (clone) {
                if (seen.contains(startPos)) {
                    return false;
                }
            }

            seen.add(startPos);
            m.setTextPosition(startPos);
            m.restoreNextStack(savedState);

            m.pushNextStack(cloneDecrement());
            return t.match(m);
        }

        return true;
    }

    @Override
    QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierNonGreedyToken(decrementMin(), decrementMax(), t, true, seen);
        qt.next = this.next;

        return qt;
    }
}
