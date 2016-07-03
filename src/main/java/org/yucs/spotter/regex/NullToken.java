package org.yucs.spotter.regex;

class NullToken extends Token {
    final static NullToken Instance = new NullToken();

    @Override
    public boolean match(Matcher m) throws RegexException {
        return true;
    }
}
