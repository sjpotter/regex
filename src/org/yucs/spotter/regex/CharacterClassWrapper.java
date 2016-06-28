package org.yucs.spotter.regex;

class CharacterClassWrapper {
    CharacterClass c;
    String regex;

    public static CharacterClassWrapper getCharacterClass(String regex) throws Exception {
        CharacterClassWrapper w = new CharacterClassWrapper();

        //NOTE: always make sure that the regex string is advanced if new cases are added
        switch (regex.charAt(0)) {
            case '[':
                int end = regex.indexOf(']');
                if (end == -1) {
                    throw new Exception("need to end character class with a brace");
                }
                w.c = new CharacterClass(regex.substring(1, end + 1));
                w.regex = regex.substring(end + 1);
                break;
            case '\\':
                w.c = new CharacterClass(regex.substring(0, 2));
                w.regex = regex.substring(2);
                break;
            case '.':
                w.c = CharacterClass.global;
                w.regex = regex.substring(1);
                break;
            case '^':
            case '$':
//uncomment when handle these special characters
//            case '|':
            case '?':
            case '+':
            case '*':
                throw new Exception("invalid character in regex: " + regex.charAt(0));
            default: //plain character
                w.c = new CharacterClass(regex.substring(0, 1));
                w.regex = regex.substring(1);
        }

        return w;
    }
}
