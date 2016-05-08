package com.android.launcher3;

import java.util.ArrayList;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.dh.home.editmode.EditModeManager;

/**
 * v4.0
 */
public class ShortcutView extends FrameLayout implements ApplicationActionListener {

    private ShortcutDeleteView mDeleteView;
    private BubbleTextView mBubbleTextView;

    private CheckLongPressHelper mLongPressHelper;
    private float mSlop;
    private boolean mNeedShowDeleteView = true;

    public boolean isShowDeleteView() {
        return mDeleteView.getVisibility() == View.VISIBLE;
    }

    public void hideDeleteView() {
        mDeleteView.setVisibility(View.GONE);
    }

    public void showDeleteView() {
        if (mNeedShowDeleteView) {
            mDeleteView.setVisibility(View.VISIBLE);
        } else {
            mDeleteView.setVisibility(View.GONE);
        }
    }

    public void setNeedShowDeleteView(boolean needShowDeleteView) {
        this.mNeedShowDeleteView = needShowDeleteView;
    }

    public boolean getNeedShowDeleteView() {
        return this.mNeedShowDeleteView;
    }

    public ShortcutDeleteView getDeleteView() {
        return mDeleteView;
    }

    private void init() {
        mLongPressHelper = new CheckLongPressHelper(this);
    }

    public BubbleTextView getBubbleTextView() {
        return mBubbleTextView;
    }

    public ShortcutView(Context context) {
        super(context);
        init();
    }

    public ShortcutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShortcutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDeleteView = (ShortcutDeleteView) findViewById(R.id.delete);
        mBubbleTextView = (BubbleTextView) findViewById(R.id.application_icon);
        mNeedShowDeleteView = true;
        if (!EditModeManager.isEditMode()) {
            hideDeleteView();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes
        // the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }
        return result;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    // void setStayPressed(boolean stayPressed) {
    // mStayPressed = stayPressed;
    // if (!stayPressed) {
    // mPressedBackground = null;
    // }
    //
    // // Only show the shadow effect when persistent pressed state is set.
    // if (getParent().getParent() instanceof ShortcutAndWidgetContainer) {
    // CellLayout layout = (CellLayout) getParent().getParent();
    // layout.setPressedIcon(this, mPressedBackground, mOutlineHelper.shadowBitmapPadding);
    // }
    //
    // updateIconState();
    // }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache, boolean setDefaultPadding) {
        mBubbleTextView.applyFromShortcutInfo(info, iconCache, setDefaultPadding, false);
    }

    @Override
    public void setPressed(boolean pressed) {
        // System.out.println("pressed");
        super.setPressed(pressed);
    }

    private AnimatorSet mAnimatorSet;

    @Override
    public void startAnimation() {
        if (mAnimatorSet != null && !mAnimatorSet.isRunning()) {
            mAnimatorSet.setStartDelay(EditModeManager.getApplicationRandomDelay());
            mAnimatorSet.start();
        }
    }

    @Override
    public void cancleAnimator() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    @Override
    public boolean isAnimatorNull() {
        return mAnimatorSet == null;
    }

    public void setAnimator(AnimatorSet animatorSet) {
        this.mAnimatorSet = animatorSet;
    }


    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        Intent intent = null;
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo si = (ShortcutInfo) tag;
            if (si.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
                return;
            }
            intent = si.getIntent();
        } else if (tag instanceof AppInfo) {
            intent = ((AppInfo) tag).getIntent();
        }
        if (intent != null) {
            if ((Intent.ACTION_MAIN.equals(intent.getAction()) && Utilities.isSystemApp(getContext(), intent))) {
                setNeedShowDeleteView(false);
                hideDeleteView();
            }
        }
    }

    @Override
    public void setApplicationAnimatorId(long id) {
        if (!isAnimatorNull()) {
            ArrayList<AnimatorListener> listener = mAnimatorSet.getListeners();
            for (AnimatorListener animatorListener : listener) {
                if (animatorListener instanceof EditModeManager.ApplicationAnimatorListener) {
                    ((EditModeManager.ApplicationAnimatorListener) animatorListener).id = id;
                    break;
                }
            }
        }

    }

    @Override
    public boolean isAnimatorRunning() {
        return mAnimatorSet != null && mAnimatorSet.isRunning();
    }

}
