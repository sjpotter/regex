package org.yucs.spotter.regex.token;

public class AnchorToken extends Token {
    public final char anchor;

    AnchorToken(char a) {
        super(tokenType.ANCHOR);
        this.anchor = a;
    }

    public String toString() {
        return "Anchor: " + anchor;
    }
}
