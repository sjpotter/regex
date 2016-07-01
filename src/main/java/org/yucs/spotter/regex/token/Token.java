package org.yucs.spotter.regex.token;

import org.yucs.spotter.regex.CharacterClassFactory;
import org.yucs.spotter.regex.Quantifier;
import org.yucs.spotter.regex.QuantifierFactory;
import org.yucs.spotter.regex.RegexException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Token {
    public enum tokenType { ANCHOR, CHARACTER_CLASS, ALT }

    public final tokenType type;
    public Token next = null;

    Token(tokenType tt) {
        type = tt;
    }
}