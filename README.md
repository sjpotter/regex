# Purpose

So a while ago I was asked a question on a technical interview on coding a simple regex parser on a whiteboard. I didn't
do particularly well on the question, one might even say I "bombed" it.  I really hate white board coding as its not a
natural way of coding.  It doesn't let me think in small snippets and then easily combine those snippets nor give me space
to save my ideas that aren't working yet while I look at another approach.

### Problem as presented

Write a simple regex matcher that can match characters, along with the regex symbols '.' and '*' for any character and 0
to infinity quantification.  This is simple, and in fact Rob Pike wrote a simple 30 line C program to do the same thing.
<sup>[1](#myfootnote1)</sup><sup>,</sup><sup>[2](#myfootnote2)</sup>

### My interview solution

On the interview I went down the wrong path of trying to tokenize strings separate from the . and the *, so that i could
match strings in larger chunks.  This is a terrible idea.  One thing I've since learned, especially for these questions
is to try to first solve base cases before going onto more complicated solutions and Rob Pike's example is a perfect
illustration of this.

### Rob Pike's Solution

the base case is

 - if we haven't reached the end of the text, check that the character we are up to in the regex is eithe a '.' or the
 same character we are up to in the text and then continue

```c
int matchhere(char *regexp, char *text)
{
    if (regexp[0] == '\0')
            return 1;

    if (*text!='\0' && (regexp[0]=='.' || regexp[0]==*text))
        return matchhere(regexp+1, text+1);

    return 0;
}
```

to add the '*' quantifier we can simply check for the '*' quantifier on the next regex character

 - if the next regex character is a * on the current regex character while passing in the rest of the regex string and
 the text that w are matching again

```c
if (regexp[1] == '*')
    return matchstar(regexp[0], regexp+2, text);
```

matchstar is a simple function

 - from this point in the text, try to match the rest of the regex.  If we can't, see if the regex character we are
 quantifying can the text chracter and repeat if it can, otherwise return false

```c
int matchstar(int c, char *regexp, char *text)
{
    do {    /* a * matches zero or more instances */
    if (matchhere(regexp, text))
        return 1;
    } while (*text != '\0' && (*text++ == c || c == '.'));
    return 0;
}
```

and the full matchhere function for this simple version of Rob Pike's code

```c
int matchhere(char *regexp, char *text)
{
    if (regexp[0] == '\0')
        return 1;
    if (regexp[1] == '*')
        return matchstar(regexp[0], regexp+2, text);
    if (*text!='\0' && (regexp[0]=='.' || regexp[0]==*text))
        return matchhere(regexp+1, text+1);
    return 0;
}
```

### My java Regex matcher

I started off simply writing a matcher similar to Rob Pike's in java, but expanded on it to try to include the full world of
perl type regular expression matching.

---

<a name="myfootnote1">1</a>: https://en.wikipedia.org/wiki/The_Practice_of_Programming

<a name="myfootnote2">2</a>: http://www.cs.princeton.edu/courses/archive/spr09/cos333/beautiful.html

