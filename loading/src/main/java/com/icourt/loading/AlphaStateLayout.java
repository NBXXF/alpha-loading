package com.icourt.loading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author youxuan  E-mail:xuanyouwu@163.com
 * @version 2.2.1  多状态布局
 * @Description
 * @Company Beijing icourt
 * @date createTime：2017/11/14
 */
public class AlphaStateLayout extends FrameLayout {

    public static final int VIEW_STATE_UNKNOWN = -1;//未知

    public static final int VIEW_STATE_CONTENT = 0;//内容

    public static final int VIEW_STATE_ERROR = 1;//错误

    public static final int VIEW_STATE_EMPTY = 2;//内容为空

    public static final int VIEW_STATE_LOADING = 3;//加载中..

    @Retention(RetentionPolicy.CLASS)
    @IntDef({VIEW_STATE_UNKNOWN,
            VIEW_STATE_CONTENT,
            VIEW_STATE_ERROR,
            VIEW_STATE_EMPTY,
            VIEW_STATE_LOADING})
    public @interface ViewState {
    }

    private LayoutInflater mInflater;

    private View mContentView;

    private View mLoadingView;

    private View mErrorView;

    private View mEmptyView;

    private boolean mAnimateViewChanges = false;

    @Nullable
    private StateListener mListener;

    @ViewState
    private int mViewState = VIEW_STATE_UNKNOWN;

    public AlphaStateLayout(Context context) {
        this(context, null);
    }

    public AlphaStateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AlphaStateLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mInflater = LayoutInflater.from(getContext());
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaStateView);

        int loadingViewResId = a.getResourceId(R.styleable.AlphaStateView_asv_loadingView, -1);
        if (loadingViewResId > -1) {
            mLoadingView = mInflater.inflate(loadingViewResId, this, false);
            addView(mLoadingView, mLoadingView.getLayoutParams());
        }

        int emptyViewResId = a.getResourceId(R.styleable.AlphaStateView_asv_emptyView, -1);
        if (emptyViewResId > -1) {
            mEmptyView = mInflater.inflate(emptyViewResId, this, false);
            addView(mEmptyView, mEmptyView.getLayoutParams());
        }

        int errorViewResId = a.getResourceId(R.styleable.AlphaStateView_asv_errorView, -1);
        if (errorViewResId > -1) {
            mErrorView = mInflater.inflate(errorViewResId, this, false);
            addView(mErrorView, mErrorView.getLayoutParams());
        }

        int viewState = a.getInt(R.styleable.AlphaStateView_asv_viewState, VIEW_STATE_CONTENT);
        mAnimateViewChanges = a.getBoolean(R.styleable.AlphaStateView_asv_animateViewChanges, false);

        switch (viewState) {
            case VIEW_STATE_CONTENT:
                mViewState = VIEW_STATE_CONTENT;
                break;

            case VIEW_STATE_ERROR:
                mViewState = VIEW_STATE_ERROR;
                break;

            case VIEW_STATE_EMPTY:
                mViewState = VIEW_STATE_EMPTY;
                break;

            case VIEW_STATE_LOADING:
                mViewState = VIEW_STATE_LOADING;
                break;

            case VIEW_STATE_UNKNOWN:
            default:
                mViewState = VIEW_STATE_UNKNOWN;
                break;
        }

        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mContentView == null) {
            return;
        }
        setView(VIEW_STATE_UNKNOWN);
    }

    /**
     * 检验一下
     *
     * @param child
     */
    @Override
    public void addView(View child) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        super.addView(child, width, height);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        if (isValidContentView(child)) {
            mContentView = child;
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    /**
     * 通过对应的状态获取对应的view
     *
     * @param state
     * @return
     */
    @Nullable
    public View getView(@ViewState int state) {
        switch (state) {
            case VIEW_STATE_LOADING:
                return mLoadingView;

            case VIEW_STATE_CONTENT:
                return mContentView;

            case VIEW_STATE_EMPTY:
                return mEmptyView;

            case VIEW_STATE_ERROR:
                return mErrorView;

            default:
                return null;
        }
    }

    @ViewState
    public int getViewState() {
        return mViewState;
    }

    public void setViewState(@ViewState int state) {
        if (state != mViewState) {
            int previous = mViewState;
            mViewState = state;
            setView(previous);
            if (mListener != null) {
                mListener.onStateChanged(mViewState);
            }
        }
    }

    /**
     * R.id.alpha_empty_view_tv
     *
     * @param id
     * @return
     */
    public AlphaStateLayout setEmptyText(@StringRes int id) {
        if (mEmptyView != null) {
            TextView viewById = mEmptyView.findViewById(R.id.alpha_empty_view_tv);
            if (viewById != null) {
                viewById.setText(id);
            }
        }
        return this;
    }

    /**
     * R.id.alpha_empty_view_tv
     *
     * @param id
     * @return
     */
    public AlphaStateLayout setEmptyText(@StringRes int id, Object... formatArgs) {
        if (mEmptyView != null) {
            TextView viewById = mEmptyView.findViewById(R.id.alpha_empty_view_tv);
            if (viewById != null) {
                viewById.setText(getContext().getString(id, formatArgs));
            }
        }
        return this;
    }

    /**
     * R.id.alpha_empty_view_iv
     *
     * @param id
     * @return
     */
    public AlphaStateLayout setEmptyImage(@DrawableRes int id) {
        if (mEmptyView != null) {
            ImageView viewById = mEmptyView.findViewById(R.id.alpha_empty_view_iv);
            if (viewById != null) {
                viewById.setImageResource(id);
            }
        }
        return this;
    }

    /**
     * R.id.alpha_error_view_tv
     *
     * @param charSequence
     * @return
     */
    public AlphaStateLayout setErrorText(CharSequence charSequence) {
        if (mEmptyView != null) {
            TextView viewById = mEmptyView.findViewById(R.id.alpha_error_view_tv);
            if (viewById != null) {
                viewById.setText(charSequence);
            }
        }
        return this;
    }

    /**
     * R.id.alpha_error_view_iv
     *
     * @param id
     * @return
     */
    public AlphaStateLayout setErrorImage(@DrawableRes int id) {
        if (mEmptyView != null) {
            ImageView viewById = mEmptyView.findViewById(R.id.alpha_error_view_iv);
            if (viewById != null) {
                viewById.setImageResource(id);
            }
        }
        return this;
    }

    /**
     * aR.id.alpha_error_view_retry_tv
     *
     * @param l
     * @return
     */
    public AlphaStateLayout setErrorRetryListener(@Nullable OnClickListener l) {
        if (mEmptyView != null) {
            TextView viewById = mEmptyView.findViewById(R.id.alpha_error_view_retry_tv);
            if (viewById != null) {
                viewById.setOnClickListener(l);
            }
        }
        return this;
    }

    /**
     * @param previousState
     */
    private void setView(@ViewState int previousState) {
        switch (mViewState) {
            case VIEW_STATE_LOADING:
                if (mLoadingView == null) {
                    return;
                }

                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_EMPTY:
                if (mEmptyView == null) {
                    return;
                }


                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_ERROR:
                if (mErrorView == null) {
                    return;
                }


                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mContentView != null) {
                    mContentView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mErrorView.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_CONTENT:
            default:
                if (mContentView == null) {
                    return;
                }


                if (mLoadingView != null) {
                    mLoadingView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    mErrorView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }

                if (mAnimateViewChanges) {
                    animateLayoutChange(getView(previousState));
                } else {
                    mContentView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /**
     * 判断view是否合法
     *
     * @param view
     * @return
     */
    private boolean isValidContentView(View view) {
        if (mContentView != null && mContentView != view) {
            return false;
        }
        return view != mLoadingView && view != mErrorView && view != mEmptyView;
    }

    /**
     * 设置对应状态的布局
     *
     * @param view
     * @param state
     * @param switchToState
     */
    public void setViewForState(View view, @ViewState int state, boolean switchToState) {
        switch (state) {
            case VIEW_STATE_LOADING:
                if (mLoadingView != null) {
                    removeView(mLoadingView);
                }
                mLoadingView = view;
                addView(mLoadingView);
                break;

            case VIEW_STATE_EMPTY:
                if (mEmptyView != null) {
                    removeView(mEmptyView);
                }
                mEmptyView = view;
                addView(mEmptyView);
                break;

            case VIEW_STATE_ERROR:
                if (mErrorView != null) {
                    removeView(mErrorView);
                }
                mErrorView = view;
                addView(mErrorView);
                break;

            case VIEW_STATE_CONTENT:
                if (mContentView != null) {
                    removeView(mContentView);
                }
                mContentView = view;
                addView(mContentView);
                break;
            default:
                break;
        }

        setView(VIEW_STATE_UNKNOWN);
        if (switchToState) {
            setViewState(state);
        }
    }

    /**
     * 设置对应状态的布局
     *
     * @param view
     * @param state
     */
    public void setViewForState(View view, @ViewState int state) {
        setViewForState(view, state, false);
    }

    public void setViewForState(@LayoutRes int layoutRes, @ViewState int state, boolean switchToState) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getContext());
        }
        View view = mInflater.inflate(layoutRes, this, false);
        setViewForState(view, state, switchToState);
    }

    public void setViewForState(@LayoutRes int layoutRes, @ViewState int state) {
        setViewForState(layoutRes, state, false);
    }

    /**
     * 是否支持过渡动画
     *
     * @param animate
     */
    public void setAnimateLayoutChanges(boolean animate) {
        mAnimateViewChanges = animate;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setStateListener(StateListener listener) {
        mListener = listener;
    }

    private void animateLayoutChange(@Nullable final View previousView) {
        if (previousView == null) {
            getView(mViewState).setVisibility(View.VISIBLE);
            return;
        }

        previousView.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).setDuration(250L);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                previousView.setVisibility(View.GONE);
                getView(mViewState).setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(getView(mViewState), "alpha", 0.0f, 1.0f).setDuration(250L).start();
            }
        });
        anim.start();
    }

    public interface StateListener {
        /**
         * 状态发生改变
         *
         * @param viewState
         */
        void onStateChanged(@ViewState int viewState);
    }
}
