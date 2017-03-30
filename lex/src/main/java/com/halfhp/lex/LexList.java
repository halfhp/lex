package com.halfhp.lex;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.widget.TextView;

import com.halfhp.lex.exception.LexNoSeparatorException;

import java.util.ArrayList;
import java.util.List;

public class LexList {

    private CharSequence separator;
    private CharSequence twoItemSeparator;
    private CharSequence lastItemSeparator;
    private CharSequence emptyText = "";
    private final CharSequence[] items;
    private List<CharacterStyle> styles;

    LexList(@NonNull CharSequence[] items) {
        this.items = items;
    }

    @NonNull
    public LexList separator(@StringRes int stringRes) {
        return separator(Lex.resources.getString(stringRes));
    }

    @NonNull
    public LexList separator(@NonNull CharSequence separator) {
        this.separator = separator;
        return this;
    }

    @NonNull
    public LexList twoItemSeparator(@StringRes int stringRes) {
        return twoItemSeparator(Lex.resources.getString(stringRes));
    }

    @NonNull
    public LexList twoItemSeparator(@NonNull CharSequence twoItemSeparator) {
        this.twoItemSeparator = twoItemSeparator;
        return this;
    }

    @NonNull
    public LexList lastItemSeparator(@StringRes int stringRes) {
        return lastItemSeparator(Lex.resources.getString(stringRes));
    }

    @NonNull
    public LexList lastItemSeparator(@NonNull CharSequence lastItemSeparator) {
        this.lastItemSeparator = lastItemSeparator;
        return this;
    }

    @NonNull
    public LexList emptyText(@NonNull CharSequence noItemText) {
        this.emptyText = noItemText;
        return this;
    }

    @NonNull
    public LexList wrappedIn(@NonNull CharacterStyle span) {
        if (styles == null) {
            styles = new ArrayList<>();
        }
        styles.add(span);
        return this;
    }

    public void into(@NonNull TextView textView) {
        textView.setText(make());
    }

    @NonNull
    private CharSequence getWrappedItem(int index) {
        if (styles != null) {
            SpannableString s = new SpannableString(items[index]);
            for (CharacterStyle style : styles) {
                s.setSpan(CharacterStyle.wrap(style), 0, s.length(), 0);
            }
            return s;
        }
        return items[index];
    }

    @NonNull
    public CharSequence make() {
        switch (items.length) {
            case 0:
                return emptyText;
            case 1:
                return getWrappedItem(0);
            case 2:
                return makeTwoItems();
            default:
                return makeThreeOrMoreItems();
        }
    }

    @NonNull
    public String makeString() {
        return make().toString();
    }

    @NonNull
    private CharSequence makeTwoItems() {
        CharSequence s;
        if (twoItemSeparator != null) {
            s = twoItemSeparator;
        } else if (separator != null) {
            s = separator;
        } else {
            throw new LexNoSeparatorException();
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(getWrappedItem(0));
        sb.append(s);
        sb.append(getWrappedItem(1));
        return sb;
    }

    @NonNull
    private CharSequence makeThreeOrMoreItems() {
        if (separator == null) {
            throw new LexNoSeparatorException();
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(getWrappedItem(0));
        for (int i = 1; i < items.length; i++) {
            if (i == items.length - 1 && lastItemSeparator != null) {
                sb.append(lastItemSeparator);
            } else {
                sb.append(separator);
            }
            sb.append(getWrappedItem(i));
        }
        return sb;
    }
}
