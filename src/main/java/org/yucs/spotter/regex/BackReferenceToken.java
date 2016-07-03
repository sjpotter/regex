package org.yucs.spotter.regex;

class BackReferenceToken extends Token {
    private final int backreference;

    BackReferenceToken(int val) {
        super();
        backreference = val;
    }

    @Override
    public boolean match(Matcher m) throws RegexException {
        String text = m.getText();
        int text_pos = m.getTextPosition();
        String stored = m.getGroup(backreference);
        if (stored == null)
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
}