package org.yucs.spotter.regex;

class LookBehindExpressionToken extends Token implements TestableToken {
    final private NormalExpressionToken t;
    final private boolean positive;

    LookBehindExpressionToken(NormalExpressionToken net, boolean p) {
        t = net;
        positive = p;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        int pos = m.getTextPosition();
        m.setIterator(-1);

        boolean ret = t.match(m);

        m.setIterator(1);
        m.setTextPosition(pos);


        if (positive) {
            if (!ret)
                return false;
        } else {
            if (ret)
                return false;
        }

        return next.match(m);
    }
}
