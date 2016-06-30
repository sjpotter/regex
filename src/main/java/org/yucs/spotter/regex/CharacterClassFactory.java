package org.yucs.spotter.regex;

class CharacterClassFactory {
    CharacterClass c;
    int regex_pos;

    static CharacterClassFactory getCharacterClass(String regex, int regex_pos) throws RegexException {
        CharacterClassFactory w = new CharacterClassFactory();

        //NOTE: always make sure that the regex string is advanced if new cases are added
        switch (regex.charAt(regex_pos)) {
            case '[':
                int end = regex.indexOf(']', regex_pos);
                if (end == -1) {
                    throw new RegexException("need to end character class (started at index: " + regex_pos + ") with a brace");
                }
                //cut out the [ and ]
                w.c = new CharacterClass(regex, regex_pos+1, end-1);
                w.regex_pos = end + 1;
                break;
            case '\\':
                w.c = new CharacterClass(regex, regex_pos, regex_pos + 1);
                w.regex_pos = regex_pos + 2;
                break;
            case '.':
                w.c = CharacterClass.global;
                w.regex_pos = regex_pos + 1;
                break;
            case '^':
            case '$':
            // uncomment when handle these special characters
            // case '|':  //as this regex handles things character by character this will be hard to implement
            case '?':
            case '+':
            case '*':
                throw new RegexException("invalid character in regex: " + regex.charAt(regex_pos) + " at index: " + regex_pos);
            default: //plain character
                w.c = new CharacterClass(regex, regex_pos, regex_pos);
                w.regex_pos = regex_pos + 1;
        }

        return w;
    }
}