package org.yucs.spotter.regex;

import java.util.Stack;

class QuantifierPossessiveToken extends QuantifierToken {
    QuantifierPossessiveToken(Quantifier q, Token t) {
        this(q.min, q.max, t, false);
    }

    private QuantifierPossessiveToken(int min, int max, Token t, boolean clone) {
        super(min, max, t, clone);
    }

    @Override
    boolean maxQuantifierStrategy(Matcher m) throws RegexException {
        for(int i=0; i < max || max == -1; i++) {
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

    @Override
    QuantifierToken cloneDecrement() {
        QuantifierToken qt = new QuantifierPossessiveToken(decrementMin(), decrementMax(), t, true);
        qt.next = this.next;

        return qt;
    }
}