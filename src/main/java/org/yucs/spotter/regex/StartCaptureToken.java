package org.yucs.spotter.regex;

class StartCaptureToken extends Token {
    final private Token t;
    final private int capture;

    StartCaptureToken(int capture, Token t) {
        this.capture = capture;
        this.t = t;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        int start_pos = m.getTextPosition();

        Token end = new EndCaptureToken(capture, start_pos);
        end.next = next;
        m.pushNextStack(end);

        return t.match(m);
    }
}
