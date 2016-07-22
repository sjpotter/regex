package org.yucs.spotter.regex;

class Quantifier {
    enum type { GREEDY, NONGREEDY, POSSESSIVE}

    final int min;
    final int max;
    type matchType;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
        this.matchType = type.GREEDY;
    }
}