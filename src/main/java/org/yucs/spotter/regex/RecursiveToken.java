package org.yucs.spotter.regex;

class RecursiveToken extends Token {
    final private int captureGroup;

    RecursiveToken(int capture) {
        captureGroup = capture;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        NormalExpressionToken t = m.getCaptureToken(captureGroup);

        // As recursive consumes state, need to have nextStack work correctly, this token resets the matcher state
        // for future matches after it executes
        m.pushNextStack(new RecursiveEndToken(m, next));

        Matcher m1 = m.copy();

        return t.matchNoFollow(m1);
    }

    @Override
    Token reverse() throws RegexException {
        throw new RegexException("Can't LookBehind with Recursive Tokens");
    }
}