package org.yucs.spotter.regex;

class EndCaptureToken extends Token {
    final private StartCaptureToken start;
    final private int capture_pos;

    int invalid_capture = 1;

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

        // i.e. this isn't a valid path, so this isn't a valid capture.
        return false;
    }
}
