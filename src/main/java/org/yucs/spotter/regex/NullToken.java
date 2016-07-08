package org.yucs.spotter.regex;

/*
 * NullToken is the Token that is at the end of all Complex Expressions
 * If this expression is consuming tokens, it will most likely have a token in the nextStack.
 * NullToken moves onto the token (or returns true if the nextStack is empty)
 */

class NullToken extends Token {
    final static NullToken Instance = new NullToken();

    @Override
    boolean match(Matcher m) throws RegexException {
        return m.matchNextStack();
    }

    @Override
    Token reverse() {
        return this;
    }
}
