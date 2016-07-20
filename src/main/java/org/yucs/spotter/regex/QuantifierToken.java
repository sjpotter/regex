package org.yucs.spotter.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class QuantifierToken extends Token {
    // Defines the quantification to be used
    final private Quantifier q;
    // The token list that is being quantified
    final private Token t;

    final private boolean clone;

    private Set<Integer> seen;

    QuantifierToken(Quantifier q, Token t) {
        this(q, t, false, null);
    }

    private QuantifierToken(Quantifier q, Token t, boolean clone, Set<Integer> seen) {
        super();

        this.q = q;
        this.t = t;
        this.clone = clone;
        this.seen = seen;
    }

    boolean match(Matcher m) throws RegexException {
        if (!clone)
            seen = new HashSet<>();

        // if haven't matched the minimum # of times yet, stick a decremented QuantifierToken on next stack and
        // try to match the token we are quantifying
        if (q.min != 0) {
            m.pushNextStack(cloneDecrement());
            return t.match(m);
        }

        // We've matched the minimum needed (0 for *, 1 for + or if specified in {,} so now unto max
        if (q.max != 0) { // max allows us to quantify more, so pick a strategy
            switch (q.matchType) {
                case GREEDY:
                    return matchGreedy(m);
                case NONGREEDY:
                    return matchNotGreedy(m);
                case POSESSIVE:
                    return matchPossessive(m);
            }

        }

        // max doesn't allow us to quantify more
        return next.match(m);
    }

    private boolean matchPossessive(Matcher m) throws RegexException {
        for(int i=0; i < q.max || q.max == -1; i++) {
            Stack<Token> savedState = m.saveAndResetNextStack();
            int startPos = m.getTextPosition();
            if (!t.match(m)) {
                m.restoreNextStack(savedState);
                m.setTextPosition(startPos);
                break;
            }
        }

        return next.match(m);
    }

    private boolean matchGreedy(Matcher m) throws RegexException {
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

    private boolean matchNotGreedy(Matcher m) throws RegexException {
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

    private QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierToken(this.q.cloneDecrement(), this.t, true, this.seen);
        qt.next = this.next;

        return qt;
    }

    Token reverse() throws RegexException {
        t.reverse();

        return super.reverse();
    }
}