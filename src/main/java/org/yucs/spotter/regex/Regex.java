package org.yucs.spotter.regex;

import java.util.*;

// Inspired by Rob Pike's implementation in TPOP:
// http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html

@SuppressWarnings("WeakerAccess")
public class Regex {
    final private Tokenizer tokenizer;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String regex = sc.next();
            String text = sc.next();

            try {
                Matcher matcher = (new Regex(regex)).Matcher();

                if (matcher.match(text)) {
                    System.out.println(text + " matched regex " + regex);
                    System.out.println("Groups:");
                    int i = 0;
                    for(String match : matcher.getGroups()) {
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

    public Regex(String r) throws RegexException {
        String regex = '(' + r + ')';

        tokenizer = new Tokenizer(regex);
        tokenizer.tokenize();
    }

    public boolean match(String text) throws RegexException {
        return (new Matcher(tokenizer).match(text));
    }

    @SuppressWarnings("WeakerAccess")
    public Matcher Matcher() throws RegexException {
        return new Matcher(tokenizer);
    }
}