package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// Abstract base class for AtomicExpressionToken and NormalExpressionToken
// provides the common "Alt" handling (adding, iterating, reversing)

abstract class ExpressionToken extends Token {
    final private List<Token> alts = new LinkedList<>();

    void addAlt(Token t) {
        alts.add(t);
    }
    Iterator<Token> altIterator() { return alts.iterator(); }

    void internalReverse() throws RegexException {
        List<Token> newAlts = new LinkedList<>();

        for(Token t : alts) {
            newAlts.add(t.reverse());
        }

        alts.clear();
        alts.addAll(newAlts);
    }

    @Override
    Token reverse() throws RegexException {
        internalReverse();

        return super.reverse();
    }
}
