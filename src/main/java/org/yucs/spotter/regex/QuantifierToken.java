package org.yucs.spotter.regex;

import java.util.Stack;

class QuantifierToken extends Token {
    // Defines the quantification to be used
    private final Quantifier q;
    // The token list that is being quantified
    private Token t;

    QuantifierToken(Quantifier q, Token t) {
        super();

        this.q = q;
        this.t = t;
    }

    boolean match(Matcher m) throws RegexException {
        // if haven't matched the minimum # of times yet, stick a decremented QuantifierToken on next stack and
        // try to match the token we are quantifying
        if (q.min != 0) {
            m.pushNextStack(cloneDecrement());
            return t.match(m);
        }

        // We've matched the minimum needed (0 for *, 1 for + or if specified in {,} so now unto max
        if (q.max != 0) { // max allows us to quantify more, so pick a strategy
            if (q.greedy)
                return matchGreedy(m);
            else {
                return matchNotGreedy(m);
            }

        }

        // max doesn't allow us to quantify more
        return next.match(m);
    }

    private boolean matchGreedy(Matcher m) throws RegexException {
        int startPos = m.getTextPosition();

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

        // try to match next, if that fails, try to match quantified token before trying again
        boolean ret = next.match(m);
        if (!ret) {
            m.setTextPosition(startPos);
            m.restoreNextStack(savedState);
            m.pushNextStack(cloneDecrement());
            return t.match(m);
        }

        return true;
    }

    private QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierToken(this.q.cloneDecrement(), this.t);
        qt.next = this.next;

        return qt;
    }

    Token reverse() throws RegexException {
        t.reverse();

        return super.reverse();
    }
}