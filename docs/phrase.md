# Phrase & Lex: a Comparison
Square's excellent Phrase lib is a staple of the i18n Android developer's toolbelt and the inspiration 
for Lex.  The major difference between the two libraries is that Lex tries to be safer and easier to use 
at a nominal cost to performance.

## Safety
Lex makes it as difficult as possible to write code that does the wrong thing.
In the example below we compare attempting to retrieve a converted value without supplying any 
key/value pairs to inject in Lex and Phrase.

Compiles (but really shouldnt):
```
Phrase.from(context, "Hi {name}.").format();
```

Does not compile:
```
Lex.say("Hi {NAME}.").make();
```

You'll notice Phrase takes a Context param but Lex doesn't.  Lex always uses
the application context when inflating resources, and as a result is safe to use anywhere, any time.
Phrase uses Fragment and Activity params which can become invalid when invoked
from a background thread.

Phrase also has one very easy to misuse method.  What do you think this produces:

```
Phrase.from(context, "some {thing}").put("thing", R.string.donkey).format();
```

If you think it prints "some donkey" then you're wrong.  Instead, the int value of `R.string.donkey`.
In fact there is no way to directly pass a string resourceId into a `put` in Phrase.   Worse yet, misusing 
this method produces no obvious errors and will print the wrong result at runtime.

Lex does exactly the opposite; you can pass in a string resourceId, but not an int:

```
Lex.say(R.string.my_phrase).with(LexKey.THING, R.string.donkey).make();
```

If you do pass in an arbitrary int, the compiler will unfortunately not throw an error but
Intellij and Android-lint will.  If you have all of these safety checks disabled, you'll still get 
the fail-fast behavior of the app crashing when the invalid resourceId is used.

## Simpler Usage
Lex tries to make the syntax as compact as possible without sacrificing readability.  And because
of the way Lex is initialized, it's methods require fewer params.

Phrase:
```
Phrase.from(context, "some {thing}")
    .put("thing", "donkey").format().toString();
```

Lex:
```
Lex.say("some {THING}")
    .with(LexKey.THING, "donkey").makeString();
```

Those that follow the MVP pattern in their apps will also notice the opportunity to eliminate references 
to a Context in Presenters that need to provide formatted text for their View.

## More Flexible
One cool feature that is unique to Lex is the ability to configure keyword delimiters; by default
`{` and `}` are used, but if for some reason you need to use something else (to match your translator's
software configuration for example) you can:

Set delimiters globally:
```
Lex.init(app, "<<", ">>");
```

Set delimiters for a single call:
```
Lex.say("some <<THING}=>>")
    .delimitBy("<<", ">>")
    .with(LexKey.THING, "horse").make();
```

## Performance
Under most conditions Lex is a bit slower than Phrase.  Unless you're counting microseconds, or formatting 
hundreds of pages of content at once, then the performance difference should not be detectable.