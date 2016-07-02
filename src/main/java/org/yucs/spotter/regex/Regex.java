package org.yucs.spotter.regex;

import java.util.*;

// Inspired by Rob Pike's implementation in TPOP:
// http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html

public class Regex {
    private Token t;
    private SortedMap<Integer, String> groups = new TreeMap<>();
    private ArrayList<String> matches;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String regex = sc.next();
            String text = sc.next();

            try {
                Regex matcher = new Regex('(' + regex + ')');

                if (matcher.match(text)) {
                    System.out.println(text + " matched regex " + regex);
                    System.out.println("Groups:");
                    int i = 0;
                    for(String match : matcher.matches) {
                        System.out.println(i++ + ": " + match);
                    }
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
        t = (new Tokenizer(r)).tokenize();
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

    /**
     * Returns a boolean that says if the text matched against the Regex
     *
     * @param text the text to match against the regex
     * @return true/false if the text matches against the regex
     * @throws RegexException
     */
    @SuppressWarnings("WeakerAccess") // Need to be public to be usable elsewhere
    public boolean match(String text) throws RegexException {
        for(int i=0; i < text.length() || i == 0; i++) { //need to test empty text string too
            if (match(t, text, i, new Stack<CloseParenToken>())) {
                matches = new ArrayList<>(groups.size());
                for(int key : groups.keySet()) {
                    matches.add(groups.get(key));
                }
                return true;
            }
        }

        matches = null;
        return false;
    }

    /**
     * The main internal matching function
     *
     * @param t           the current regex token we are matching against
     * @param text        the entire text
     * @param text_pos    our current location within the text
     * @param closeParen  a stack of where we accumulate grouping matches
     * @return            true/false if we were able to finish matching the regex from here
     * @throws RegexException
     */
    boolean match(Token t, String text, int text_pos, Stack<CloseParenToken> closeParen) throws RegexException {
        // if we matched every token, we've finished regex, so it passes
        if (t == null) {
            if (closeParen.size() == 0) { // only finished if no paren (i.e. after alternatives enclosed in a group (|)
                return true;
            } else { // if finished the token set of the alternative, continue after the alternative
                t = closeParen.pop();
            }
        }

        return t.match(this, text, text_pos, closeParen);
    }

    void recordGroup(int paren_pos, String text, int text_start, int text_end) {
        groups.put(paren_pos, text.substring(text_start, text_end));
    }
}