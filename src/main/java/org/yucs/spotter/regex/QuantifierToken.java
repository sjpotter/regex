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

    /*
        Greedy matching is complicated as can't use the function call stack for back tracking
        Therefore, we have 2 stacks, one as part of the Greedy function (text_pos) that allows us to reset
        the place we are matching in the text when we backtrack and the capturedGroup stack which is part of
        the Matcher class.
     */

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

    private boolean matchGreedyOld(Matcher m) throws RegexException {
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

    @Override
    Token reverse() throws RegexException {
        QuantifierToken cur = new QuantifierToken(q, t.reverse());

        return super.reverse(cur);
    }
}