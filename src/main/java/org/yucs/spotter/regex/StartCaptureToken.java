package org.yucs.spotter.regex;

class StartCaptureToken extends Token {
    int start_pos;

    @Override
    boolean match(Matcher m) throws RegexException {
        int old_start_pos = start_pos;

        start_pos = m.getTextPosition();
        boolean ret = next.match(m);

        if (!ret)
            start_pos = old_start_pos;

        return ret;
    }
}
