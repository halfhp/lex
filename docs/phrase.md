# Phrase vs Lex: a Comparison & Migration Guide
Square's excellent Phrase lib is a staple of the i18n Android developer's toolbelt and the inspiration 
for Lex.  Phrase is easy enough use and still works, but we like to think Lex is an all-around improvement.

## Safety
A major goal of Lex is to make it as difficult as possible to write code that does something unexpected.
In the example below we demonstrate attempting to retrieve a converted value without supplying any 
key/value pairs to inject.

This compiles (but really shouldnt):
```
Phrase.from(context, "Hi {name}.").format();
```

This does not compile:
```
Lex.say("Hi {NAME}.").make();
```

You probably noticed Lex doesn't take a Context parameter.  Lex always uses
the application context when inflating resources, and as a result is safe to use anywhere, any time.
Phrase on the other hand accepts Fragment and Activity params which can become null when invoked
from a background thread.

Phrase also has one very easy to misuse method.  What do you think this produces:

```
Phrase.from(context, R.string.my_phrase).put("thing", R.string.donkey).format();
```

If you think it prints "some donkey" then you're wrong.  Instead, the int value of the resource is injected as the key value.  
In fact there is no way to directly pass a string resourceId into a put in Phrase.   Worse yet, misusing 
this method produces no obvious errors, even at runtime.

Lex does exactly the opposite; you can pass in a string resourceId, but not an int:

```
Lex.say(R.string.my_phrase).with(LexKey.THING, R.string.donkey).make();
```

If you do try to pass in an arbitrary int, the compiler will unfortunately not throw an error but
Intellij and Android-lint will.  If you have all of these safety checks disabled, you'll still get 
the fail-fast behavior of the app crashing when the invalid resourceId is used.

## Simpler Usage
Lex was designed to make the syntax as compact as possible without sacrificing readability.  And because
of the way Lex is initialized, it's methods require fewer parameters.

Phrase:
```
String str = Phrase.from(context, "some {thing}")
    .put("thing", "donkey").format().toString();
```

Lex:
```
String str = Lex.say("some {THING}")
    .with(LexKey.THING, "donkey").makeString();
```

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
While Phrase is consistently 2-3x faster than Lex, unless you're optimizing at the microsecond
level it's **extremely** unlikely that either library will create performance issues in your app.

# How
TODO