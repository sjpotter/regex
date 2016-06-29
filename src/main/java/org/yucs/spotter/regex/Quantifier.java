package org.yucs.spotter.regex;

class Quantifier {
    final int min;
    final int max;

    Quantifier(int min, int max) {
        this.min = min;
        this.max = max;
    }
}