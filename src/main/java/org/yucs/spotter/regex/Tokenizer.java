package org.yucs.spotter.regex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Tokenizer {
    private final String regex;

    int parenCount = 0;

    final private Map<Integer, CloseParenToken> cptMap = new HashMap<>();

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

        // start of line anchor token
        if (regex.charAt(regex_pos) == '^') {
            Token t = new AnchorToken('^');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        }

        // end of line anchor token
        if (regex.charAt(regex_pos) == '$') {
            Token t = new AnchorToken('$');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        }

        // word boundary anchor token
        if (regex.charAt(regex_pos) == '\\'  && (regex.charAt(regex_pos+1) == 'b' || regex.charAt(regex_pos+1) == 'B')) {
            Token t = new AnchorToken(regex.charAt(regex_pos+1));
            t.next = tokenize(regex_pos+2, end);
            return t;
        }

        // end group token
        if (regex.charAt(regex_pos) == ')') {
            Token t = cptMap.get(regex_pos);
            t.next = tokenize(regex_pos + 1, end);
            return t;
        }

        // start group token
        if (regex.charAt(regex_pos) == '(') {
            int endParen = findMatchingParen(regex_pos);

            //need to be able to match parens in the regex
            CloseParenToken cpt = new CloseParenToken();
            cptMap.put(endParen, cpt);

            OpenParenToken t = createParenToken(regex_pos, endParen, cpt);

            cpt.matched = t;

            tokenize(endParen, end);

            return t;
        }

        // Backreference token
        if (regex.charAt(regex_pos) == '\\' && Character.isDigit(regex.charAt(regex_pos+1))) {
            regex_pos++;
            int val = Character.digit(regex.charAt(regex_pos), 10);
            while (Character.isDigit(regex.charAt(regex_pos+1))) {
                regex_pos++;
                val *= 10;
                val += Character.digit(regex.charAt(regex_pos), 10);
            }

            Token t = new BackReferenceToken(val);
            t.next = tokenize(regex_pos+1, end);
            return t;
        }

        // regular character matching token
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

        for(int i=start; i < end; i++) {
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

    private OpenParenToken createParenToken(int regex_pos, int endParen, CloseParenToken cpt) throws RegexException {
        int start = regex_pos + 1;

        List<Integer> pipes = findPipes(start, endParen);

        OpenParenToken t = new OpenParenToken(parenCount++, cpt);

        for (int pipe : pipes) {
            t.addAlt(tokenize(start, pipe));
            start = pipe + 1;
        }

        t.addAlt(tokenize(start, endParen));

        return t;
    }
}