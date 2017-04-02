# Lex
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
Lex.say("that {ANIMAL} is {MOOD}."
    .with(LexKey.ANIMAL, "donkey")
    .with(LexKey.MOOD, "happy").to(someTextView);
```

##### As a CharSequence:
```
CharSequence cs = Lex.say(R.string.that_animal)
    .with(LexKey.ANIMAL, R.string.donkey).make();
```

##### As a String:
``` 
String str = Lex.say(R.string.that_animal)
    .with(LexKey.ANIMAL, R.string.donkey).makeString();
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

### Usage

#### Custom Delimiters
TODO (spoiler: not supported)