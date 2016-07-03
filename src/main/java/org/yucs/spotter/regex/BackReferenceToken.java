package org.yucs.spotter.regex;

class BackReferenceToken extends Token {
    private final int backreference;

    BackReferenceToken(int val) {
        backreference = val;
    }

    @Override
    public boolean match(Regex r, int text_pos) throws RegexException {
        String stored = r.groups.get(backreference);
        if (stored == null)
            return false;

        for(int i=0; i < stored.length(); i++) {
            if (text_pos >= r.text.length() || stored.charAt(i) != r.text.charAt(text_pos)) {
                return false;
            }
            text_pos++;
        }

        return r.match(next, text_pos);
    }
}
