package org.yucs.spotter.regex.token;

public class Token {
    @SuppressWarnings("WeakerAccess")
    public enum tokenType { ANCHOR, CHARACTER_CLASS, ALT }

    public final tokenType type;
    public Token next = null;

    Token(tokenType tt) {
        type = tt;
    }
}