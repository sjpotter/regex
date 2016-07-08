package org.yucs.spotter.regex;

class RecursiveToken extends Token {
    final private int captureGroup;

    RecursiveToken(int capture) {
        captureGroup = capture;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        NormalExpressionToken t = m.getCaptureToken(captureGroup);

        m.pushNextStack(new RecursiveEndToken(m, next));

        Matcher m1 = m.copy();

        return t.matchNoFollow(m1);
    }

    @Override
    Token reverse() throws RegexException {
        throw new RegexException("Can't LookBehind with Recursive Tokens");
    }
}