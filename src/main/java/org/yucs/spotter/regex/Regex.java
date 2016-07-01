package org.yucs.spotter.regex;

import org.yucs.spotter.regex.token.*;

import java.util.Iterator;
import java.util.Scanner;

import static org.yucs.spotter.regex.token.Token.tokenType.ALT;
import static org.yucs.spotter.regex.token.Token.tokenType.ANCHOR;

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
        if (t.type == ANCHOR && ((AnchorToken) t).anchor == '^') //if first token is start anchor, consume it, and only try to match from start of text
            return match(t.next, text, 0);

        for(int i=0; i < text.length(); i++) { //no start anchor so can try matching from anywhere in text
            if (match(t, text, i))
                return true;
        }

        return false;
    }

    private boolean match(Token t, String text, int text_pos) throws RegexException {
        //finished regex, means we matched every token, so it passes
        if (t == null) {
            return true;
        }

        if (t.type == ANCHOR) {
            if (((AnchorToken) t).anchor == '$') {
                return text.length() == text_pos;
            } else {
                throw new RegexException("Unexpected ANCHOR token: " + ((AnchorToken) t).anchor);
            }
        } else if (t.type == ALT) {
            return matchAlternates(text, text_pos, (AltToken) t);
        } else {
            CharacterToken ct = (CharacterToken) t;
            if (ct.q != null)
               return matchRange(text, text_pos, ct.c, ct.q, t.next);
            // if this text character matches regex token, continue matching rest of text against rest of regex
            return ct.c.match(text.charAt(text_pos)) && match(t.next, text, text_pos + 1);
        }
    }

    private boolean matchRange(String text, int text_pos, CharacterClass c, Quantifier q, Token t) throws RegexException {
        for (int i = 0; i < q.min; i++) { // can we match the minimum number of characters?
            if (text.length() > text_pos && c.match(text.charAt(text_pos))) {
                text_pos++;
            } else {
                return false;
            }
        }

        for(int i=0; i <= q.max-q.min || q.max == -1; i++) {
            if (match(t, text, text_pos)) { // matched the minimum, see if the rest of text matches the rest of the regex
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
            if (match(t, text, text_pos))
                return true;
        }

        return false;
    }
}