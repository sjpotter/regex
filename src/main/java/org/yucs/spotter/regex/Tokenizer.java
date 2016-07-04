package org.yucs.spotter.regex;

import java.util.*;

class Tokenizer {
    private final String regex;

    int captureCount = 0;

    private Token t = null;

    Tokenizer(String r) {
        regex = r;
    }

    Token tokenize() throws RegexException {
        if (t == null)
            t = createExpressionToken(0, regex.length());

        return t;
    }

    private Token tokenize(int regex_pos, int end) throws RegexException {
        Token t;

        if (regex_pos >= end) {
            return NullToken.Instance;
        }

        // Non quantifiable regex token, return in their block
        if (regex.charAt(regex_pos) == '^') {
            // start of line anchor token
            t = new AnchorToken('^');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        } else if (regex.charAt(regex_pos) == '$') {
            // end of line anchor token
            t = new AnchorToken('$');
            t.next = tokenize(regex_pos + 1, end);
            return t;
        } else if (regex.charAt(regex_pos) == '\\'  && (regex_pos+1 < regex.length() && (regex.charAt(regex_pos+1) == 'b' || regex.charAt(regex_pos+1) == 'B'))) {
            // word boundary anchor token
            t = new AnchorToken(regex.charAt(regex_pos+1));
            t.next = tokenize(regex_pos+2, end);
            return t;
        }

        // Quantifiable regex tokens, return at end when quantifier is parsed if present
        if (regex.charAt(regex_pos) == '(') {
            // start group token
            int endParen = findMatchingParen(regex_pos);

            t = createExpressionToken(regex_pos+1, endParen);

            regex_pos = endParen + 1;
        } else if (regex.charAt(regex_pos) == '\\' && (regex_pos+1 < regex.length() && Character.isDigit(regex.charAt(regex_pos+1)))) {
            // Backreference token
            regex_pos++;
            int val = Character.digit(regex.charAt(regex_pos), 10);
            while (Character.isDigit(regex.charAt(regex_pos+1))) {
                regex_pos++;
                val *= 10;
                val += Character.digit(regex.charAt(regex_pos), 10);
            }

            t = new BackReferenceToken(val);
            regex_pos++;
        } else {
            // regular character matching token
            CharacterClassFactory ccf = CharacterClassFactory.getCharacterClass(regex, regex_pos);

            t = new CharacterToken(ccf.c);
            regex_pos = ccf.regex_pos;
        }

        t.next = NullToken.Instance;

        QuantifierFactory qf = QuantifierFactory.parse(regex, regex_pos);
        if (qf != null) {
            t = new QuantifierToken(qf.q, t);
            regex_pos = qf.regex_pos;
        }

        t.next = tokenize(regex_pos, end);
        return t;
    }

    private int findMatchingParen(int start) throws RegexException {
        Stack<Integer> parens = new Stack<>();

        // by reading the first character first, can make the switch in loop simpler as don't have to check if index 0
        parens.push(start);

        boolean slashIsEscape = false;
        for(int i = start+1; i < regex.length(); i++) {
            switch (regex.charAt(i)) {
                case '\\':
                    slashIsEscape = !slashIsEscape;
                    break;
                case '(':
                    if (!slashIsEscape || regex.charAt(i-1) != '\\') {
                        parens.push(i);
                    }
                    break;
                case ')':
                    if (!slashIsEscape || regex.charAt(i-1) != '\\') {
                        parens.pop();
                    }
                    if (parens.size() == 0)
                        return i;
                    break;
            }
        }

        throw new RegexException("unbalanced parens");
    }

    private List<Integer> findPipes(int start, int end) throws RegexException {
        LinkedList<Integer> parens = new LinkedList<>();
        LinkedList<Integer> pipes = new LinkedList<>();

        // by reading the first character first, can make the switch in loop simpler as don't have to check if index 0
        boolean slashIsEscape = false;
        switch (regex.charAt(start)) {
            case '\\':
                slashIsEscape = true;
                break;
            case '|':
                pipes.add(start);
                break;
            case '(':
                parens.add(start);
                break;
            case ')':
                throw new RegexException("unbalanced parens");
        }

        // As we are searching for alternates, only find pipes that are not in sub expressions (i.e. surrounded by ()
        for(int i=start+1; i < end; i++) { //last element should be a paren
            switch (regex.charAt(i)) {
                case '\\':
                    slashIsEscape = !slashIsEscape;
                    break;
                case '(':
                    if (!slashIsEscape || regex.charAt(i-1) != '\\') {
                        parens.push(i);
                    }
                    break;
                case ')':
                    if (!slashIsEscape || regex.charAt(i-1) != '\\') {
                        if (parens.size() == 0)
                            throw new RegexException("unbalanced parens");
                        parens.pop();
                    }
                    break;
                case '|':
                    if ((!slashIsEscape || regex.charAt(i-1) != '\\') && parens.size() == 0) {
                        pipes.addLast(i);
                    }
            }
        }

        return pipes;
    }

    private ExpressionToken createExpressionToken(int regex_pos, int endParen) throws RegexException {
        List<Integer> pipes = findPipes(regex_pos, endParen);

        ExpressionToken t = new ExpressionToken(captureCount++);

        for (int pipe : pipes) {
            t.addAlt(tokenize(regex_pos, pipe));
            regex_pos = pipe + 1;
        }

        t.addAlt(tokenize(regex_pos, endParen));

        return t;
    }
}