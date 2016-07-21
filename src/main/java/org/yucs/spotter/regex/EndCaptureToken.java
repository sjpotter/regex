package org.yucs.spotter.regex;

/*
 * Dynamically inserted into the nextStack when a start captureToken is executed
 * If reached, captures the state between start_pos (where StartCaptureToken was) and current text position
 * If we fail matching after it, revert the capture, by popping it off the capture stack.
 */

class EndCaptureToken extends Token {
    final private int capture;
    final private int start_pos;

    EndCaptureToken(int capture, int start_pos) {
        this.capture = capture;
        this.start_pos = start_pos;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        m.pushGroup(capture, m.getText().substring(start_pos, m.getTextPosition()));

        if (next.match(m))
            return true;

        // this isn't a valid path, so this isn't a valid capture.
        m.popGroup(capture);
        return false;
    }
}
