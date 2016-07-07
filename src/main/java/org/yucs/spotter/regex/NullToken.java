package org.yucs.spotter.regex;

class NullToken extends Token {
    final static NullToken Instance = new NullToken();

    @Override
    boolean match(Matcher m) throws RegexException {
        return m.nextStack.size() == 0 || m.nextStack.pop().match(m);
    }

    @Override
    Token reverse() {
        return this;
    }
}
