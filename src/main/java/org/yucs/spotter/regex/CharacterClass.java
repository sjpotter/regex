package org.yucs.spotter.regex;

import java.util.HashSet;

class CharacterClass {
    private boolean all = false;
    private boolean negate = false;

    private final HashSet<Character> set = new HashSet<>();
    final static CharacterClass global = new CharacterClass();

    private static final String digits = "0-9";
    private static final String lower = "a-z";
    private static final String upper = "A-Z";

    CharacterClass(String str, int beg, int end) throws Exception {
        int i = beg;
        if (end > beg && str.charAt(i) == '^') {
            i++;
            negate = true;
        }
        for (; i <= end; i++) {
            if (str.charAt(i) == '\\') {
                parseSlash(str, i);
                i++;
            } else if (i+2 <= end && str.charAt(i+1) == '-' ) {
                parseRange(str, i);
                i += 2;
            } else {
                set.add(str.charAt(i));
            }
        }
    }

    private CharacterClass() {
        all = true;
    }

    public String toString() {
        if (all)
            return "<global>";

        String ret = String.valueOf(set);
        if (negate) {
            ret += " (negated)";
        }

        return ret;
    }

    boolean match(char c) {
        if (all || set.contains(c))
            return !negate;

        return negate;
    }

    private void parseRange(String s, int pos) throws Exception {
        if (s.charAt(pos) < s.charAt(pos+2)) {
            for(char c=s.charAt(pos); c <= s.charAt(pos+2); c++)
                set.add(c);
        } else {
            throw new Exception("Character class ranged have to be in ascending order");
        }
    }

    private void parseSlash(String s, int pos) throws Exception {
        if (s.length() == pos + 1) {
            throw new Exception("string ended with a single unescaped \\");
        }

        switch (s.charAt(pos + 1)) {
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
            case '^':
                set.add('^');
                break;
            case '$':
                set.add('$');
                break;
            case 'd':
                parseRange(digits, 0);
                break;
            case 'w':
                parseRange(digits, 0);
                parseRange(upper, 0);
                parseRange(lower, 0);
                break;
            default:
                throw new Exception("parseSlash: unknown slash case: " + s.charAt(1));
        }
    }
}