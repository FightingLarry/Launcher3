package com.android.launcher3;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dh.utils.ViewUtils;

/**
 * v4.0
 */
public class ShortcutDeleteView extends ImageView {
    public ShortcutDeleteView(Context context) {
        super(context);
        init(context);
    }

    public ShortcutDeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShortcutDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setImageResource(R.drawable.application_delete);
        setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
    }


    protected void setAroundPadding(Context context, int left, int right, int top, int bottom) {
        int paddingLeft = (int) ViewUtils.dpToPx(context, left);
        int paddingRight = (int) ViewUtils.dpToPx(context, right);
        int paddingTop = (int) ViewUtils.dpToPx(context, top);
        int paddingBottom = (int) ViewUtils.dpToPx(context, bottom);
        this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    protected void setAroundMargin(Context context, int left, int right, int top, int bottom) {
        int marginLeft = (int) ViewUtils.dpToPx(context, left);
        int marginRight = (int) ViewUtils.dpToPx(context, right);
        int marginTop = (int) ViewUtils.dpToPx(context, top);
        int marginBottom = (int) ViewUtils.dpToPx(context, bottom);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        setLayoutParams(params);
    }

}
