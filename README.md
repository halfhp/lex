# Lex
A better way to i18n.

# Usage
TODO

## Naming Keys
Because keys must implement the `LexKey` interface, you're limited to using names that are legal
class names in Java.  The suggested convention is to declare an enum to hold your keys and use
upper-case names with underscores for spaces:

```
public enum MyLexKey {
    QUANTITY,
    DAYS,
    HOURS,
    MINUTES,
    MESSAGE
}
```

Also keep in mind that key names are case sensitive and your strings.xml key usage must exactly match
your defined key names.

Works:
```
<string name="tries_remaining">Tries remaining: {QUANTITY}</string>
```

Doesn't Work:
```
<string name="tries_remaining">Tries remaining: {quantity}</string>
<string name="tries_remaining">Tries remaining: {Quantity}</string>
```

## Escaping Delimiters
TODO (spoiler: not supported)

# Why Lex > Phrase
Square's excellent Phrase lib is a staple of the i18n Android developer's toolbelt and the inspiration 
for Lex.  Phrase is easy enough use and still works, but we like to think Lex is an all-around improvement.

## Safer
Lex makes it harder to have accidents.  In the example below we demonstrate
attempting to retrieve a converted value without supplying any key/value pairs to inject.

This compiles (but really shouldnt):
```
Phrase.from(context, "some {thing}").format();
```

This wont:
```
Lex.say("Foo").make();
```

You'll also notice Lex doesn't take a Context parameter.  This is because Lex always uses
the application context when inflating resources, and as a result is totally to use in your app
anywhere, any time.  Phrase on the other hand can crash your app when invoked from a background thread
if you aren't careful.

Finally, Phrase has one particularly easy to misuse method.  What do you think this produces:

```
Phrase.from(context, R.string.my_phrase).put("thing", R.string.donkey).format().toString();
```

If you think it prints "some donkey" then you're wrong.  For an unknown reason, Phrase includes an
overloaded put that accepts an int value and prints the int value directly.  In fact there is no way
to directly pass a string resourceId into a put in Phrase.   Worse yet, misusing this method produces 
no obvious errors, even at runtime.

Lex does exactly the opposite; you can pass in a string resourceId, but not an int.

If you do try to pass in an arbitrary int, the compiler will unfortunately not throw an error but
Intellij and Android-lint will.  If for some reason you have all of these safety checks disabled,
you'll still at least get the fail-fast behavior of the app crashing the moment the invalid text is parsed.


## Simpler Usage
Lex was designed to make the syntax as compact as possible without sacrificing readability.  And because
of the way Lex is initialized, it's methods require fewer parameters.

Phrase:
```
String str = Phrase.from(context, "some {thing}").put("thing", "donkey").format().toString();
```

Lex:
```
String str = Lex.say("some {thing}").with("thing", "donkey").makeString();
```

## More Flexible
One cool feature that is unique to Lex is the ability to configure keyword delimiters; by default
`{` and `}` are used, but if for some reason you need to use something else (to match your translator's
software configuration for example) you can:

```
Lex.init(app, "<<", ">>");
```

# Upgrading from Phrase
TODO