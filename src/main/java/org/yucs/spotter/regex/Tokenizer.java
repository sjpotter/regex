package org.yucs.spotter.regex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Tokenizer {
    private final String regex;

    private Map<Integer, CloseParenToken> cpnMap = new HashMap<>();

    Tokenizer(String r) {
        regex = r;
    }

    Token tokenize() throws RegexException {
        return tokenize(0, regex.length());
    }

    private Token tokenize(int regex_pos, int end) throws RegexException {
        if (regex_pos >= end) {
            return null;
        }

        if (regex.charAt(regex_pos) == '^') {
            Token t = new AnchorToken('^');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        }

        if (regex.charAt(regex_pos) == '$') {
            Token t = new AnchorToken('$');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        }

        if (regex.charAt(regex_pos) == ')') {
            Token t = cpnMap.get(regex_pos);
            t.next = tokenize(regex_pos + 1, end);

            return t;
        }

        if (regex.charAt(regex_pos) == '(') {
            int endParen = findMatchingParen(regex_pos);

            CloseParenToken cpt = new CloseParenToken();
            cpnMap.put(endParen, cpt);

            OpenParenToken t = createParenToken(regex_pos, endParen);

            cpt.matched = t;

            t.next = tokenize(endParen, end);

            return t;
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
        t.next = tokenize(regex_pos, end);

        return t;
    }

    private int findMatchingParen(int start) throws RegexException {
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

    private List<Integer> findPipes(int start, int end) throws RegexException {
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

    private OpenParenToken createParenToken(int regex_pos, int endParen) throws RegexException {
        List<Integer> pipes = findPipes(regex_pos + 1, endParen-2); // TODO: ugly

        int start = regex_pos + 1;

        OpenParenToken t = new OpenParenToken(start);

        for (int pipe : pipes) {
            t.addAlt(tokenize(start, pipe));
            start = pipe + 1;
        }

        t.addAlt(tokenize(start, endParen));

        return t;
    }
}
