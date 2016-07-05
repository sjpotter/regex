package org.yucs.spotter.regex;

class LookAheadExpressionToken extends Token implements TestableToken {
    final private NormalExpressionToken t;

    LookAheadExpressionToken(NormalExpressionToken net) {
        t = net;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        int pos = m.getTextPosition();

        boolean ret = t.match(m);

        m.setTextPosition(pos);

        return ret && next.match(m);
    }
}
