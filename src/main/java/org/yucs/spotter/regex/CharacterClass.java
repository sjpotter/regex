package org.yucs.spotter.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class CharacterClass {
    private boolean all = false;
    final static CharacterClass global = new CharacterClass();

    private final HashSet<Character> characters = new HashSet<>();
    private final HashSet<Character> negated = new HashSet<>();

    private static final Set<Character> digits = new HashSet<>();
    private static final Set<Character> lower = new HashSet<>();
    private static final Set<Character> upper = new HashSet<>();
    private static final Set<Character> words = new HashSet<>();

    static {
        for(char c='0'; c <= '9'; c++) {
            digits.add(c);
        }
        for(char c='a'; c <= 'z'; c++) {
            lower.add(c);
            upper.add(Character.toUpperCase(c));
        }

        words.addAll(digits);
        words.addAll(lower);
        words.addAll(upper);
    }
    private static final Set<Character> whitespace = new HashSet<>(Arrays.asList(new Character[] {' ', '\t','\r', '\n', '\f'}));

    CharacterClass(String str, int beg, int end) throws RegexException {
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

    public boolean match(char c) {
        return characters.contains(c) || (negated.size() > 0 && !negated.contains(c)) || all;
    }

    private void parseRange(boolean negate, String s, int pos) throws RegexException {
        if (s.charAt(pos) < s.charAt(pos+2)) {
            for(char c=s.charAt(pos); c <= s.charAt(pos+2); c++)
                if (!negate) {
                    characters.add(c);
                } else {
                    negated.add(c);
                }
        } else {
            throw new RegexException("Character class ranged have to be in ascending order: " + s.substring(pos, pos+3));
        }
    }

    private void parseSlash(boolean negate, String s, int pos) throws RegexException {
        if (s.length() == pos + 1) {
            throw new RegexException("string ended with a single unescaped \\");
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
            case '|':
                characters.add('|');
                break;
            case '(':
                characters.add('(');
                break;
            case ')':
                characters.add(')');
                break;
            case 'd':
                addSet(negate, digits);
                break;
            case 'D':
                addSet(!negate, digits);
                break;
            case 'w':
                addSet(negate, words);
                break;
            case 'W':
                addSet(!negate, words);
                break;
            case 's':
                addSet(negate, whitespace);
                break;
            case 'S':
                addSet(!negate, whitespace);
                break;
            default:
                throw new RegexException("parseSlash: unknown slash case: " + s.charAt(pos + 1) + " at index: " + (pos+1));
        }
    }

    private void addSet(boolean negate, Set<Character> set) {
        if (negate)
            negated.addAll(set);
        else
            characters.addAll(set);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterClass that = (CharacterClass) o;

        return all == that.all && characters.equals(that.characters) && negated.equals(that.negated);
    }

    @Override
    public int hashCode() {
        int result = (all ? 1 : 0);
        result = 31 * result + characters.hashCode();
        result = 31 * result + negated.hashCode();
        return result;
    }
}