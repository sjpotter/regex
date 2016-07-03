package org.yucs.spotter.regex;

import java.util.Stack;

class QuantifierToken extends Token {
    private final Quantifier q;
    private final Token t;

    QuantifierToken(Quantifier q, Token t) {
        this.q = q;
        this.t = t;
    }

    public boolean match(Regex r) throws RegexException {
        // match minimum
        for (int i = 0; i < q.min; i++) {
            if (!r.match(t))
                return false;
        }
        
        if (q.greedy)
            return matchGreedy(r);
        
        return matchNotGreedy(r);
    }

    private boolean matchGreedy(Regex r) throws RegexException {
        Stack<Integer> text_pos = new Stack<>();
        int old_text_pos = r.text_pos;

        // as greedy, match as much as possible
        for(int i=0; i < q.max-q.min || q.max == -1; i++) {
            if (r.match(t)) {
                text_pos.push(old_text_pos);
                old_text_pos = r.text_pos;
            } else {
                break;
            }
        }

        // try to match from here (consumed as much as possible) and if can't, back off one by one until minimum match
        while (text_pos.size() > 0) {
            if (r.match(next)) {
                return true;
            }
            r.text_pos = text_pos.pop();
        }

        return r.match(next);
    }

    private boolean matchNotGreedy(Regex r) throws RegexException {
        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            // try to match from here (starting with minimum match, adding one by one if can't match)
            if (r.match(next)) { // matched the minimum, see if the rest of text matches the rest of the regex
                return true;
            }

            // couldn't match rest of regex against rest of text
            // so now try to match one more (till maximum/infinity) before we retry
            if (!r.match(t))
                return false;
        }

        return false; // finished the max quantifier without finding regex match with the rest of the text
    }
}
