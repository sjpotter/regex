package org.yucs.spotter.regex.token;

import org.yucs.spotter.regex.CharacterClass;
import org.yucs.spotter.regex.Quantifier;

public class CharacterToken extends Token {
    public final CharacterClass c;
    public final Quantifier q;

    CharacterToken(CharacterClass c, Quantifier q) {
        super(tokenType.CHARACTER_CLASS);
        this.c = c;
        this.q = q;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (c == null) throw new AssertionError();
        sb.append("CharacterClass: ").append(c.toString()).append(", ");

        if (q != null) {
            sb.append("Quantifier: min = ").append(q.min).append(" max = ").append(q.max);
        } else {
            sb.append("Quantifier: null");
        }

        return sb.toString();
    }
}

