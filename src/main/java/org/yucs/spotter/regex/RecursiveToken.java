package org.yucs.spotter.regex;

class RecursiveToken extends Token {
    final private int captureGroup;

    RecursiveToken(int capture) {
        captureGroup = capture;
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        Matcher m1 = m.copy();

        NormalExpressionToken t = (NormalExpressionToken) m.getCaptureToken(captureGroup);

        boolean ret = t.normalMatcher(m1, false);
        if (ret)
            m.setTextPosition(m1.getTextPosition());

        return ret;
    }
}