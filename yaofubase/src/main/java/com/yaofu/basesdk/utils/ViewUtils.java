package com.yaofu.basesdk.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;


import java.util.List;

/**
 * Auth:long3.yang
 * Date:2020/10/21
 **/
public class ViewUtils {

    private static float measureTextWidth(String text, Context context, int textSize) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        float strWidth = (int) textPaint.measureText(text);
        return ScreenUtils.dp2px(context, strWidth);
    }

    public static  void addLabel(Context context, LinearLayout tvLabelContainer, List<String> labelList
            , float paddingValue, int textSize, int offWidth, int drawableId, int maxCnt, int colorId){
        tvLabelContainer.removeAllViews();
        if (labelList== null || labelList.size() <=0 ){
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, ScreenUtils.dp2px(context, paddingValue), 0);
        int screenWidth = ScreenUtils.getScreenWidth(context);
        int remainWidth  = (int) (screenWidth - ScreenUtils.dp2px(context, offWidth));

        int displayCnt = 0;
        for (String label: labelList){
            if (displayCnt < maxCnt) {
                float measureWidth = measureTextWidth(label, context, textSize) + 2*ScreenUtils.dp2px(context, (int) paddingValue);
                if (remainWidth > measureWidth) {
                    TextView tv = new TextView(context);
                    tv.setTextSize(textSize);
                    tv.setTextColor(ContextCompat.getColor(context,colorId));
                    tv.setSingleLine(true);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackground(ContextCompat.getDrawable(context, drawableId));
                    tv.setPadding(ScreenUtils.dp2px(context, (int) paddingValue), 0, ScreenUtils.dp2px(context, (int) paddingValue), 0);
                    tv.setText(label);
                    tvLabelContainer.addView(tv, layoutParams);
                    remainWidth -= measureWidth;
                    displayCnt += 1;
                }
            }
        }
    }


    /**
     * ViewHolder简洁写法,避免适配器中重复定义ViewHolder,减少代码量 用法:
     *
     * <pre>
     * if (convertView == null)
     * {
     * 	convertView = View.inflate(context, R.layout.ad_demo, null);
     * }
     * TextView tv_demo = ViewHolderUtils.get(convertView, R.id.tv_demo);
     * ImageView iv_demo = ViewHolderUtils.get(convertView, R.id.iv_demo);
     * </pre>
     */
    public static <T extends View> T hold(View view, int id)
    {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();

        if (viewHolder == null)
        {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }

        View childView = viewHolder.get(id);

        if (childView == null)
        {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;
    }

    /**
     * 替代findviewById方法
     */
    public static <T extends View> T find(View view, int id)
    {
        return (T) view.findViewById(id);
    }


    /**
     * 测量文字高度
     * @param paint
     * @return
     */
    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
    }

    /**
     * 测量 View
     *
     * @param measureSpec
     * @param defaultSize View 的默认大小
     * @return
     */
    public static int measure(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    public static GradientDrawable generateDrawable(Context context, @ColorRes int solid, @ColorRes int strokeColor, int strokeWidth, float corner) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(solid));
        drawable.setStroke(strokeWidth, context.getResources().getColor(strokeColor));
        drawable.setCornerRadius(corner);
        return drawable;
    }

    public static GradientDrawable generateDrawable(Context context, @ColorRes int solid, @ColorRes int strokeColor, int strokeWidth,
                                                    float[] corner) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(solid));
        drawable.setStroke(strokeWidth, context.getResources().getColor(strokeColor));
        drawable.setCornerRadii(corner);
        return drawable;
    }

    public static GradientDrawable generateDrawable(Context context, @ColorRes int solid,
                                                    float[] corner) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(solid));
        drawable.setCornerRadii(corner);
        return drawable;
    }

    public static GradientDrawable generateDrawable(Context context, @ColorRes int solid, float corner) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(solid));
        drawable.setCornerRadius(corner);
        return drawable;
    }

    public static GradientDrawable generateDrawable(Context context, int radius, int strokeWidth,
                                                    int strokeColor, @ColorRes int solidColor, float alpha) {

        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            bgDrawable.setStroke(strokeWidth, context.getResources().getColor(strokeColor));
        }
        bgDrawable.setColor(context.getResources().getColor(solidColor) & (((int) (alpha * 0xff) << 24) | 0xffffff));
        return bgDrawable;
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    public static void setPaddingSmart(Context context, View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height += ScreenUtils.getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() +ScreenUtils.getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void calcProgressToView(ProgressBar progressBar, long offset, long total) {
        final float percent = (float) offset / total;
        progressBar.setProgress((int) (percent * progressBar.getMax()));
    }


}
