package org.yucs.spotter.regex;

/*
 * EndCapture saves the text between it and its matching StartCaptureToken.
 * It also saves the previous state so if the matching along this path fails later, the state can be restored
 */

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

        // this isn't a valid path, so this isn't a valid capture.
        m.popGroup(capture_pos);
        return false;
    }
}
