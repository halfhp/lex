package com.halfhp.lex.demo;

import android.content.Context;
import android.graphics.Color;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.halfhp.lex.Lex;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom listview used by {@link ListviewExampleActivity}.  Bonus: since we're storing the refs into our
 * xml props, we don't need into implement the ViewHolder pattern in the normal way.
 */
class MyListItemView extends LinearLayout {

    private NumberFormat GPA_FORMAT = new java.text.DecimalFormat("#.#");

    @BindView(R.id.item_title)
    TextView title;

    @BindView(R.id.item_description)
    TextView description;

    @BindView(R.id.book_count)
    TextView bookCount;

    @BindView(R.id.gpa)
    TextView gpa;

    public MyListItemView(Context context) {
        super(context);
        inflate(context, R.layout.list_item_generic, this);
        ButterKnife.bind(this);
    }

    public void bind(BookList item) {
        Lex.say(R.string.name_template)
                .with(LexKey.FIRST_NAME, item.firstName)
                .with(LexKey.LAST_NAME, item.lastName)
                .into(title);

        Lex.list(item.titles)
                .wrappedIn(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC))
                .wrappedIn(new ForegroundColorSpan(Color.WHITE))
                .separator(R.string.comma_separator)
                .twoItemSeparator(R.string.and_separator)
                .lastItemSeparator(R.string.comma_and_separator)
                .into(description);

        Lex.say(R.string.item_count_template)
                .withNumber(LexKey.COUNT, item.titles.length)
                .wrappedIn(new AbsoluteSizeSpan(24, true))
                .wrappedIn(new ForegroundColorSpan(Color.WHITE))
                .withPlural(LexKey.ITEM, item.titles.length, R.plurals.book)
                .wrappedIn(new ForegroundColorSpan(Color.YELLOW))
                .into(bookCount);

        Lex.say(R.string.gpa_template)
                .withNumber(LexKey.NUMBER, item.gpa, GPA_FORMAT)
                .wrappedIn(new AbsoluteSizeSpan(24, true))
                .into(gpa);
    }

}
