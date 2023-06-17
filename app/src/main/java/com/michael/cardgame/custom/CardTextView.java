package com.michael.cardgame.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.michael.cardgame.R;

public class CardTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CardTextView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.font);
        setTypeface(typeface);
    }

    public CardTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CardTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

}
