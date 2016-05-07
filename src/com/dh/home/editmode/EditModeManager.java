package com.dh.home.editmode;

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

    public static int getApplicationRandomDelay() {
        return ((int) (Math.random() * 3) + 1) * 150;
    }

    public final static int sApplicationDelay = 400;



}
