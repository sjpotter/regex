package org.yucs.spotter.regex;

import java.util.HashSet;

public class CharacterClass {
    private boolean all = false;
    public boolean negate = false;

    private final HashSet<Character> set = new HashSet<Character>();
    public static CharacterClass global = new CharacterClass(true);

    private static final String digits = "0-9";
    private static final String lower = "a-z";
    private static final String upper = "A-Z";

    CharacterClass(String str) throws Exception {
        int i = 0;
        if (str.length() > 1 && str.charAt(i) == '^') {
            i++;
            negate = true;
        }
        for (; i < str.length(); i++) {
            if (str.charAt(i) == '\\') {
                parseSlash(str.substring(i, i+2));
                i++;
            } else if (i+2 < str.length() && str.charAt(i+1) == '-' ) {
                parseRange(str.substring(i,i+3));
                i += 2;
            } else {
                set.add(str.charAt(i));
            }
        }
    }

    private CharacterClass(boolean match) {
        all = match;
    }

    public boolean match(char c) {
        if (all || set.contains(c))
            return !negate && true;

        return negate || false;
    }

    private void parseRange(String s) throws Exception {
        if (s.charAt(0) < s.charAt(2)) {
            for(char c=s.charAt(0); c <= s.charAt(2); c++)
                set.add(c);
        } else {
            throw new Exception("Character class ranged have to be in ascending order");
        }
    }

    private void parseSlash(String s) throws Exception {
        if (s.length() != 2) {
            throw new Exception("parseSlash requires a 2 character strings");
        }

        if (s.charAt(0) != '\\') {
            throw new Exception("parseSlash only takes 2 charater strings if first character is a \\");
        }

        switch (s.charAt(1)) {
            case '\\':
                set.add('\\');
                break;
            case '+':
                set.add('+');
                break;
            case '*':
                set.add('*');
                break;
            case '?':
                set.add('?');
                break;
            case 'd':
                parseRange(digits);
                break;
            case 'w':
                parseRange(digits);
                parseRange(upper);
                parseRange(lower);
                break;
            default:
                throw new Exception("parseSlash: unknown slash case: " + s.charAt(1));
        }
    }
}
