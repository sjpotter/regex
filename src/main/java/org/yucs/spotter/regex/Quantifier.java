package org.yucs.spotter.regex;

public class Quantifier {
    public final int min;
    public final int max;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
    }
}