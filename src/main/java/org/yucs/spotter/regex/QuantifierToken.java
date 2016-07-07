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
        Stack<Token> savedStack = m.nextStack;
        for (int i = 0; i < q.min; i++) {
            m.nextStack = new Stack<>();
            if (!t.match(m)) {
                m.nextStack = savedStack;
                return false;
            }
        }
        m.nextStack = savedStack;
        
        if (q.greedy)
            return matchGreedy(m);
        
        return matchNotGreedy(m);
    }

    // Greedy matching is really annoying, as so much state has to be reset and hard to jump back into proper position.
    // Therefore, unoptimally, we figure out the maximum # of matches that this greedy matcher matches
    // Then match that amount-1 without the nextStack and then finally match once more with the next stack.
    // iterate on amount down to 1.  If 1 didn't pass, just continue with next token (as already matched minimum above)
    // TODO: though now that I think about it, that's buggy too, as need to consider nextToken there as well.
    //       might have to put minimum-1 into nextToken set and see if can finish from minimum. this is complicated

    private boolean matchGreedy(Matcher m) throws RegexException {
        int max = calcMaxGreedy(m);
        int start = m.getTextPosition();

        for(int i=max; i > 0; i--) {
            if (greedyInnerLoop(m, i))
                return true;
            m.setTextPosition(start);
        }

        return next.match(m);
    }

    private int calcMaxGreedy(Matcher m) throws RegexException {
        Stack<Token> savedStack = m.nextStack;
        m.nextStack = new Stack<>();

        int start = m.getTextPosition();
        int i;
        for(i=0; i < q.max-q.min || q.max == -1; i++) {
            if (!t.match(m)) {
                break;
            }
        }

        m.setTextPosition(start);
        m.nextStack = savedStack;

        return i;
    }

    private boolean greedyInnerLoop(Matcher m, int consume) throws RegexException {
        Stack<Token> savedStack = m.nextStack;
        m.nextStack = new Stack<>();

        for(int i=0; i < consume-1; i++) // we know this works, as tested above
            t.match(m);

        m.nextStack = new Stack<>();
        m.nextStack.addAll(savedStack);
        m.nextStack.push(next);

        boolean ret = t.match(m);
        if (ret)
            return true;

        m.nextStack = savedStack;
        return false;
    }

    private boolean matchNotGreedy(Matcher m) throws RegexException {
        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            int pos = m.getTextPosition();
            Stack<Token> savedStack = m.nextStack;

            m.nextStack = new Stack<>();
            m.nextStack.addAll(savedStack);

            // try to match from here (starting with minimum match, adding one by one if can't match)
            if (next.match(m)) { // matched the minimum, see if the rest of text matches the rest of the regex
                return true;
            }

            m.nextStack = new Stack<>();

            // couldn't match rest of regex against rest of text
            // so now try to match one more (till maximum/infinity) before we retry
            m.setTextPosition(pos);
            if (!t.match(m))
                return false;

            m.nextStack = savedStack;
        }

        return false; // finished the max quantifier without finding regex match with the rest of the text
    }

    @Override
    Token reverse() throws RegexException {
        QuantifierToken cur = new QuantifierToken(q, t.reverse());

        return super.reverse(cur);
    }
}