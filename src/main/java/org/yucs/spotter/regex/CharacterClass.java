package org.yucs.spotter.regex;

import java.util.HashSet;

class CharacterClass {
    private boolean all = false;
    final static CharacterClass global = new CharacterClass();

    private final HashSet<Character> characters = new HashSet<>();
    private final HashSet<Character> negated = new HashSet<>();

    private static final String digits = "0-9";
    private static final String lower = "a-z";
    private static final String upper = "A-Z";
    private static final char[] whitespace = {' ', '\t','\r', '\n', '\f'};

    CharacterClass(String str, int beg, int end) throws Exception {
        int i = beg;
        boolean negate = false;

        if (end > beg && str.charAt(beg) == '^') {
            i++;
            negate = true;
        }

        for (; i <= end; i++) {
            if (str.charAt(i) == '\\') {
                parseSlash(negate, str, i);
                i++;
            } else if (i+2 <= end && str.charAt(i+1) == '-' ) {
                parseRange(negate, str, i);
                i += 2;
            } else {
                if (!negate) {
                    characters.add(str.charAt(i));
                } else {
                    negated.add(str.charAt(i));
                }
            }
        }
    }

    private CharacterClass() {
        all = true;
    }

    public String toString() {
        if (all)
            return "<global>";

        StringBuilder sb = new StringBuilder();

        if (characters.size() > 0) {
            sb.append("Positive: ").append(String.valueOf(characters));
            if (negated.size() > 0) {
                sb.append(" ");
            }
        }
        if (negated.size() > 0) {
            sb.append("Negative: ").append(String.valueOf(negated));
        }

        return sb.toString();
    }

    boolean match(char c) {
        return characters.contains(c) || !negated.contains(c) || all;
    }

    private void parseRange(boolean negate, String s, int pos) throws Exception {
        if (s.charAt(pos) < s.charAt(pos+2)) {
            for(char c=s.charAt(pos); c <= s.charAt(pos+2); c++)
                if (!negate) {
                    characters.add(c);
                } else {
                    negated.add(c);
                }
        } else {
            throw new Exception("Character class ranged have to be in ascending order");
        }
    }

    private void parseSlash(boolean negate, String s, int pos) throws Exception {
        if (s.length() == pos + 1) {
            throw new Exception("string ended with a single unescaped \\");
        }

        switch (s.charAt(pos + 1)) {
            case '\\':
                characters.add('\\');
                break;
            case '+':
                characters.add('+');
                break;
            case '*':
                characters.add('*');
                break;
            case '?':
                characters.add('?');
                break;
            case '^':
                characters.add('^');
                break;
            case '$':
                characters.add('$');
                break;
            case 'd':
                parseRange(negate, digits, 0);
                break;
            case 'D':
                parseRange(!negate, digits, 0);
                break;
            case 'w':
                parseRange(negate, digits, 0);
                parseRange(negate, upper, 0);
                parseRange(negate, lower, 0);
                break;
            case 'W':
                parseRange(!negate, digits, 0);
                parseRange(!negate, upper, 0);
                parseRange(!negate, lower, 0);
                break;
            case 's':
                for (char c : whitespace) {
                    characters.add(c);
                }
                break;
            case 'S':
                for (char c : whitespace) {
                    negated.add(c);
                }
                break;
            default:
                throw new Exception("parseSlash: unknown slash case: " + s.charAt(1));
        }
    }
}