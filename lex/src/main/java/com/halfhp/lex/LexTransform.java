package com.halfhp.lex;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;

import com.halfhp.lex.exception.LexDuplicateKeyException;
import com.halfhp.lex.exception.LexInvalidKeyException;

class LexTransform {

    private String key;
    private CharSequence value;
    private boolean isOptional;

    public boolean isEmpty() {
        return key == null;
    }

    public void store(@NonNull String key, @NonNull CharSequence value, boolean isOptional) {
        this.key = key;
        this.value = value;
        this.isOptional = isOptional;
    }

    public void apply(@NonNull LexContext context) {
        if (context.inflatedKeys.contains(key)) {
            throw new LexDuplicateKeyException(key);
        }
        context.inflatedKeys.add(key);
        context.keyReplaceHolder[0] = context.openDelimiter + key + context.closeDelimiter;
        context.valueReplaceHolder[0] = value;
        final CharSequence result = TextUtils.replace(context.inflatedText,
                context.keyReplaceHolder,
                context.valueReplaceHolder);
        if (!isOptional && TextUtils.equals(context.inflatedText, result)) {
            throw new LexInvalidKeyException(String.format("Non-optional Key missing in template. " +
                    "key: %s%s%s template: [%s]", context.openDelimiter, key, context.closeDelimiter, context.templateText));
        } else {
            context.inflatedText = result;
        }
    }

    public void wrapWith(@NonNull CharacterStyle span) {
        SpannableString s = new SpannableString(value);
        s.setSpan(CharacterStyle.wrap(span), 0, value.length(), 0);
        value = s;
    }
}
