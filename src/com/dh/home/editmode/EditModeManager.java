package com.dh.home.editmode;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.android.launcher3.ApplicationActionListener;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Folder;
import com.android.launcher3.Hotseat;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;

/**
 * v4.0 Created by dh on 2016/5/3.
 */
public class EditModeManager {

    private static boolean isEditMode = false;

    public static boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        EditModeManager.isEditMode = isEditMode;
    }

    public final int sApplicationDelay = 400;

    private final float scaleFrom = 1.0f;
    private final float scaleTo = 0.9f;
    private Long[] mApplicationAnimatorArray = new Long[2];
    public final static Long HOTSEAT_ANIMATOR = -999l;

    private boolean isOpenFolder;


    private static EditModeManager instance;

    private EditModeManager() {
        initApplicationAnimatorArray();
    }

    public synchronized static EditModeManager getInstance() {
        if (instance == null) {
            instance = new EditModeManager();
        }
        return instance;
    }



    public void enterEditMode(Workspace workspace, Hotseat hotseat, int currentPage) {

        // DeleteView
        if (!isEditMode()) {
            hideOrShowDeleteView(workspace, false);
            hideOrShowDeleteView(hotseat, false);
        }

        Folder folder = workspace.getOpenFolder();
        if (folder != null) {
            isOpenFolder = true;
            hideOrShowDeleteView(folder, false);
            startFolderChildAnimator(folder);
        }

        // animator
        CellLayout cell = (CellLayout) workspace.getChildAt(currentPage);
        long id = workspace.getIdForScreen(cell);
        if (mApplicationAnimatorArray[1] != id) {
            startCellAnimator(cell, id);
            mApplicationAnimatorArray[1] = id;
        }
        if (mApplicationAnimatorArray[0] != HOTSEAT_ANIMATOR) {
            startCellAnimator(hotseat.getLayout(), HOTSEAT_ANIMATOR);
            mApplicationAnimatorArray[0] = HOTSEAT_ANIMATOR;
        }

        isEditMode = true;

    }


    public void exitEditMode(Workspace workspace, Hotseat hotseat) {
        if (isEditMode()) {
            hideOrShowDeleteView(workspace, true);
            hideOrShowDeleteView(hotseat, true);
            initApplicationAnimatorArray();
            isEditMode = false;
        }
    }

    public void enterEditModeOnFolder(Folder folder) {
        isOpenFolder = true;
        if (isEditMode) {
            hideOrShowDeleteView(folder, false);
            startFolderChildAnimator(folder);
        } else {
            hideOrShowDeleteView(folder, true);
        }
    }

    public void exitEditModeOnFolder() {
        isOpenFolder = false;
    }



    public void startOnAddInSreenViewAnimator(View v, long id) {
        if (v != null) {
            if (v instanceof ApplicationActionListener) {
                ApplicationActionListener listener = (ApplicationActionListener) v;
                ItemInfo info = (ItemInfo) v.getTag();
                if (info == null) {
                    return;
                }
                listener.setApplicationAnimatorId(id);
                if (!listener.isAnimatorRunning()) {
                    // listener.setAnimator(null);
                    startAnimator(v, id, listener);
                }
            }
        }
    }


    private void hideOrShowDeleteView(ViewGroup viewgroup, boolean hideOrShow) {
        if (viewgroup instanceof Workspace) {
            for (int i = 0; i < viewgroup.getChildCount(); i++) {
                CellLayout celllayout = (CellLayout) viewgroup.getChildAt(i);
                hideOrShowDeleteView(celllayout, hideOrShow);
            }
        } else if (viewgroup instanceof Folder) {
            CellLayout celllayout = ((Folder) viewgroup).getContent();
            hideOrShowDeleteView(celllayout, hideOrShow);
        } else if (viewgroup instanceof Hotseat) {
            CellLayout celllayout = ((Hotseat) viewgroup).getLayout();
            hideOrShowDeleteView(celllayout, hideOrShow);
        }
    }

    private void hideOrShowDeleteView(CellLayout celllayout, boolean hideOrShow) {
        for (int j = 0; j < celllayout.getCountY(); j++) {
            for (int k = 0; k < celllayout.getCountX(); k++) {
                View v = celllayout.getChildAt(k, j);
                if (v != null) {
                    if (v instanceof ApplicationActionListener) {
                        ApplicationActionListener listener = (ApplicationActionListener) v;
                        ItemInfo info = (ItemInfo) v.getTag();
                        if (info == null) {
                            continue;
                        }
                        if (!hideOrShow) {
                            if (!listener.isShowDeleteView()) {
                                listener.showDeleteView();
                            }
                        } else {
                            listener.hideDeleteView();
                        }
                    }
                }
            }
        }
    }


    private void initApplicationAnimatorArray() {
        mApplicationAnimatorArray[0] = -1l;
        mApplicationAnimatorArray[1] = -1l;
    }

    private void startFolderChildAnimator(Folder folder) {
        startCellAnimator(folder.getContent(), -2);
    }

    /**
     * 启动当前屏幕层元素的动画
     *
     * @param celllayout 屏幕层
     * @param id
     */
    private void startCellAnimator(CellLayout celllayout, long id) {

        for (int j = 0; j < celllayout.getShortcutCount(); j++) {
            View v = celllayout.getShortcutAtIndex(j);
            if (v != null) {
                if (v instanceof ApplicationActionListener) {
                    ApplicationActionListener listener = (ApplicationActionListener) v;
                    ItemInfo info = (ItemInfo) v.getTag();
                    if (info == null) {
                        continue;
                    }
                    startAnimator(v, id, listener);
                }
            }
        }
    }


    private void startAnimator(View view, long id, ApplicationActionListener listener) {

        if (!listener.isAnimatorNull()) {
            listener.startAnimation();
            return;
        }

        listener.cancleAnimator();

        long a1During = 500;
        ValueAnimator a1x = ObjectAnimator.ofFloat(view, "scaleX", scaleFrom, scaleTo);
        a1x.setDuration(a1During);
        ValueAnimator a1y = ObjectAnimator.ofFloat(view, "scaleY", scaleFrom, scaleTo);
        a1y.setDuration(a1During);

        AnimatorSet a1 = new AnimatorSet();
        a1.playTogether(a1x, a1y);
        a1.setInterpolator(new DecelerateInterpolator());

        long a2During = 300;
        ValueAnimator a2x = ObjectAnimator.ofFloat(view, "scaleX", scaleTo, scaleFrom);
        a2x.setDuration(a2During);
        ValueAnimator a2y = ObjectAnimator.ofFloat(view, "scaleY", scaleTo, scaleFrom);
        a2y.setDuration(a2During);
        AnimatorSet a2 = new AnimatorSet();
        a2.playTogether(a2x, a2y);
        a2.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(a1).before(a2);
        animatorSet.setStartDelay(getApplicationRandomDelay());
        animatorSet.addListener(new ApplicationAnimatorListener(view, id));

        listener.setAnimator(animatorSet);

        animatorSet.start();
    }



    public static int getApplicationRandomDelay() {
        return ((int) (Math.random() * 3) + 1) * 150;
    }

    public class ApplicationAnimatorListener implements Animator.AnimatorListener {

        public long id;
        private View view;

        public ApplicationAnimatorListener(View view, long id) {
            this.id = id;
            this.view = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (isEditMode
                    && (isOpenFolder || id == mApplicationAnimatorArray[1] || id == mApplicationAnimatorArray[0])) {
                animation.setStartDelay(sApplicationDelay);
                animation.start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // TODO Auto-generated method stub

        }

    }


}
