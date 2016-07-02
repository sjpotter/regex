package org.yucs.spotter.regex;

class Quantifier {
    final int min;
    final int max;
    boolean greedy;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
        this.greedy = true;
    }
}