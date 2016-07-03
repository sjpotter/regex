package org.yucs.spotter.regex;

import java.util.Stack;

class QuantifierToken extends Token {
    private final Quantifier q;
    private final Token t;

    QuantifierToken(Quantifier q, Token t) {
        this.q = q;
        this.t = t;
    }

    public boolean match(Matcher m) throws RegexException {
        // match minimum
        for (int i = 0; i < q.min; i++) {
            if (!m.match(t))
                return false;
        }
        
        if (q.greedy)
            return matchGreedy(m);
        
        return matchNotGreedy(m);
    }

    private boolean matchGreedy(Matcher m) throws RegexException {
        Stack<Integer> text_pos = new Stack<>();
        int old_text_pos = m.getTextPosition();

        // as greedy, match as much as possible
        for(int i=0; i < q.max-q.min || q.max == -1; i++) {
            if (m.match(t)) {
                text_pos.push(old_text_pos);
                old_text_pos = m.getTextPosition();
            } else {
                break;
            }
        }

        // try to match from here (consumed as much as possible) and if can't, back off one by one until minimum match
        while (text_pos.size() > 0) {
            if (m.match(next)) {
                return true;
            }
            m.setTextPosition(text_pos.pop());
        }

        return m.match(next);
    }

    private boolean matchNotGreedy(Matcher m) throws RegexException {
        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            // try to match from here (starting with minimum match, adding one by one if can't match)
            if (m.match(next)) { // matched the minimum, see if the rest of text matches the rest of the regex
                return true;
            }

            // couldn't match rest of regex against rest of text
            // so now try to match one more (till maximum/infinity) before we retry
            if (!m.match(t))
                return false;
        }

        return false; // finished the max quantifier without finding regex match with the rest of the text
    }
}