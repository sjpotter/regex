package org.yucs.spotter.regex;

class Quantifier {
    final int min;
    final int max;
    final int regex_pos;

    private Quantifier(int min, int max, int regex_pos) {
        this.min = min;
        this.max = max;
        this.regex_pos = regex_pos;
    }

    // parses out */+/{n}/{n,}/{n,m} syntax
    // */+/? are easy.  {} is complicated, as it can be a quantifier or just a regular regex character
    static Quantifier parse(String regex, int regex_pos) {
        if (regex.length() == regex_pos)
            return null;

        switch (regex.charAt(regex_pos)) {
            case '{':
                return handleVariable(regex, regex_pos + 1);
            case '*':
                return new Quantifier(0, -1, regex_pos + 1);
            case '+':
                return new Quantifier(1, -1, regex_pos + 1);
            case '?':
                return new Quantifier(0, 1, regex_pos + 1);
        }

        return null;
    }

    private static Quantifier handleVariable(String regex, int regex_pos) {
        if (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) { //is there a number after the {
            int val = Character.digit(regex.charAt(regex_pos), 10);
            regex_pos++;

            while (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                val *= 10;
                val += Character.digit(regex.charAt(regex_pos), 10);
                regex_pos++;
            }

            if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { //if it's {#} we need to match exact
                return new Quantifier(val, val, regex_pos + 1);
            } else if (regex.length() > regex_pos && regex.charAt(regex_pos) == ',') { // can be {#,} or {#,#}
                int min = val;

                regex_pos++;
                if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { // {#,}
                    return new Quantifier(min, -1, regex_pos + 1);
                } else { // determine if it's a valid // {#,#}
                    if (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                        val = Character.digit(regex.charAt(regex_pos), 10);
                        regex_pos++;
                        while (regex.length() > regex_pos && Character.isDigit(regex.charAt(regex_pos))) {
                            val *= 10;
                            val += Character.digit(regex.charAt(regex_pos), 10);
                            regex_pos++;
                        }
                    }

                    if (regex.length() > regex_pos && regex.charAt(regex_pos) == '}') { //maybe valid {#,#}
                        if (min <= val) // {min,val} only valid if min <= val (max)
                            return new Quantifier(min, val, regex_pos+1);
                    }
                }
            }
        }

        //regex following { invalid as a quantifier
        return null;
    }
}