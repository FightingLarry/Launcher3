package com.android.launcher3;

import android.animation.AnimatorSet;

/**
 * v4.0
 */
public interface ApplicationActionListener {

    boolean isShowDeleteView();

    void hideDeleteView();

    void showDeleteView();

    void setNeedShowDeleteView(boolean needShowDeleteView);

    void startAnimation();

    void cancleAnimator();

    boolean isAnimatorNull();

    void setAnimator(AnimatorSet animator);

    void setApplicationAnimatorId(long id);

    boolean isAnimatorRunning();
}
