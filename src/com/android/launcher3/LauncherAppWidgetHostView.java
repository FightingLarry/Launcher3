/*
 * Copyright (C) 2009 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.android.launcher3.DragLayer.TouchCompleteListener;
import com.dh.home.editmode.EditModeManager;
import com.dh.utils.ViewUtils;

import java.util.ArrayList;

/**
 * {@inheritDoc}
 */
public class LauncherAppWidgetHostView extends AppWidgetHostView
        implements
            TouchCompleteListener,
            ApplicationActionListener {

    LayoutInflater mInflater;

    private CheckLongPressHelper mLongPressHelper;
    private Context mContext;
    private int mPreviousOrientation;
    private DragLayer mDragLayer;

    private float mSlop;

    // v4.0
    private ShortcutDeleteView mDeleteView;

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        mContext = context;
        mLongPressHelper = new CheckLongPressHelper(this);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDragLayer = ((Launcher) context).getDragLayer();

        // v4.0 start
        setChildrenDrawingOrderEnabled(true);
        mDeleteView = new ShortcutDeleteView(mContext);
        int processPX = (int) ViewUtils.dpToPx(context, 8);
        int currentPadingBottom = mDeleteView.getPaddingBottom() + processPX;
        int currentPadingRight = mDeleteView.getPaddingRight() + processPX;
        mDeleteView.setAroundPadding(mContext, mDeleteView.getPaddingLeft(), currentPadingRight,
                mDeleteView.getPaddingTop(), currentPadingBottom);
        if (context instanceof Launcher) {
            Launcher launcher = (Launcher) context;
            mDeleteView.setOnClickListener(launcher);
            if (EditModeManager.isEditMode()) {
                showDeleteView();
            } else {
                hideDeleteView();
            }
        }
        addView(mDeleteView);
        // v4.0 end
    }

    // v4.0
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (i == 0) {
            return childCount - 1;
        } else {
            return i - 1;
        }

    }

    @Override
    protected View getErrorView() {
        return mInflater.inflate(R.layout.appwidget_error, this, false);
    }

    @Override
    public void updateAppWidget(RemoteViews remoteViews) {
        // Store the orientation in which the widget was inflated
        mPreviousOrientation = mContext.getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean isReinflateRequired() {
        // Re-inflate is required if the orientation has changed since last inflated.
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (mPreviousOrientation != orientation) {
            return true;
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Just in case the previous long press hasn't been cleared, we make sure to start fresh
        // on touch down.
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mLongPressHelper.cancelLongPress();
        }

        // Consume any touch events for ourselves after longpress is triggered
        if (mLongPressHelper.hasPerformedLongPress()) {
            mLongPressHelper.cancelLongPress();
            return true;
        }

        // Watch for longpress events at this level to make sure
        // users can always pick up this widget
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLongPressHelper.postCheckForLongPress();
                mDragLayer.setTouchCompleteListener(this);
                // v4.0 start
                if (EditModeManager.isEditMode()) {
                    float x = ev.getX();
                    float y = ev.getY();

                    int height = mDeleteView.getHeight();
                    int weith = mDeleteView.getWidth();

                    if (x < weith && y < height) {
                        return false;
                    } else
                        return true;
                }
                // v4.0 end
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, ev.getX(), ev.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }

        // Otherwise continue letting touch events fall through to children
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        // If the widget does not handle touch, then cancel
        // long press when we release the touch
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, ev.getX(), ev.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }
        // v4.0
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        mLongPressHelper.cancelLongPress();
    }

    @Override
    public void onTouchComplete() {
        if (!mLongPressHelper.hasPerformedLongPress()) {
            // If a long press has been performed, we don't want to clear the record of that since
            // we still may be receiving a touch up which we want to intercept
            mLongPressHelper.cancelLongPress();
        }
    }

    @Override
    public int getDescendantFocusability() {
        return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    }

    // V4.0 start
    private AnimatorSet mAnimatorSet;

    @Override
    public boolean isShowDeleteView() {
        return mDeleteView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void hideDeleteView() {
        mDeleteView.setVisibility(View.GONE);
    }

    @Override
    public void showDeleteView() {
        mDeleteView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNeedShowDeleteView(boolean needShowDeleteView) {

    }

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

    @Override
    public void setAnimator(AnimatorSet animator) {
        this.mAnimatorSet = animator;
    }

    @Override
    public void setApplicationAnimatorId(long id) {
        if (!isAnimatorNull()) {
            ArrayList<Animator.AnimatorListener> listener = mAnimatorSet.getListeners();
            for (Animator.AnimatorListener animatorListener : listener) {
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

    // V4.0 end
}
