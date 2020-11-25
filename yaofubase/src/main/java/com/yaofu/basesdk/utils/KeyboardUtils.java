package com.yaofu.basesdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.lang.ref.WeakReference;

/**
 * Created by liujing on 2017/8/24.
 */

public class KeyboardUtils {
    /**
     * 显示软键盘
     *
     * @param activity
     * @param editText
     */
    public static void showSolfInput(Activity activity, View editText) {
        if (activity == null || activity.isDestroyed() || editText == null) {
            return;
        }
        WeakReference<Activity> weakContext = new WeakReference<>(activity);
        WeakReference<View> weakReference = new WeakReference<>(editText);
        final Activity weakActivity = weakContext.get();
        final View weakView = weakReference.get();
        if (weakActivity != null && weakView != null) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    weakView.setFocusable(true);
                    weakView.setFocusableInTouchMode(true);
                    weakView.requestFocus();
                    weakView.findFocus();
                    InputMethodManager imm = (InputMethodManager)
                            weakActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(weakView, 0);
                    }
                }
            }, 400);
        }
    }

    public static void showSolfInput(View editText, long delayTime) {
        if (editText == null) {
            return;
        }
        WeakReference<View> weakReference = new WeakReference<>(editText);
        final View weakView = weakReference.get();
        if (weakView != null && weakView.getContext() != null) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    weakView.setFocusable(true);
                    weakView.setFocusableInTouchMode(true);
                    weakView.requestFocus();
                    weakView.findFocus();
                    InputMethodManager imm = (InputMethodManager)
                            weakView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(weakView, 0);
                    }
                }
            }, delayTime);
        }
    }

    public static void showSolfInput(Activity activity, View editText,boolean im) {
        if (activity == null || activity.isDestroyed() || editText == null) {
            return;
        }
        WeakReference<Activity> weakContext = new WeakReference<>(activity);
        WeakReference<View> weakReference = new WeakReference<>(editText);
        final Activity weakActivity = weakContext.get();
        final View weakView = weakReference.get();
        if (weakActivity != null && weakView != null) {
            weakView.setFocusable(true);
            weakView.setFocusableInTouchMode(true);
            weakView.requestFocus();
            weakView.findFocus();
            InputMethodManager imm = (InputMethodManager)
                    weakActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(weakView, 0);
            }
        }
    }

    public static void setFocusable(Activity activity, View editText) {
        if (activity == null || activity.isDestroyed() || editText == null) {
            return;
        }
        WeakReference<View> weakReference = new WeakReference<>(editText);
        final View weakView = weakReference.get();
        if (weakView != null) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    weakView.setFocusable(true);
                    weakView.setFocusableInTouchMode(true);
                    weakView.requestFocus();
                    weakView.findFocus();
                }
            }, 400);
        }
    }

    public static void focusableFalse(View editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.clearFocus();
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSolfInput(Activity activity) {
        if (activity == null || activity.isDestroyed()) {
            return;
        }
        WeakReference<Activity> weakContext = new WeakReference<>(activity);
        Activity weakActivity = weakContext.get();
        if (weakActivity != null) {
            View view = weakActivity.getCurrentFocus();
            if (view == null) {
                view = new View(weakActivity);
            }
            InputMethodManager imm = (InputMethodManager)
                    weakActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 如果输入法在窗口上已经显示，则隐藏，反之则显示
     */
    public static void toggleSoftInput(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        WeakReference<View> weakContext = new WeakReference<>(view);
        View weakActivity = weakContext.get();
        if (weakActivity != null) {
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 隐藏软键盘，请直接传递有焦点的view
     */
    public static void hideSolfInput(Activity activity, View view) {
        if (activity == null || activity.isDestroyed()) {
            return;
        }
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        Activity weakActivity = weakReference.get();
        if (weakActivity != null) {
            if (view == null) {
                view = weakActivity.getCurrentFocus();
            }
            if (view == null) {
                view = new View(weakActivity);
            }
            InputMethodManager imm = (InputMethodManager)
                    weakActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void keyBoardListener(Activity activity, View view) {
        KeyboardUtils keyboardUtil = new KeyboardUtils();
        keyboardUtil.hideKeyBoardListener(activity, view);
    }

    //纪录根视图的显示高度
    private int rootViewVisibleHeight;

    private void hideKeyBoardListener(final Activity activity, final View view) {
        if (activity == null || activity.isDestroyed() || view == null) {
            return;
        }
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        Activity weakActivity = weakReference.get();
        if (weakActivity != null && weakActivity.getWindow() != null) {
            final View rootView = weakActivity.getWindow().getDecorView();
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (rootView != null) {
                                //获取当前根视图在屏幕上显示的大小
                                Rect r = new Rect();
                                rootView.getWindowVisibleDisplayFrame(r);
                                int visibleHeight = r.height();
                                if (rootViewVisibleHeight == 0) {
                                    rootViewVisibleHeight = visibleHeight;
                                    return;
                                }

                                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                                if (rootViewVisibleHeight == visibleHeight) {
                                    return;
                                }

                                //根视图显示高度变小超过200，可以看作软键盘显示了
                                if (rootViewVisibleHeight - visibleHeight > 200) {
                                    rootViewVisibleHeight = visibleHeight;
                                    return;
                                }

                                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                                if (visibleHeight - rootViewVisibleHeight > 200) {
                                    setFocusable(activity, view);
                                }
                                rootViewVisibleHeight = visibleHeight;
                            }
                        }
                    });
        }
    }


}
