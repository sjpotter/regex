package org.yucs.spotter.regex;

class StartCaptureToken extends Token {
    int start_pos;

    @Override
    boolean match(Matcher m) throws RegexException {
        start_pos = m.getTextPosition();
        return next.match(m);
    }
}
