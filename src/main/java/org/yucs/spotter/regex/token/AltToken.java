package org.yucs.spotter.regex.token;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AltToken extends Token {
    private List<Token> alts = new LinkedList<>();

    AltToken() {
        super(tokenType.ALT);
    }

    void addAlt(Token t) {
        alts.add(t);
    }

    public Iterator<Token> getAlts() {
        return alts.iterator();
    }

    public String toString() {
        return "Alternatives";
    }
}
