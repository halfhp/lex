package com.halfhp.lex;

import android.support.annotation.NonNull;
import android.text.style.CharacterStyle;
import android.widget.TextView;

import com.halfhp.lex.exception.LexMissingKeyException;

public class ParsedLexString extends LexString {

    ParsedLexString(LexContext context) {
        super(context);
    }

    public void into(@NonNull TextView textView) {
        textView.setText(make());
    }

    @NonNull
    public ParsedLexString wrappedIn(@NonNull CharacterStyle span) {
        context.lastTransform.wrapWith(span);
        return this;
    }

    @NonNull
    private CharSequence getValidatedText() {
        // TODO: this match slows things down quite a bit...could use some optimization.
        if (context.matcher == null) {
            context.matcher = context.keyPattern.matcher(context.inflatedText);
        } else {
            context.matcher.reset(context.inflatedText);
        }
        if (context.matcher.find()) {
            throw new LexMissingKeyException(String
                    .format("Key %s not supplied into template: [%s]", context.matcher.group(1), context.templateText));
        }
        return context.inflatedText;
    }

    @NonNull
    public CharSequence make() {
        if (!context.lastTransform.isEmpty()) {
            context.lastTransform.apply(context);
        }
        return getValidatedText();
    }

    @NonNull
    public String makeString() {
        return make().toString();
    }

    @Override
    protected ParsedLexString getParsedLexString(@NonNull LexContext context) {
        return this;
    }
}
