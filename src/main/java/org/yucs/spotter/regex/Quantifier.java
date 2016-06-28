package org.yucs.spotter.regex;

class Quantifier {
    final int min;
    final int max;
    final String regex;

    private Quantifier(int min, int max, String regex) {
        this.min = min;
        this.max = max;
        this.regex = regex;
    }

    // parses out */+/{n}/{n,}/{n,m} syntax
    // */+/? are easy.  {} is complicated, as it can be a quantifier or just a regular regex character
    static Quantifier parse(String regex) {
        if (regex.length() == 0)
            return null;

        switch (regex.charAt(0)) {
            case '{':
                return handleVariable(regex.substring(1));
            case '*':
                return new Quantifier(0, -1, regex.substring(1));
            case '+':
                return new Quantifier(1, -1, regex.substring(1));
            case '?':
                return new Quantifier(0, 1, regex.substring(1));
        }

        return null;
    }

    private static Quantifier handleVariable(String regex) {
        if (regex.length() > 0 && Character.isDigit(regex.charAt(0))) { //is there a number after the {
            int val = Character.digit(regex.charAt(0), 10);
            regex = regex.substring(1);

            while (regex.length() > 0 && Character.isDigit(regex.charAt(0))) {
                val *= 10;
                val += Character.digit(regex.charAt(0), 10);
                regex = regex.substring(1);
            }

            if (regex.length() > 0 && regex.charAt(0) == '}') { //if it's {#} we need to match exact
                return new Quantifier(val, val, regex.substring(1));
            } else if (regex.length() > 0 && regex.charAt(0) == ',') { // can be {#,} or {#,#}
                int min = val;

                regex = regex.substring(1);
                if (regex.length() > 0 && regex.charAt(0) == '}') { // {#,}
                    new Quantifier(min, -1, regex.substring(1));
                } else { // determine if it's a valid // {#,#}
                    if (regex.length() > 0 && Character.isDigit(regex.charAt(0))) {
                        val = Character.digit(regex.charAt(0), 10);
                        regex = regex.substring(1);
                        while (regex.length() > 0 && Character.isDigit(regex.charAt(0))) {
                            val *= 10;
                            val += Character.digit(regex.charAt(0), 10);
                            regex = regex.substring(1);
                        }
                    }

                    if (regex.length() > 0 && regex.charAt(0) == '}') { //valid {#,#}
                        return new Quantifier(min, val, regex.substring(1));
                    }
                    //reached here, means we did {#,#<anything but '}'> so not a valid quantifier. consume nothing
                }
            }
        }

        return null;
    }
}