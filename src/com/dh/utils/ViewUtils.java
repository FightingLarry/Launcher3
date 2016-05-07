package com.dh.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * v4.0
 */
public class ViewUtils {

    private static Point mRealSize;

    public static float dpToPx(Context context, int dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    public static float spToPx(Context context, int spValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
    }

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getDimenPx(Context context, int ResId) {
        return context.getResources().getDimensionPixelSize(ResId);
    }

    public static int getScreenHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static boolean isHdpi(Context context) {
        if (getScreenDensity(context) == 240) {
            return true;
        }

        return false;
    }

    public static boolean isLdpi(Context context) {
        if (getScreenDensity(context) == 120) {
            return true;
        }

        return false;
    }

    public static boolean isMdpi(Context context) {
        if (getScreenDensity(context) == 160) {
            return true;
        }

        return false;
    }

    public static boolean isXhdpi(Context context) {
        if (getScreenDensity(context) == 320) {
            return true;
        }

        return false;
    }

    public static void setListViewHeightBasedOnChildren(Context context, ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int screenHeight = getScreenHeightPixels(context);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight() + screenHeight;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static boolean isPointInView(int rawX, int rawY, View view) {
        if (view == null) {
            return false;
        }

        int coordinates[] = {0, 0};
        view.getLocationOnScreen(coordinates);
        int left = coordinates[0];
        int right = left + view.getWidth();
        int top = coordinates[1];
        int bottom = top + view.getHeight();
        if (rawX >= left && rawX <= right && rawY >= top && rawY <= bottom) {
            return true;
        } else {
            return false;
        }
    }

    public static Bitmap drawViewToCanvas(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static int getTitleBarHeight(Activity activity) {
        if (activity == null) {
            return 0;
        } else {
            int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight = contentTop - getStatusBarHeight(activity);
            return titleBarHeight;
        }
    }

    public static int getStatusBarHeight(Activity activity) {
        if (activity == null) {
            return 0;
        } else {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            return statusBarHeight;
        }
    }



    public static void setTextViewValue(Activity context, int txtId, String content) {
        if (context != null) {
            TextView textView = ((TextView) context.findViewById(txtId));
            if (textView != null) {
                textView.setText(content);
            }
        }
    }

    public static void setTextViewValue(View container, int txtId, String content) {
        if (container != null) {
            TextView textview = ((TextView) container.findViewById(txtId));
            if (textview != null) {
                textview.setText(content);
            }
        }
    }

    public static void setTextViewValue(Activity context, View container, int txtId, int contentId) {
        if (context != null) {
            TextView textView = (TextView) container.findViewById(txtId);
            if (textView != null) {
                textView.setText(context.getString(contentId));
            }
        }
    }

    public static void setImgResource(Activity context, int imgId, int sourceId) {
        if (context != null) {
            ImageView imageView = (ImageView) context.findViewById(imgId);
            if (imageView != null) {
                imageView.setImageResource(sourceId);
            }
        }
    }

    public static void setImgResource(Activity context, int imgId, Bitmap source) {
        if (context != null) {
            ImageView imageView = (ImageView) context.findViewById(imgId);
            if (imageView != null) {
                imageView.setImageBitmap(source);
            }
        }
    }



    public static <T> ArrayList<T[]> transToArray(int pagerSize, List<T> datas) {

        ArrayList<T[]> productList = new ArrayList<T[]>();

        int count = datas.size() / pagerSize + ((datas.size() % pagerSize) == 0 ? 0 : 1);
        for (int i = 0; i < count; i++) {
            Object[] ps = new Object[pagerSize];
            for (int j = 0; j < pagerSize; j++) {
                int dataPosition = i * pagerSize + j;
                if (dataPosition < datas.size()) {
                    ps[j] = datas.get(dataPosition);
                }
            }
            productList.add((T[]) ps);
        }

        return productList;
    }



    /**
     * 解决toast重复的问题
     *
     * Android中的Toast是非常好用的一个信息提示控件， 但是系统自带的Toast多次点击之后，会出现多次， 如显示时间是2S，点击5次之后就会显示5X2=10S。
     *
     * @param context
     * @param s
     */
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

}
