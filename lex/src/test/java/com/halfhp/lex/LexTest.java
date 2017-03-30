package com.halfhp.lex;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.halfhp.lex.exception.LexAlreadyInitializedException;
import com.halfhp.lex.exception.LexDuplicateKeyException;
import com.halfhp.lex.exception.LexInvalidKeyException;
import com.halfhp.lex.exception.LexInvalidValueException;
import com.halfhp.lex.exception.LexMissingKeyException;
import com.halfhp.lex.exception.LexNoSeparatorException;
import com.halfhp.lex.exception.LexNotInitializedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RuntimeEnvironment;

import java.text.DecimalFormat;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(LexTestRunner.class)
public class LexTest {

    enum LexKey implements com.halfhp.lex.LexKey {
        COUNT,
        SOMETHING,
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

    @Test(expected = LexAlreadyInitializedException.class)
    public void init_throwsException_onSecondInvocation() {
        Lex.init(RuntimeEnvironment.application);
    }

    @Test(expected = LexNotInitializedException.class)
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

    @Test
    public void makeString_withResId_inflatesKeys() {
        assertEquals("some thing.", Lex.say(R.string.thing_template)
                .with(LexKey.SOMETHING, "thing").makeString());
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

    @Test(expected = LexInvalidKeyException.class)
    public void with_throwsException_onNullKey() {
        Lex.say("This is {KEY_ONE}.").with(null, "a test").make();
    }

    @Test(expected = LexInvalidValueException.class)
    public void with_throwsException_onNullValue() {
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_ONE, null).make();
    }

    @Test(expected = LexInvalidKeyException.class)
    public void make_throwsException_ifMissingNonOptionalKeys() {
        Lex.say("This is {KEY_ONE}.")
                .with(LexKey.KEY_ONE, "key one")
                .with(LexKey.KEY_TWO, " key two").make();
    }

    @Test(expected = LexInvalidKeyException.class)
    public void make_throwsException_ifWrongKeys() {
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_TWO, "a test").make();
    }

    @Test
    public void into_setsTextViewValue() {
        TextView textView = new TextView(RuntimeEnvironment.application);
        Lex.say("This is {KEY_ONE}.").with(LexKey.KEY_ONE, "a test").into(textView);
        assertEquals("This is a test.", textView.getText().toString());
    }

    @Test(expected = LexMissingKeyException.class)
    public void into_throwsException_onUninflatedKeys() {
        Lex.say("This is {KEY_ONE} and {KEY_TWO}.").with(LexKey.KEY_ONE, "key one").make();
    }

    @Test(expected = LexDuplicateKeyException.class)
    public void with_throwsException_onDuplicateKeys() {
        Lex.say("This is {KEY_ONE}.")
                .with(LexKey.KEY_ONE, "a test")
                .with(LexKey.KEY_ONE, "a test")
                .make();
    }

    @Test
    public void with_wrappedIn() {
        CharSequence result = Lex.say(R.string.this_is_a_template)
                .with(LexKey.SOMETHING, "test")
                .wrappedIn(new ForegroundColorSpan(Color.RED))
                .make();

        final int spanCount = ((SpannableStringBuilder) result)
                .getSpans(0, result.length(), Object.class).length;

        // value is wrapped both in the replacement text substitute and
        // in the specified style, so expect 2:
        assertEquals(2, spanCount);
    }

    @Test
    public void with_resourceId() {
        assertEquals("This is a test.", Lex
                .say(R.string.this_is_a_template)
                .with(LexKey.SOMETHING, R.string.test)
                .makeString());
    }

    @Test
    public void withNumber() {
        assertEquals("3 fingers", Lex
                .say("{COUNT} fingers")
                .withNumber(LexKey.COUNT, 3)
                .makeString());
    }

    @Test
    public void withNumber_andNumberFormat() {
        assertEquals("3.5 average", Lex
                .say("{COUNT} average")
                .withNumber(LexKey.COUNT, 3.5, new DecimalFormat("#.#"))
                .makeString());
    }

    @Test
    public void withPlural() {
        int count = 3;
        assertEquals("3 Names", Lex
                .say("{COUNT} {SOMETHING}")
        .with(LexKey.COUNT, String.valueOf(count))
        .withPlural(LexKey.SOMETHING, count, R.plurals.name).makeString());

        count = 1;
        assertEquals("1 Name", Lex
                .say("{COUNT} {SOMETHING}")
                .with(LexKey.COUNT, String.valueOf(count))
                .withPlural(LexKey.SOMETHING, count, R.plurals.name).makeString());
    }

    @Test
    public void to_populatesTextView() {
        TextView tv = new TextView(RuntimeEnvironment.application);

        Lex.say(R.string.this_is_a_template)
                .with(LexKey.SOMETHING, "test")
                .into(tv);

        assertEquals("This is a test.", tv.getText().toString());
    }

    @Test
    public void list_withListArg() {
        assertEquals("One Two Three", Lex
                .list(Arrays.asList("One", "Two", "Three"))
                .separator(" ")
                .makeString());
    }

    @Test
    public void list_ofTwo_usesLastItemSeparator() {
        assertEquals("One, Two and Three", Lex.list("One", "Two", "Three")
                .separator(", ")
                .lastItemSeparator(" and ").make().toString());
    }

    @Test
    public void list_ofThree_usesLastItemSeparator() {
        assertEquals("One, Two, and Three", Lex.list("One", "Two", "Three")
                .separator(", ")
                .lastItemSeparator(", and ").make().toString());

    }

    @Test
    public void list_ofThree_usesSeparator() {
        assertEquals("One, Two, Three", Lex.list("One", "Two", "Three")
                .separator(", ")
                .make().toString());
    }

    @Test
    public void list_ofMoreThanThree_usesLastItemSeparator() {
        assertEquals("One, Two, Three, and Four", Lex.list("One", "Two", "Three", "Four")
                .separator(", ")
                .lastItemSeparator(", and ").make().toString());
    }

    @Test
    public void list_usesTwoItemSeparator_IfTwoItems() {
        assertEquals("One and Two", Lex.list("One", "Two")
                .separator(", ")
                .twoItemSeparator(" and ")
                .lastItemSeparator(", and ").make().toString());
    }

    @Test
    public void list_ofNoItems_usesEmptyText() {
        assertEquals("No items.", Lex.list(new CharSequence[]{})
                .separator(", ")
                .twoItemSeparator(" and ")
                .lastItemSeparator(", and ")
                .emptyText("No items.")
                .make().toString());
    }

    @Test
    public void list_wrappedInSpan() {
        final CharSequence result = Lex
                .list("One", "Two")
                .wrappedIn(new ForegroundColorSpan(Color.RED))
                .separator(", ")
                .make();

        final int spanCount = ((SpannableStringBuilder) result)
                .getSpans(0, result.length(), Object.class).length;

        assertEquals(2, spanCount);
    }

    @Test
    public void list_separator_withResourceId() {
        assertEquals("One and Two", Lex.list("One", "Two").separator(R.string.and_separator).makeString());
    }

    @Test
    public void list_twoItemSeparator_withResourceId() {
        assertEquals("One and Two", Lex.list("One", "Two").twoItemSeparator(R.string.and_separator).makeString());
    }

    @Test
    public void list_lastItemSeparator_withResourceId() {
        assertEquals("One, Two and Three", Lex
                .list("One", "Two", "Three")
                .separator(R.string.comma_separator)
                .lastItemSeparator(R.string.and_separator).makeString());
    }

    @Test(expected = LexNoSeparatorException.class)
    public void list_ofThreeOrMoreItemsWithoutSeparator_throwsLexNoSeparatorException() {
        Lex.list("One", "Two", "Three", "Four").make();
    }

    @Test
    public void list_into_populatesTextView() {
        TextView tv = new TextView(RuntimeEnvironment.application);

        Lex.list("One", "Two")
                .separator(R.string.and_separator)
                .into(tv);

        assertEquals("One and Two", tv.getText().toString());
    }
}