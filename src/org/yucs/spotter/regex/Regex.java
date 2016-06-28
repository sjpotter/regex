package org.yucs.spotter.regex;

import java.util.Scanner;

// Inspired by Rob Pike's implementation in TPOP:
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
        if (regex.charAt(0) == '^')
            return match(regex.substring(1), text);

        for(int i=0; i < text.length(); i++) {
            if (match(regex, text.substring(i)))
                return true;
        }

        return false;
    }

    private boolean match(String regex, String text) throws Exception {
        //reached end of regex. matched
        if (regex.length() == 0) {
            return true;
        }

        //finished text and regex anchored to end with a $
        if (regex.length() == 1 && text.length() == 0 && regex.charAt(0) == '$')
            return true;

        CharacterClassWrapper w = CharacterClassWrapper.getCharacterClass(regex);
        CharacterClass c = w.c;
        regex = w.regex;

        //regex not complete, but text complete
        //moved here to catch errors in regex caught by parsing
        if (text.length() == 0) {
            return false;
        }

        //handle variable length matches
        Quantifier q = Quantifier.parse(regex);
        if (q != null) {
            return matchRange(c, q.min, q.max, q.regex, text);
        }

        // simple case of single length match
        return c.match(text.charAt(0)) && match(regex, text.substring(1));
    }

    /**
     * @param c - CharacterClass that can be matched between min and max types
     * @param min - minimum number of required matches
     * @param max - maximum number of possible matches (or -1 for no limit)
     * @param regex - the remaining regex to be parsed
     * @param text - the remaining text to be matched against
     * @return boolean if we were able to complete a regex match or not
     * @throws Exception
     */
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