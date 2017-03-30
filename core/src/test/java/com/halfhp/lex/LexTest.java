package com.halfhp.lex;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class LexTest {

    static {
        Lex.init(RuntimeEnvironment.application);
    }

    enum MyLexKeys implements Lex.LexKey {
        KEY_ONE,
        KEY_TWO
    }

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test(expected = Lex.LexAlreadyInitializedException.class)
    public void init_throwsException_onSecondInvocation() {
        Lex.init(RuntimeEnvironment.application);
    }

    @Test
    public void makeString_inflatesKeys() {
        assertEquals("This is a test.", Lex.say("This is {KEY_ONE}.").with(MyLexKeys.KEY_ONE, "a test").make());
    }

    @Test public void make_retainsSpans() {
        SpannableStringBuilder ssb =
                new SpannableStringBuilder("Hello {KEY_ONE}, you are {KEY_TWO} years old.");
        ssb.setSpan("bold", 5, 28, 0);
        CharSequence formatted  = Lex.say(ssb).with(MyLexKeys.KEY_ONE, "Abe").with(MyLexKeys.KEY_TWO, "20").make();
        assertEquals("Hello Abe, you are 20 years old.", formatted.toString());
        assertTrue(formatted instanceof Spannable);
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void with_throwsException_onNullKey() {
        Lex.say("This is {KEY_ONE}.").with(null, "a test").make();
    }

    @Test(expected = Lex.LexInvalidValueException.class)
    public void with_throwsException_onNullValue() {
        Lex.say("This is {KEY_ONE}.").with(MyLexKeys.KEY_ONE, null).make();
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void make_throwsException_ifMissingNonOptionalKeys() {
        Lex.say("This is {KEY_ONE}.")
                .with(MyLexKeys.KEY_ONE, "key one")
                .with(MyLexKeys.KEY_TWO, " key two").make();
    }

    @Test(expected = Lex.LexInvalidKeyException.class)
    public void make_throwsException_ifWrongKeys() {
        Lex.say("This is {KEY_ONE}.").with(MyLexKeys.KEY_TWO, "a test").make();
    }

    @Test
    public void to_setsTextViewValue() {
        TextView textView = new TextView(RuntimeEnvironment.application);
        Lex.say("This is {KEY_ONE}.").with(MyLexKeys.KEY_ONE, "a test").to(textView);
        assertEquals("This is a test.", textView.getText().toString());
    }
}