package org.yucs.spotter.regex;

import java.util.Scanner;

// Inspired by Rob Pike's implementation in TPOP:
// http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html

public class Regex {
    private Token t;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String regex = sc.next();
            String text = sc.next();

            try {
                Regex matcher = new Regex(regex);
                System.out.println(matcher);

                if (matcher.match(text)) {
                    System.out.println(text + " matched regex " + regex);
                } else {
                    System.out.println(text + " didn't match regex " + regex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("WeakerAccess") // Needs to be public to be usable elsewhere
    public Regex(String r) throws Exception {
        t = Token.tokenize(r, 0);
    }

    public String toString() {
        Token t = this.t;
        StringBuilder sb = new StringBuilder();

        while (t != null) {
            if (t.anchor != 0) {
                sb.append("Anchor: ").append(t.anchor).append('\n');
            } else {
                sb.append("CharacterClass: ").append(t.c.toString()).append(", ");
                if (t.q != null) {
                    sb.append("Quantifier: min = ").append(t.q.min).append(" max = ").append(t.q.max).append('\n');
                } else {
                    sb.append("Quantifier: null\n");
                }
            }
            t = t.next;
        }

        if (sb.length() > 0)
            sb.setLength(sb.length()-1);

        return sb.toString();
    }

    @SuppressWarnings("WeakerAccess") // Need to be public to be usable elsewhere
    public boolean match(String text) {
        if (t.anchor == '^')
            return match(t.next, text, 0);

        for(int i=0; i < text.length(); i++) {
            if (match(t, text, i))
                return true;
        }

        return false;
    }

    private boolean match(Token t, String text, int text_pos) {
        if (t == null) {
            return true;
        }

        if (t.anchor == '$') {
            return text.length() == text_pos;
        }

        if (t.q != null) {
            return matchRange(text, text_pos, t.c, t.q, t.next);
        }

        return t.c.match(text.charAt(text_pos)) && match(t.next, text, text_pos + 1);
    }

    private boolean matchRange(String text, int text_pos, CharacterClass c, Quantifier q, Token t) {
        for (int i = 0; i < q.min; i++) { // can we match the minimum number of characters?
            if (text.length() > text_pos && c.match(text.charAt(text_pos))) {
                text_pos++;
            } else {
                return false;
            }
        }

        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            if (match(t, text, text_pos)) { // matched the minimum, see if the rest of text matches the regex without consuming more of the regex
                return true;
            }
            // couldn't match rest of text, so now try to match one more (till maximum/infinity) before we retry
            if (text.length() != text_pos && c.match(text.charAt(text_pos))) {
                text_pos++; // eat one character from text if it matches regex character
            } else {
                return false; // reached end of text or didn't match regex char
            }
        }

        return false;
    }
}