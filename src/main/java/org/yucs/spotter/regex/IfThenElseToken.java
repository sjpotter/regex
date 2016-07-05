package org.yucs.spotter.regex;

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
        if (ifToken.match(m)) {
            return thenToken.match(m);
        }

        return elseToken.match(m);
    }
}