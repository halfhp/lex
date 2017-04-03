package com.halfhp.lex;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.halfhp.lex")
public class LexTest {

    enum LexKey implements Lex.Key {
        THING,
        KEY_ONE,
        KEY_TWO
    }

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before() {
        if(Lex.resources == null) {
            Lex.init(RuntimeEnvironment.application);
        }
    }

    @Test(expected = Lex.LexAlreadyInitializedException.class)
    public void init_throwsException_onSecondInvocation() {
        Lex.init(RuntimeEnvironment.application);
    }

    @Test(expected = Lex.LexNotInitializedException.class)
    public void make_throwsException_ifNotInitialized() {
        Lex.resources = null;
        assertEquals("This is key1 and key2.", Lex.say("This is {KEY_ONE} and {KEY_TWO}.")
                .with(LexKey.KEY_ONE, "key1")
                .with(LexKey.KEY_TWO, "key2").makeString());
    }

    @Test
    public void makeString_inflatesKeys() {
        assertEquals("This is key1 and key2.", Lex.say("This is {KEY_ONE} and {KEY_TWO}.")
                .with(LexKey.KEY_ONE, "key1")
                .with(LexKey.KEY_TWO, "key2").makeString());
    }

    // TODO: waiting on fix foe
    // TODO: https://github.com/robolectric/robolectric/issues/2653
    @Ignore
    @Test
    public void makeString_withResId_inflatesKeys() {
        assertEquals("some thing.", Lex.say(R.string.lex_test_some_thing)
                .with(LexKey.THING, "thing").makeString());
    }

    @Test public void make_retainsSpans() {
        SpannableStringBuilder ssb =
                new SpannableStringBuilder("Hello {KEY_ONE}, you are {KEY_TWO} years old.");
        ssb.setSpan("bold", 5, 28, 0);
        CharSequence formatted  = Lex.say(ssb).with(LexKey.KEY_ONE, "Abe").with(LexKey.KEY_TWO, "20").make();
        assertEquals("Hello Abe, you are 20 years old.", formatted.toString());
        assertTrue(formatted instanceof Spannable);
    }

    @Test
    public void say_withCustomDelimiters_parsesKeys() {
        assertEquals("This is key1 and key2.", Lex.say("This is <<KEY_ONE>> and <<KEY_TWO>>.")
                .delimitBy("<<", ">>")
                .with(LexKey.KEY_ONE, "key1")
                .with(LexKey.KEY_TWO, "key2").makeString());
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void with_throwsException_onNullKey() {
        Lex.say("This is {KEY_ONE}.").with(null, "a test").make();
    }

    @Test(expected = Lex.LexInvalidValueException.class)
    public void with_throwsException_onNullValue() {
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_ONE, null).make();
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void make_throwsException_ifMissingNonOptionalKeys() {
        Lex.say("This is {KEY_ONE}.")
                .with(LexKey.KEY_ONE, "key one")
                .with(LexKey.KEY_TWO, " key two").make();
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void make_throwsException_ifWrongKeys() {
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_TWO, "a test").make();
    }

    @Test
    public void to_setsTextViewValue() {
        TextView textView = new TextView(RuntimeEnvironment.application);
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_ONE, "a test").to(textView);
        assertEquals("This is a test.", textView.getText().toString());
    }

    @Test(expected = Lex.LexMissingKeyException.class)
    public void to_throwsException_onUninflatedKeys() {
        Lex.say("This is {KEY_ONE} and {KEY_TWO}.").with(LexKey.KEY_ONE, "key one").make();
    }

    @Test(expected = Lex.LexDuplicateKeyException.class)
    public void with_throwsException_onDuplicateKeys() {
        Lex.say("This is {KEY_ONE}.")
                .with(LexKey.KEY_ONE, "a test")
                .with(LexKey.KEY_ONE, "a test")
                .make();
    }
}