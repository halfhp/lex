# Lex [![CircleCI](https://circleci.com/gh/halfhp/lex.svg?style=shield)](https://circleci.com/gh/halfhp/lex) [![codecov](https://codecov.io/gh/halfhp/lex/branch/master/graph/badge.svg)](https://codecov.io/gh/halfhp/lex)
A string templating library for Android.  Lex was inspired by the Square's excellent templating
library [Phrase](https://github.com/square/phrase).  If you're a Phrase user check out our [Phrase Comparison & Migration](docs/phrase.md) guide.

Benefits:
* Superior readability for developers and translators
* Flexible configuration to support a wide range of translator / project formats to simplify migration
and sharing of resources among projects.
* Safer than traditional formatted Strings and similar template libraries.

#### Examples

##### Into a TextView
```
// prints "that donkey is happy"
Lex.say("that {ANIMAL} is {MOOD}."
    .with(LexKey.ANIMAL, "donkey")
    .with(LexKey.MOOD, "happy").to(someTextView);
```

##### As a CharSequence:
```
strings.xml:
<string name="that_animal">that {ANIMAL}.</string>

...

// prints "that donkey."
CharSequence cs = Lex.say(R.string.that_animal)
    .with(LexKey.ANIMAL, R.string.donkey).make();
```

##### As a String:
``` 
String str = Lex.say(R.string.that_animal)
    .with(LexKey.ANIMAL, R.string.donkey).makeString();
```

##### List Formatting:
```
// prints "One, Two, and Three"
String str = Lex.list("One", "Two", "Three")
    .separator(", ")
    .lastItemSeparator(", and ").make();
```

### Add Lex as a dependency
TODO

### Initialize Lex
Add the following to the top of your app's Application.onCreate method:
```
Lex.init(this);
```

**IMPORTANT:** Don't initialize Lex outside of Application.onCreate as it can have unexpected consequences.

### Define your keys
Create an enum that implements Lex.Key:
```
public enum LexKey implements Lex.Key {
    QUANTITY,
    DAYS,
    HOURS,
    MINUTES,
    MESSAGE,
    ADDRESS_1
    FIRST_NAME,
    LAST_NAME
}
```

Remeber that key names are case-sensitive in Lex.  For clarity
it's strong suggested that you use the upper-case naming convention shown above.

### Create your template strings
As mentioned above, Lex key names are case sensitive and your strings.xml key usage must exactly match 
what is declared in your key enum.

Doesn't Work:
```
<string name="tries_remaining">Tries remaining: {quantity}</string>
<string name="tries_remaining">Tries remaining: {Quantity}</string>
```

Works:
```
<string name="tries_remaining">Tries remaining: {QUANTITY}</string>
```

### List Formatting
Lex can combine arbitrary length lists using conditional separators to produce properly formatted strings.  Lex's
fluent API for lists takes a sequence of zero or more `CharSequence` elements:

```
LexList list = Lex.list("One" "Two", "Three")
```

And provides four configuration options that can be combined for desired behavior:

```
// default separator
list.separator(",")
```
```
// if set, will always be used when separating a list of exactly two items.
list.twoItemSeparator(" and ")
```
```
// if set, will always be used when separating the last to items in the list, unless
// the list is exactly two items long and a twoItemSeparator has also been set.
list.lastItemSeparator(", and")
```
```
// text to return when formatting an empty list.  default is "".
list.emptyText("No items.")
```

For example, if we wanted to format abitrary length lists of items with an Oxford Comma: 
```
Lex.list(numbers)
    .separator(", ")
    .twoItemSeparator(" and ")
    .lastItemSeparator(", and ").make().toString()
```
If `numbers` is `{"One", "Two", "Three"}` then "One, Two, and Three" is printed.
If `numbers` is `{"One", "Two"}` then it prints "One and Two".

Or if you don't want the Oxford Comma:
```
Lex.list(numbers)
    .separator(", ")
    .lastItemSeparator(" and ").make().toString()
```
Now, if `numbers` is `{"One", "Two", "Three"}` then "One, Two and Three" is printed.
If `numbers` is `{"One", "Two"}` then it prints "One and Two".