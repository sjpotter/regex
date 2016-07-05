package org.yucs.spotter.regex;

class RecursiveToken extends Token {
    final private int captureGroup;

    RecursiveToken(int capture) {
        captureGroup = capture;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        Matcher m1 = m.copy();

        NormalExpressionToken t = m.getCaptureToken(captureGroup);

        boolean ret = t.matchNoFollow(m1);
        if (ret)
            m.setTextPosition(m1.getTextPosition());

        return ret;
    }

    @Override
    Token reverse() throws RegexException {
        throw new RegexException("Can't LookBehind with Recursive Tokens");
    }
}