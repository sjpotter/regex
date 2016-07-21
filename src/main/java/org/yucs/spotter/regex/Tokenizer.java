package org.yucs.spotter.regex;

import java.util.*;

class Tokenizer {
    private final String regex;

    int captureCount = 0;
    final Map<Integer, NormalExpressionToken> captureMap = new HashMap<>();

    private Token t = null;

    Tokenizer(String r) {
        regex = r;
    }

    Token tokenize() throws RegexException {
        if (t == null)
            t = createCapturedExpressionToken(captureCount++, 0, regex.length());

        return t;
    }

    private Token tokenize(int regex_pos, int end) throws RegexException {
        Token t = null;

        if (regex_pos >= end) {
            return NullToken.Instance;
        }

        // Non quantifiable regex token, return in their block
        // Quantifiable regex tokens, return at end when quantifier is parsed if present
        switch (regex.charAt(regex_pos)) {
            case '^': // start of line anchor token
            case '$': // end of line anchor token
                t = new AnchorToken(regex.charAt(regex_pos));
                t.next = tokenize(regex_pos + 1, end);
                return t;

            case '\\':
                if (regex_pos + 1 < regex.length()) {
                    // word boundary anchor token
                    if (regex.charAt(regex_pos + 1) == 'b' || regex.charAt(regex_pos + 1) == 'B') {
                        t = new AnchorToken(regex.charAt(regex_pos + 1));
                        t.next = tokenize(regex_pos + 2, end);
                        return t;
                    }

                    // BackReference token
                    if (Character.isDigit(regex.charAt(regex_pos + 1))) {
                        regex_pos++;
                        int val = Character.digit(regex.charAt(regex_pos), 10);
                        while (Character.isDigit(regex.charAt(regex_pos + 1))) {
                            regex_pos++;
                            val *= 10;
                            val += Character.digit(regex.charAt(regex_pos), 10);
                        }

                        t = new BackReferenceToken(val);
                        regex_pos++;
                    }
                } else {
                    throw new RegexException("Ending with a single \\");
                }

                break;

            case '(': { // There are many types of clauses that are within parens
                int endParen = findMatchingParen(regex_pos);

                if (regex.charAt(regex_pos + 1) == '?') { // There are also many types of clauses that are within (? )
                    switch (regex.charAt(regex_pos + 2)) {
                        case '>':
                            t = createAtomicExpressionToken(regex_pos + 3, endParen);
                            break;

                        // Look Ahead does not make sense to be quantified, position resets after they are done
                        case '=':  // Positive Look Ahead
                            t = createLookAheadExpressionToken(regex_pos + 3, endParen, true);
                            t.next = tokenize(endParen + 1, end);
                            return t;

                        case '!': // Negative Look Ahead
                            t = createLookAheadExpressionToken(regex_pos + 3, endParen, false);
                            t.next = tokenize(endParen + 1, end);
                            return t;

                        case '<':
                            switch (regex.charAt(regex_pos + 3)) {
                                case '=': // Positive Look Behind
                                    t = createLookBehindExpressionToken(regex_pos + 4, endParen, true);
                                    t.next = tokenize(endParen + 1, end);
                                    return t;
                                case '!': // Negative Look Behind
                                    t = createLookBehindExpressionToken(regex_pos + 4, endParen, false);
                                    t.next = tokenize(endParen + 1, end);
                                    return t;
                                default:
                                    throw new RegexException("Unknown lookbehind grouping");
                            }

                        case '(':
                            t = createIfThenElseToken(regex_pos + 2, endParen);
                            t.next = tokenize(endParen + 1, end);
                            return t;

                        default:
                            // RegexRecursion will go here (quantifiable!)
                            // Instead of having a massive case clause (might have to change later
                            char c = regex.charAt(regex_pos + 2);
                            if (c == 'R' || Character.isDigit(c)) {
                                t = createRecursiveToken(regex_pos + 2, endParen);
                                break;
                            }

                            throw new RegexException("Unknown grouping type");
                    }
                } else { // normal capture
                    int capture = captureCount++;
                    t = new StartCaptureToken();
                    t.next = createCapturedExpressionToken(capture, regex_pos + 1, endParen);
                    t.next.next = new EndCaptureToken(t, capture);
                }
                regex_pos = endParen + 1;
            }
        }

        if (t == null) { // fall through position
            // regular character matching token
            CharacterClassFactory ccf = CharacterClassFactory.getCharacterClass(regex, regex_pos);

            t = new CharacterToken(ccf.c);
            regex_pos = ccf.regex_pos;
        }

        Token last = t;

        // handling the startcapturetoken / expression / endcapturetoken sequence
        while (!(last.next instanceof NullToken))
             last = last.next;

        QuantifierFactory qf = QuantifierFactory.parse(regex, regex_pos);
        if (qf != null) {
            t = new QuantifierToken(qf.q, t);
            regex_pos = qf.regex_pos;
            last = t;
        }

        last.next = tokenize(regex_pos, end);
        return t;
    }

    // Given a start index that is an open paren, find the index of the matching close paren
    private int findMatchingParen(int start) throws RegexException {
        if (regex.charAt(start) != '(') {
            throw new RegexException("findMatchingParen: didn't start with an open paren");
        }

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

    // find the pipes that separate expressions at the same level within the start/end indices.
    // if a section is enclosed in parens, its not at the same level, and hence not a pipe we care about
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

    private Token createRecursiveToken(int regex_pos, int endParen) throws RegexException {
        int capture;
        if (regex.charAt(regex_pos) == 'R' && regex_pos + 1 == endParen)
            capture = 0;
        else {
            try {
                capture = Integer.parseInt(regex.substring(regex_pos, endParen));
            } catch (NumberFormatException e) {
                throw new RegexException("createRecursiveToken: couldn't parse " + regex.substring(regex_pos, endParen) + " as int");
            }
        }
        return new RecursiveToken(capture);
    }

    private Token createIfThenElseToken(int regex_pos, int endParen) throws RegexException {
        int ifEndParen = findMatchingParen(regex_pos);

        // Support testing if capture group exists.
        Token ifToken = tokenize(regex_pos, ifEndParen);
        Token thenToken;
        Token elseToken;

        if (ifToken instanceof NormalExpressionToken) {
            captureCount--; // TODO: HACK as the tokenize on the () string above would have incremented
            ifToken = new CaptureGroupTesterToken(regex.substring(regex_pos+1, ifEndParen));
        }

        if (!(ifToken instanceof TestableToken)) {
            throw new RegexException("Didn't parse a testable token from: " + regex.substring(regex_pos, ifEndParen));
        }

        List<Integer> pipes = findPipes(ifEndParen+1, endParen);
        switch (pipes.size()) {
            case 0:
                thenToken = createNormalExpressionToken(ifEndParen+1, endParen);
                elseToken = NullToken.Instance;
                break;
            case 1:
                thenToken = createNormalExpressionToken(ifEndParen+1, pipes.get(0));
                elseToken = createNormalExpressionToken(pipes.get(0)+1, endParen);
                break;
            default:
                throw new RegexException("Expected at most one pipe in if/then/else token parsing");
        }

        return new IfThenElseToken(ifToken, thenToken, elseToken);
    }

    private Token createLookAheadExpressionToken(int regex_pos, int endParen, boolean positive) throws RegexException {
        NormalExpressionToken t = createNormalExpressionToken(regex_pos, endParen);

        return new LookAheadExpressionToken(t, positive);
    }

    private Token createLookBehindExpressionToken(int regex_pos, int endParen, boolean positive) throws RegexException {
        NormalExpressionToken t = createNormalExpressionToken(regex_pos, endParen);

        t.internalReverse();
        return new LookBehindExpressionToken(t, positive);
    }

    private NormalExpressionToken createCapturedExpressionToken(int capturePos, int regex_pos, int endParen) throws RegexException {
        NormalExpressionToken t = createNormalExpressionToken(regex_pos, endParen);

        if (capturePos != -1) {
            captureMap.put(capturePos, t);
        }

        return t;
    }

    private Token createAtomicExpressionToken(int regex_pos, int endParen) throws RegexException {
        AtomicExpressionToken t = new AtomicExpressionToken();
        parseExpression(t, regex_pos, endParen);

        return t;
    }

    private NormalExpressionToken createNormalExpressionToken(int regex_pos, int endParen) throws RegexException {
        NormalExpressionToken t = new NormalExpressionToken();
        parseExpression(t, regex_pos, endParen);

        return t;
    }

    private void parseExpression(ExpressionToken t, int regex_pos, int endParen) throws RegexException {
        List<Integer> pipes = findPipes(regex_pos, endParen);

        for (int pipe : pipes) {
            t.addAlt(tokenize(regex_pos, pipe));
            regex_pos = pipe + 1;
        }

        t.addAlt(tokenize(regex_pos, endParen));
    }
}