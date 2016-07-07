package org.yucs.spotter.regex;

class EndCaptureToken extends Token {
    final private StartCaptureToken start;
    final private int capture_pos;

    EndCaptureToken(Token t, int pos) {
        start = (StartCaptureToken) t;
        capture_pos = pos;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        m.pushGroup(capture_pos, m.getText().substring(start.start_pos, m.getTextPosition()));
        if (next.match(m))
            return true;

        m.popGroup(capture_pos);
        return false;
    }
}
