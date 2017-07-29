![image](docs/logo.png)

[![CircleCI](https://circleci.com/gh/halfhp/lex.svg?style=shield)](https://circleci.com/gh/halfhp/lex) [![codecov](https://codecov.io/gh/halfhp/lex/branch/master/graph/badge.svg)](https://codecov.io/gh/halfhp/lex)

A string templating library for Android.  Lex was inspired by the Square's excellent templating
library [Phrase](https://github.com/square/phrase).  If you're a Phrase user check out our [Phrase Comparison & Migration](docs/phrase.md) guide.

Benefits:
* Better readability for both developers and translators.
* Configurable to support translator / project formats.
* Safer than traditional formatted Strings and similar template libraries.
* Supports Spannable text.

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

##### More cool stuff:
```
Lex.say(R.string.item_count_template)
    .withNumber(LexKey.COUNT, item.titles.length, countFormat)    // formatted number
    .wrappedIn(new AbsoluteSizeSpan(24, true))                    // make {COUNT} 24dp 
    .wrappedIn(new ForegroundColorSpan(Color.BLUE))               // make it BLUE too
    .withPlural(LexKey.ITEM, item.titles.length, R.plurals.book)  // pluralize 'book'
    .wrappedIn(new ForegroundColorSpan(Color.YELLOW))             // make {ITEM} YELLOW
    .into(bookCount);                                             // put results straight into a TextView
```

# Gradle Dependency
```
dependencies {
    compile 'com.halfhp.lex:lex:1.0.2'
}
```

# Initialize Lex
Add the following to the top of your app's Application.onCreate method:
```
Lex.init(this);
```

**IMPORTANT:** Don't initialize Lex outside of Application.onCreate as it can have unexpected consequences.

# Define some LexKeys
Create an enum that implements LexKey:
```
public enum LexKey implements LexKey {
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

Remeber that key names are case-sensitive in Lex.  For clarity it's strong suggested that you use the 
upper-case naming convention shown above.  We also suggest you try to keep your key names generic so
they can be reused, preventing the list from getting too big, which can make it difficult to spot errors
in template strings.

# Create your template strings
As mentioned above, Lex key names are case sensitive and your strings.xml key usage must exactly match 
what is declared in your key enum.

```
<string name="tries_remaining_template">Tries remaining: {quantity}</string> // WRONG
<string name="tries_remaining_template">Tries remaining: {Quantity}</string> // WRONG
<string name="tries_remaining_template">Tries remaining: {QUANTITY}</string> // OK
```

# List Formatting
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
    .lastItemSeparator(", and ").make()
```
If `numbers` is `{"One", "Two", "Three"}` then "One, Two, and Three" is printed.
If `numbers` is `{"One", "Two"}` then it prints "One and Two".

Or if you don't want the Oxford Comma:
```
Lex.list(numbers)
    .separator(", ")
    .lastItemSeparator(" and ").make()
```
If `numbers` contains `{"One", "Two", "Three"}` then "One, Two and Three" is printed.
If `numbers` contains `{"One", "Two"}` then it prints "One and Two".

#### A note on list separators
For clarity, the examples above use hardcoded strings to define separators.  In a
production app however, these separators should usually be defined in `strings.xml`.
Be aware that Android strips leading and trailing spaces.

```
<string name=""> and </string>
```

Gets stripped and converted to "and", which is not what you want in this case.  To
prevent this, use explicit whitespace characters:

```
<string name="">\u0020and\u0020</string>
```

Which becomes " and ".

# Demo App
For an idiomatic source code example check out the [Demo App](https://github.com/halfhp/lex/tree/master/demo-app).