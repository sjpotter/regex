package org.yucs.spotter.regex;

/*
 * BackReference gets the captured group state from a previous group and checks if the text matches that here
 */
class BackReferenceToken extends Token {
    private final int backreference;

    BackReferenceToken(int val) {
        super();
        backreference = val;
    }

    @Override
    boolean match(Matcher m) throws RegexException {
        if (m.getIterator() != -1) {
            return forwardMatch(m);
        }

        return reverseMatch(m);
    }

    private boolean forwardMatch(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();
        String stored = m.getGroup(backreference);

        if (stored == null) // TODO: unsure this is correct, maybe its an automatic and continue to next match
            return false;

        for(int i=0; i < stored.length(); i++) {
            if (text_pos >= text.length() || stored.charAt(i) != text.charAt(text_pos)) {
                return false;
            }
            text_pos++;
        }

        m.setTextPosition(text_pos);

        return next.match(m);
    }

    private boolean reverseMatch(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition() - 1;
        String stored = m.getGroup(backreference);

        if (stored == null) // TODO: unsure this is correct, maybe its an automatic and continue to next match
            return false;

        for(int i=stored.length() -1; i >= 0; i--) {
            if (text_pos < 0 || stored.charAt(i) != text.charAt(text_pos)) {
                return false;
            }
            text_pos--;
        }

        m.setTextPosition(text_pos);

        return next.match(m);
    }
}