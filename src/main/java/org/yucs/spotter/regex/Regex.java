package org.yucs.spotter.regex;

import org.yucs.spotter.regex.token.*;

import java.util.Iterator;
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
                //System.out.println(matcher);

                if (matcher.match(text)) {
                    System.out.println(text + " matched regex " + regex);
                } else {
                    System.out.println(text + " didn't match regex " + regex);
                }
            } catch (RegexException e) {
                System.out.println(e.toString());
            }
        }
    }

    @SuppressWarnings("WeakerAccess") // Needs to be public to be usable elsewhere
    public Regex(String r) throws RegexException {
        t = TokenFactory.tokenize(r, 0);
    }

    /*
    public String toString() {
        Token t = this.t;
        StringBuilder sb = new StringBuilder();

        while (t != null) {
            sb.append(t).append("\n");
            t = t.next;
        }

        if (sb.length() > 0)
            sb.setLength(sb.length()-1);

        return sb.toString();
    } */

    @SuppressWarnings("WeakerAccess") // Need to be public to be usable elsewhere
    public boolean match(String text) throws RegexException {
        for(int i=0; i < text.length() || i == 0; i++) { //need to test empty text string too
            if (match(t, text, i, null))
                return true;
        }

        return false;
    }

    private boolean match(Token t, String text, int text_pos, Token future) throws RegexException {
        // if we matched every token, we've finished regex, so it passes
        if (t == null) {
            if (future == null) { // only finished if no future (i.e. after alternatives enclosed in a group (|)
                return true;
            } else { // if finished the token set of the alternative, continue after the alternative
                t = future;
                future = null;
            }
        }

        switch (t.type) {
            case ANCHOR:
                AnchorToken at = (AnchorToken) t;
                if (at.anchor == '$') {
                    return text.length() == text_pos;
                } else if (at.anchor == '^') {
                    return text_pos == 0 && match(t.next, text, text_pos, future);
                } else {
                    throw new RegexException("Unexpected ANCHOR token: " + at.anchor);
                }

            case ALT:
                return matchAlternates(text, text_pos, (AltToken) t);

            case CHARACTER_CLASS:
                CharacterToken ct = (CharacterToken) t;

                if (ct.q != null)
                   return matchRange(text, text_pos, ct.c, ct.q, t.next, future);

                // 1. if we have no more text (i.e. text_pos >= text.length() short cut and return false
                // 2. see if this character matches, if not short cut and return false
                // 3. if passed above, continue matching rest of regex against rest of text
                return text_pos < text.length() && ct.c.match(text.charAt(text_pos)) && match(t.next, text, text_pos + 1, future);

            default:
                throw new RegexException("Unknown TokenType: " + t.type);
        }
    }

    private boolean matchRange(String text, int text_pos, CharacterClass c, Quantifier q, Token t, Token future) throws RegexException {
        for (int i = 0; i < q.min; i++) { // can we match the minimum number of characters?
            if (text.length() > text_pos && c.match(text.charAt(text_pos))) {
                text_pos++;
            } else {
                return false;
            }
        }

        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            if (match(t, text, text_pos, future)) { // matched the minimum, see if the rest of text matches the rest of the regex
                return true;
            }
            // couldn't match rest of regex against rest of text
            // so now try to match one more (till maximum/infinity) before we retry
            if (text.length() != text_pos && c.match(text.charAt(text_pos))) {
                text_pos++; // eat one character from text if it matches regex character
            } else {
                return false; // reached end of text without matching the rest of the regex
            }
        }

        return false; // finished the max quantifier without finding regex match with the rest of the text
    }

    private boolean matchAlternates(String text, int text_pos, AltToken alts) throws RegexException {
        Iterator<Token> it = alts.getAlts();

        // every alternate chains to the rest of the regex after its grouping
        // so it just becomes, test every alternate
        while (it.hasNext()) {
            Token t = it.next();
            if (match(t, text, text_pos, alts.next))
                return true;
        }

        return false;
    }
}