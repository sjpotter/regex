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
        // match minimum
        for (int i = 0; i < q.min; i++) {
            if (!t.match(m))
                return false;
        }
        
        if (q.greedy)
            return matchGreedy(m);
        
        return matchNotGreedy(m);
    }

    /*
        Greedy matching is complicated as can't use the function call stack for back tracking
        Therefore, we have 2 stacks, one as part of the Greedy function (text_pos) that allows us to reset
        the place we are matching in the text when we backtrack and the capturedGroup stack which is part of
        the Matcher class.
     */

    private boolean matchGreedy(Matcher m) throws RegexException {
        Stack<Integer> text_pos = new Stack<>();
        int old_text_pos = m.getTextPosition();

        // as greedy, match as much as possible and record backtrack positions
        for(int i=0; i < q.max-q.min || q.max == -1; i++) {
            if (t.match(m)) {
                text_pos.push(old_text_pos);
                old_text_pos = m.getTextPosition();
            } else {
                break;
            }
        }

        // try to match from here, but manual backtrack is necessary
        while (text_pos.size() > 0) {
            if (next.match(m)) {
                return true;
            }
            m.setTextPosition(text_pos.pop());
            m.popGroup(t.captureGroup());
        }

        return next.match(m);
    }

    private boolean matchNotGreedy(Matcher m) throws RegexException {
        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            int pos = m.getTextPosition();
            // try to match from here (starting with minimum match, adding one by one if can't match)
            if (next.match(m)) { // matched the minimum, see if the rest of text matches the rest of the regex
                return true;
            }

            // couldn't match rest of regex against rest of text
            // so now try to match one more (till maximum/infinity) before we retry
            m.setTextPosition(pos);
            if (!t.match(m))
                return false;
        }

        return false; // finished the max quantifier without finding regex match with the rest of the text
    }
}