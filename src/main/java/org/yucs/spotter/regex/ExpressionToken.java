package org.yucs.spotter.regex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

abstract class ExpressionToken extends Token {
    final private List<Token> alts = new LinkedList<>();

    void addAlt(Token t) {
        alts.add(t);
    }
    Iterator<Token> altIterator() { return alts.iterator(); }
}
