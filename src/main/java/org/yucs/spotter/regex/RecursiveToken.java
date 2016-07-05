package org.yucs.spotter.regex;

class RecursiveToken extends Token {
    final private int captureGroup;

    RecursiveToken(int capture) {
        captureGroup = capture;
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        Matcher m1 = m.copy();

        boolean ret = m.getCaptureToken(captureGroup).match(m1);
        if (ret)
            m.setTextPosition(m1.getTextPosition());

        return ret;
    }
}
