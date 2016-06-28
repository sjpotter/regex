package org.yucs.spotter.regex;

import java.util.Scanner;

// Based on Rob Pike's implementation in TPOP:
// http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html

public class Regex {
    private final String regex;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String regex = sc.next();
            String text = sc.next();

            try {
                Regex matcher = new Regex(regex);

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

    Regex(String r) {
        this.regex = r;
    }

    boolean match(String text) throws Exception {
        return match(regex, text);
    }

    private boolean match(String regex, String text) throws Exception {
        //reached end of both regex and text means a match
        if (regex.length() == 0 && text.length() == 0) {
            return true;
        }

        // regex complete, but text to match hasn't been
        if (regex.length() == 0)
            return false;

        CharacterClassWrapper w = CharacterClassWrapper.getCharacterClass(regex);
        CharacterClass c = w.c;
        regex = w.regex;

        //regex not complete, but text complete
        //moved here to catch errors in regex
        if (text.length() == 0) {
            return false;
        }

        Quantifier q = Quantifier.parse(regex);
        if (q != null) {
            return matchRange(c, q.min, q.max, q.regex, text);
        }

        // simple case, match the current text string character against current character class character
        // and if it passes, match rest of regex against rest of string
        return c.match(text.charAt(0)) && match(regex, text.substring(1));
    }

    private boolean matchRange(CharacterClass c, int min, int max, String regex, String text) throws Exception {
        if (max != -1 && max < min) {
            throw new Exception("matchRange: min can't be less than max");
        }
        if (min != 0) { // can we match the minimum number of characters?
            for(int i=0; i < min; i++) {
                if (text.length() > 0 && c.match(text.charAt(0))) {
                    text = text.substring(1);
                } else {
                    return false;
                }
            }
        }

        for(int i=0; i <= max-min || max == -1; i++) {
            if (match(regex, text)) { // matched the minimum, see if the rest of text matches the regex without consuming more of the regex
                return true;
            }
            // couldn't match rest of text
            if (text.length() != 0 && c.match(text.charAt(0))) {
                text = text.substring(1); // eat one character from text if it matches regex character
            } else {
                return false; // reached end of text or didn't match regex char
            }
        }

        return false;
    }
}