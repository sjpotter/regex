package org.yucs.spotter.regex;

import java.util.Stack;

class QuantifierToken extends Token {
    private final Quantifier q;
    private final Token t;

    QuantifierToken(Quantifier q, Token t) {
        super();

        this.q = q;
        this.t = t;
    }

    boolean match(Matcher m) throws RegexException {
        // if haven't matched the minimum # of times yet, stick a decrement QuantifierToken on next stack and
        // try to match the token we are quantifying
        if (q.min != 0) {
            m.nextStack.push(cloneDecrement());
            return t.match(m);
        }

        if (q.max != 0) {
            if (q.greedy)
                return matchGreedy(m);
            else {
                return matchNotGreedy(m);
            }

        }

        return next.match(m);
    }

    private boolean matchGreedy(Matcher m) throws RegexException {
        Stack<Token> savedState = m.nextStack;
        m.nextStack = new Stack<>();
        m.nextStack.addAll(savedState);

        int startPos = m.getTextPosition();

        // try to match quantified token greedily, if greedily fails, go to next;
        m.nextStack.push(cloneDecrement());
        boolean ret = t.match(m);
        if (!ret) {
            m.nextStack = savedState;
            m.setTextPosition(startPos);
            return next.match(m);
        }

        return true;
    }

    private boolean matchNotGreedy(Matcher m) throws RegexException {
        Stack<Token> savedState = m.nextStack;
        m.nextStack = new Stack<>();
        m.nextStack.addAll(savedState);

        int startPos = m.getTextPosition();

        // try to match next, if that fails, try to match quantified token before trying again
        boolean ret = next.match(m);
        if (!ret) {
            m.setTextPosition(startPos);
            m.nextStack = savedState;
            m.nextStack.push(cloneDecrement());
            return t.match(m);
        }

        return true;
    }

    private QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierToken(this.q.cloneDecrement(), this.t);
        qt.next = this.next;

        return qt;
    }

}