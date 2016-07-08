package org.yucs.spotter.regex;

import java.util.Stack;

class IfThenElseToken extends Token {
    final private Token ifToken;
    final private Token thenToken;
    final private Token elseToken;

    IfThenElseToken(Token ifToken, Token thenToken, Token elseToken) throws RegexException {
        if (!(ifToken instanceof TestableToken))
            throw new RegexException("IfThenElseToken: ifToken not a TestableToken");

        this.ifToken = ifToken;
        this.thenToken = thenToken;
        this.elseToken = elseToken;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        // Empty stack for if clause, as only the tokens within it define true/false for the then/else clauses
        Stack<Token> savedStack = m.saveAndResetNextStack();
        boolean ret = ifToken.match(m);

        // stack is returned for then/else clause as they continue matching next tokens.
        m.restoreNextStack(savedStack);

        Token exec;
        if (ret) {
            exec = thenToken;
        } else {
            exec = elseToken;
        }

        return exec.match(m);
    }

    @Override
    Token reverse() throws RegexException {
        Token cur = new IfThenElseToken(ifToken.reverse(), thenToken.reverse(), elseToken.reverse());

        return super.reverse(cur);
    }
}