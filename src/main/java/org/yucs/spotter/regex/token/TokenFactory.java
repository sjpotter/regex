package org.yucs.spotter.regex.token;

import org.yucs.spotter.regex.CharacterClassFactory;
import org.yucs.spotter.regex.Quantifier;
import org.yucs.spotter.regex.QuantifierFactory;
import org.yucs.spotter.regex.RegexException;

import java.util.LinkedList;
import java.util.List;

public class TokenFactory {
    public static Token tokenize(String regex, int regex_pos) throws RegexException {
        if (regex_pos == regex.length()) {
            return null;
        }

        if (regex_pos == 0 && regex.charAt(regex_pos) == '^') {
            Token t = new AnchorToken('^');
            t.next = tokenize(regex, regex_pos + 1);
            return t;
        }

        if (regex_pos + 1 == regex.length() && regex.charAt(regex_pos) == '$') {
            return new AnchorToken('$');
        }

        if (regex.charAt(regex_pos) == '(') {
            int endParen = findMatchingParen(regex, regex_pos);

            List<Integer> pipes = findPipes(regex, regex_pos + 1, endParen-1);
            if (pipes.size() != 0) {
                return createAltToken(regex, regex_pos, endParen, pipes);
            }
            // no alternatives in paren
            // TODO: if we deal with matching groups, will have to keep paren token
            Token t = tokenize(regex.substring(regex_pos + 1, endParen), 0);
            Token next = tokenize(regex, endParen + 1);
            if (t == null) {
                return next;
            } else {
                Token tmp = t;
                while (tmp.next != null)
                    tmp = tmp.next;
                tmp.next = next;

                return t;
            }
        }

        CharacterClassFactory ccf = CharacterClassFactory.getCharacterClass(regex, regex_pos);
        regex_pos = ccf.regex_pos;

        QuantifierFactory qf = QuantifierFactory.parse(regex, regex_pos);
        Quantifier q = null;
        if (qf != null) {
            regex_pos = qf.regex_pos;
            q = qf.q;
        }

        Token t = new CharacterToken(ccf.c, q);
        t.next = tokenize(regex, regex_pos);

        return t;
    }

    private static int findMatchingParen(String regex, int start) throws RegexException {
        LinkedList<Integer> parens = new LinkedList<>();
        for(int i = start; i < regex.length(); i++) {
            if (regex.charAt(i) == '(' && (i == 0 || regex.charAt(i-1) != '\\')) {
                parens.push(i);
            }
            if (regex.charAt(i) == ')' && regex.charAt(i-1) != '\\') {
                if (parens.size() == 0) // this is probably impossible if we start ther string with a '('
                    throw new RegexException("unbalanced parens");
                parens.pop();
                if (parens.size() == 0)
                    return i;
            }
        }

        throw new RegexException("unbalanced parens");
    }

    private static List<Integer> findPipes(String regex, int start, int end) throws RegexException {
        LinkedList<Integer> parens = new LinkedList<>();
        LinkedList<Integer> alternates = new LinkedList<>();

        for(int i=start; i <= end; i++) {
            if (regex.charAt(i) == '|' && (i == start || regex.charAt(i-1) != '\\') && parens.size() == 0) {
                alternates.addLast(i);
            } else if (regex.charAt(i) == '(' && (i == start || regex.charAt(i-1) != '\\')) {
                parens.push(i);
            } else if  (regex.charAt(i) == ')' && (i == start || regex.charAt(i-1) != '\\')) {
                if (parens.size() == 0) {
                    throw new RegexException("unbalanced parens");
                }
                parens.pop();
            }
        }

        return alternates;
    }

    private static Token createAltToken(String regex, int regex_pos, int endParen, List<Integer> pipes) throws RegexException {
        AltToken t = new AltToken();

        int start = regex_pos + 1;
        for (int pipe : pipes) {
            t.addAlt(tokenize(regex.substring(start, pipe), 0));
            start = pipe + 1;
        }

        t.addAlt(tokenize(regex.substring(start, endParen),0));
        t.next = tokenize(regex, endParen+1);

        return t;
    }

}
