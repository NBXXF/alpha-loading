package com.icourt.loading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description
 * Company Beijing iCourt
 *
 * @author Junkang.Ding Email:dingjunkang@icourt.cc
 *         date createTime：2017/11/6
 *         version 2.2.1
 */
public class AlphaLoadingView extends AppCompatImageView {

    private Animatable animatable;

    public AlphaLoadingView(Context context) {
        this(context, null);
    }

    public AlphaLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.alpha_loading);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == VISIBLE) {
            if (animatable != null && !animatable.isRunning()) {
                animatable.start();
            }
        } else {
            if (animatable != null && animatable.isRunning()) {
                animatable.stop();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (animatable != null && animatable.isRunning()) {
            animatable.stop();
        }
        Drawable target = getDrawable();
        if (target != null && target instanceof Animatable) {
            animatable = (Animatable) target;
        }
        if (animatable != null && getVisibility() == VISIBLE) {
            animatable.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (animatable != null && animatable.isRunning()) {
            animatable.stop();
            animatable = null;
        }
    }

    private void checkUpdatedForNewDrawable() {
        Drawable drawable = getDrawable();
        if (drawable != animatable && drawable instanceof Animatable) {
            if (animatable != null) {
                animatable = (Animatable) drawable;
                if (animatable.isRunning()) {
                    animatable.stop();
                }
                if (getVisibility() == VISIBLE) {
                    animatable.start();
                }
            } else {
                animatable = null;
            }
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        checkUpdatedForNewDrawable();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        checkUpdatedForNewDrawable();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        checkUpdatedForNewDrawable();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        checkUpdatedForNewDrawable();
    }

    @Override
    public void setImageIcon(@Nullable Icon icon) {
        super.setImageIcon(icon);
        checkUpdatedForNewDrawable();
    }

    @Override
    public void setImageLevel(int level) {
        super.setImageLevel(level);
        checkUpdatedForNewDrawable();
    }

    /**
     * show 展示loading
     */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /**
     * 停止消失
     */
    public void dismiss() {
        setVisibility(View.GONE);
    }

    /**
     * 是否展示loading中
     *
     * @return
     */
    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

}
