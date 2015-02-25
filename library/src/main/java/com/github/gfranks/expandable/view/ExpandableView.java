package com.github.gfranks.expandable.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ExpandableView extends LinearLayout implements View.OnClickListener {

    private static final int DEFAULT_ANIMATION_DURATION = 200;

    /**
     * boolean tracking expansion and collapse
     */
    private boolean mIsCollapsed;
    /**
     * boolean tracking if content click events should trigger expansion or collapse
     */
    private boolean mCollapseOnContentClick;
    /**
     * boolean tracking if any click events should trigger expansion or collapse
     */
    private boolean mDisableExpandCollapseOnClick;
    /**
     * boolean determining if gradient overlay should be added over content when collapsed
     * (NOTE: this will only be applied if the collapsed content height is greater than 0)
     */
    private boolean mAddGradientOverlayWhenCollapsed;
    /**
     * Color of the gradient overlay (starts with Color.TRANSPARENT)
     */
    private int mGradientOverlayColor;
    /**
     * The view that holds the gradient overlay
     */
    private View mGradientOverlay;
    /**
     * duration for expansion and collapse animation
     */
    private long mAnimationDuration;
    /**
     * height of the content view when collapsed
     */
    private int mCollapsedContentHeight;
    /**
     * Header View
     */
    private View mHeaderView;
    /**
     * Content View (view to be expanded or collapsed)
     */
    private View mContentView;
    /**
     * Footer View
     */
    private View mFooterView;
    /**
     * Listener for ExpandableView expand/collapse callbacks
     */
    private ExpandableViewListener mListener;
    private boolean mIsInflated;
    private Animator.AnimatorListener mExpandAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            getContentView().setVisibility(View.VISIBLE);
            if (mListener != null) {
                mListener.willExpand(ExpandableView.this);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mIsCollapsed = false;
            if (mListener != null) {
                mListener.didExpand(ExpandableView.this);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };
    private Animator.AnimatorListener mCollapseAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (mListener != null) {
                mListener.willCollapse(ExpandableView.this);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mCollapsedContentHeight <= 0) {
                getContentView().setVisibility(View.GONE);
            }
            mIsCollapsed = true;
            if (mListener != null) {
                mListener.didCollapse(ExpandableView.this);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public ExpandableView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setClipChildren(true);
        setClipToPadding(true);
        mAnimationDuration = DEFAULT_ANIMATION_DURATION;
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setClipChildren(true);
        setClipToPadding(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView, defStyleAttr, 0);
        mIsCollapsed = a.getBoolean(R.styleable.ExpandableView_isCollapsed, false);
        mCollapseOnContentClick = a.getBoolean(R.styleable.ExpandableView_collapseOnContentClick, false);
        mAnimationDuration = a.getInt(R.styleable.ExpandableView_animationDuration, DEFAULT_ANIMATION_DURATION);
        int headerLayoutResId = a.getResourceId(R.styleable.ExpandableView_headerLayout, -1);
        int contentLayoutResId = a.getResourceId(R.styleable.ExpandableView_contentLayout, -1);
        int footerLayoutResId = a.getResourceId(R.styleable.ExpandableView_footerLayout, -1);
        mDisableExpandCollapseOnClick = a.getBoolean(R.styleable.ExpandableView_disableExpandCollapseOnClick, false);
        mCollapsedContentHeight = a.getDimensionPixelSize(R.styleable.ExpandableView_collapsedContentHeight, 0);
        mAddGradientOverlayWhenCollapsed = a.getBoolean(R.styleable.ExpandableView_addGradientOverlayWhenCollapsed, false);
        mGradientOverlayColor = a.getColor(R.styleable.ExpandableView_gradientOverlayColor, Color.WHITE);
        a.recycle();

        if (headerLayoutResId != -1) {
            setHeaderView(headerLayoutResId);
        }

        if (contentLayoutResId != -1) {
            setContentView(contentLayoutResId);
        }

        if (footerLayoutResId != -1) {
            setFooterView(footerLayoutResId);
        }
    }

    /**
     * @param listener ExpandableViewListener that will receive callbacks for expanding and collapsing
     */
    public void setExpandableViewListener(ExpandableViewListener listener) {
        mListener = listener;
    }

    /**
     * @return boolean determining if content view is expanded
     */
    public boolean isExpanded() {
        return !mIsCollapsed;
    }

    /**
     * This method should be called when first inflating the view, if you would like to expand or collapse the content by default
     *
     * @param expanded boolean determining if content should be expanded
     * @param animate  boolean determining if collapse or expansion should be animated
     * @see #expandContent()
     * @see #collapseContent()
     */
    public void setExpanded(boolean expanded, boolean animate) {
        if (animate) {
            if (expanded) {
                expandContent();
            } else {
                collapseContent();
            }
            return;
        }

        mIsCollapsed = !expanded;
        if (expanded) {
            getContentView().getLayoutParams().height = LayoutParams.WRAP_CONTENT;
            getContentView().setVisibility(View.VISIBLE);
            if (mAddGradientOverlayWhenCollapsed && mGradientOverlay != null) {
                mGradientOverlay.setAlpha(0f);
            }
        } else {
            getContentView().getLayoutParams().height = mCollapsedContentHeight;
            if (mCollapsedContentHeight <= 0) {
                getContentView().setVisibility(View.GONE);
            } else if (mAddGradientOverlayWhenCollapsed && mGradientOverlay != null) {
                mGradientOverlay.setAlpha(1f);
            }
        }
        getContentView().requestLayout();
    }

    /**
     * @return the set animation duration
     * @see #setAnimationDuration(long)
     */
    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    /**
     * @param animationDuration long determining the duration the animation should set when expanding or collapsing
     */
    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    /**
     * @return boolean determining if click events should trigger expansion or collapse
     * @see #setDisableExpandCollapseOnClick(boolean)
     */
    public boolean isDisableExpandCollapseOnClick() {
        return mDisableExpandCollapseOnClick;
    }

    /**
     * Set to disable expansion or collapse when clicking the header, content, or footer
     *
     * @param disableExpandCollapseOnClick boolean determining if click events should trigger expansion or collapse
     */
    public void setDisableExpandCollapseOnClick(boolean disableExpandCollapseOnClick) {
        mDisableExpandCollapseOnClick = disableExpandCollapseOnClick;
    }

    /**
     * @return int the height of the content view when collapsed
     * @see #setCollapsedContentHeight(int)
     */
    public int getCollapsedContentHeight() {
        return mCollapsedContentHeight;
    }

    /**
     * If the height passed exceeds the height of the content view when expanded, the view will not collapse.
     *
     * @param collapsedContentHeight The height (in px) of the content view when collapsed. May be 0
     */
    public void setCollapsedContentHeight(int collapsedContentHeight) {
        mCollapsedContentHeight = collapsedContentHeight;
    }

    /**
     * @return boolean if a gradient is overlayed on top of the content view
     * @see #setAddGradientOverlayWhenCollapsed(boolean)
     */
    public boolean isAddGradientOverlayWhenCollapsed() {
        return mAddGradientOverlayWhenCollapsed;
    }

    /**
     * @param addGradientOverlayWhenCollapsed boolean determining if a gradient overlay should be added on top
     *                                        of the content view when collapsed. (NOTE: This will only be applied if the collapsed content height is greater than 0)
     */
    public void setAddGradientOverlayWhenCollapsed(boolean addGradientOverlayWhenCollapsed) {
        if (mAddGradientOverlayWhenCollapsed == addGradientOverlayWhenCollapsed) {
            return;
        }

        mAddGradientOverlayWhenCollapsed = addGradientOverlayWhenCollapsed;
        if (mIsInflated) {
            if (mAddGradientOverlayWhenCollapsed) {
                ensureGradientOverlayAdded();
            } else {
                ensureGradientOverlayRemoved();
            }
        }
    }

    /**
     * @return bottom color of gradient overlay
     * @see #setGradientOverlayColor(int)
     */
    public int getGradientOverlayColor() {
        return mGradientOverlayColor;
    }

    /**
     * NOTE: Please set before you call
     *
     * @param gradientOverlayColor color to be used to set the gradient overlay. Color will start as transparent
     *                             and gradient applied to match this color
     * @see #setAddGradientOverlayWhenCollapsed(boolean) or proper gradient color may not be applied
     */
    public void setGradientOverlayColor(int gradientOverlayColor) {
        mGradientOverlayColor = gradientOverlayColor;
    }

    /**
     * @param headerLayoutResId layout resource id to be inflated as the header view
     * @see #setHeaderView(android.view.View)
     */
    public void setHeaderView(int headerLayoutResId) {
        setHeaderView(inflate(getContext(), headerLayoutResId, null));
    }

    /**
     * @return the header view
     * @see #setHeaderView(int)
     * @see #setHeaderView(android.view.View)
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * @param headerView header view to be added to the ExpandableView
     */
    public void setHeaderView(View headerView) {
        if (getFooterView() != null) {
            removeView(getFooterView());
        }
        addView(headerView, 0);
        mHeaderView = headerView;
        mHeaderView.setOnClickListener(mDisableExpandCollapseOnClick ? null : this);
    }

    /**
     * @param contentLayoutResId layout resource id to be inflated as the content view
     * @see #setContentView(android.view.View)
     */
    public void setContentView(int contentLayoutResId) {
        setContentView(inflate(getContext(), contentLayoutResId, null));
    }

    /**
     * @return the content view
     * @see #setContentView(int)
     * @see #setContentView(android.view.View)
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * @param contentView content view to be added to the ExpandableView
     * @throws java.lang.IllegalStateException if header has not been added
     */
    public void setContentView(View contentView) {
        ensureHeaderView();

        if (getContentView() != null) {
            removeView(getContentView());
        }
        addView(contentView, 1);
        mContentView = contentView;
        mContentView.setOnClickListener(this);

        if (mIsInflated && mAddGradientOverlayWhenCollapsed) {
            ensureGradientOverlayAdded();
        }
    }

    /**
     * Setting a footer view is not required
     *
     * @param footerLayoutResId layout resource id to be inflated as the footer view
     * @see #setFooterView(android.view.View)
     */
    public void setFooterView(int footerLayoutResId) {
        setFooterView(inflate(getContext(), footerLayoutResId, null));
    }

    /**
     * @return the footer view
     * @see #setFooterView(int)
     * @see #setFooterView(android.view.View)
     */
    public View getFooterView() {
        return mFooterView;
    }

    /**
     * @param footerView footer view to be added to the ExpandableView
     */
    public void setFooterView(View footerView) {
        if (getFooterView() != null) {
            removeView(getFooterView());
        }
        addView(footerView, getChildCount());
        mFooterView = footerView;
        mFooterView.setOnClickListener(this);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mIsCollapsed = mIsCollapsed;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setExpanded(!ss.mIsCollapsed, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v("GFGFGF", "Measured Height: " + getContentView().getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 3) {
            throw new IllegalStateException("ExpandableView may only have 3 children (header + content + footer)");
        }

        mIsInflated = true;
        if (getChildCount() > 0) {
            if (getHeaderView() == null) {
                mHeaderView = getChildAt(0);
                mHeaderView.setOnClickListener(this);
            }

            if (getChildCount() > 1) {
                if (getContentView() == null) {
                    mContentView = getChildAt(1);
                    mContentView.setOnClickListener(this);
                }


                if (getChildCount() > 2) {
                    if (getFooterView() == null) {
                        mFooterView = getChildAt(2);
                        mFooterView.setOnClickListener(this);
                    }
                }
            }
        }

        if (mAddGradientOverlayWhenCollapsed) {
            ensureGradientOverlayAdded();
        }

        if (!isExpanded()) {
            setExpanded(false, false);
        }
    }

    @Override
    public void onClick(View v) {
        if (mDisableExpandCollapseOnClick) {
            return;
        }

        if (v == getHeaderView() || v == getFooterView() || (v == getContentView() && mCollapseOnContentClick)) {
            if (isExpanded()) {
                collapseContent();
            } else {
                expandContent();
            }
        }
    }

    /**
     * Expands the content view
     */
    public void expandContent() {
        if (isExpanded() || (mListener != null && !mListener.canExpand(this))) {
            return;
        }

        getContentView().measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        final int fromHeight = getContentView().getHeight();
        final int toHeight = getContentView().getMeasuredHeight();

        Animator animator = getHeightAnimator(fromHeight, toHeight);
        animator.addListener(mExpandAnimationListener);
        if (mAddGradientOverlayWhenCollapsed && mGradientOverlay != null) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator, getGradientOverlayAlphaAnimator(1f, 0f));
            set.start();
        } else {
            animator.start();
        }
    }

    /**
     * Collapses the content view
     */
    public void collapseContent() {
        if (!isExpanded() || (mListener != null && !mListener.canCollapse(this))) {
            return;
        }

        final int fromHeight = getContentView().getMeasuredHeight();
        final int toHeight = mCollapsedContentHeight;

        if (toHeight >= fromHeight) {
            return;
        }

        Animator animator = getHeightAnimator(fromHeight, toHeight);
        animator.addListener(mCollapseAnimationListener);
        if (mAddGradientOverlayWhenCollapsed && mGradientOverlay != null) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator, getGradientOverlayAlphaAnimator(0f, 1f));
            set.start();
        } else {
            animator.start();
        }
    }

    private void ensureHeaderView() {
        if (getHeaderView() == null || getChildCount() == 0) {
            throw new IllegalStateException("Header view must be present before setting content");
        }
    }

    private void ensureGradientOverlayAdded() {
        if (getContentView() == null || mCollapsedContentHeight == 0) {
            Log.w(getClass().getName(), "Inflation may be in progress but -> Gradient overlay is " +
                    "only supported if you provide a collapsed view height greater than 0");
            return;
        }

        removeView(getContentView());
        FrameLayout overlayContainer = new FrameLayout(getContext());
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.TRANSPARENT, mGradientOverlayColor});
        gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
        overlayContainer.addView(getContentView());
        mGradientOverlay = new ImageView(getContext());
        ((ImageView) mGradientOverlay).setImageDrawable(gradientDrawable);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mCollapsedContentHeight);
        lp.gravity = Gravity.BOTTOM;
        mGradientOverlay.setLayoutParams(lp);
        overlayContainer.addView(mGradientOverlay);
        if (isExpanded()) {
            mGradientOverlay.setAlpha(0f);
        }
        addView(overlayContainer, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void ensureGradientOverlayRemoved() {
        try {
            ((FrameLayout) getChildAt(1)).removeView(getContentView());
            removeViewAt(1);
            addView(getContentView(), 1);
        } catch (Throwable t) {
            Log.w(getClass().getName(), "Error occurred when attempting to remove gradient overlay -> " + t.getMessage());
        }
    }

    private Animator getHeightAnimator(int fromHeight, int toHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.setDuration(mAnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getContentView().getLayoutParams().height = (int) animation.getAnimatedValue();
                getContentView().requestLayout();
                if (mListener != null) {
                    mListener.onHeightOffsetChanged(ExpandableView.this, animation.getAnimatedFraction());
                }
            }
        });
        return animator;
    }

    private Animator getGradientOverlayAlphaAnimator(float fromAlpha, float toAlpha) {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(fromAlpha, toAlpha);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mGradientOverlay.setAlpha((float) animation.getAnimatedValue());
            }
        });
        return alphaAnimator;
    }

    public interface ExpandableViewListener {
        /**
         * @param expandableView The ExpandableView object requesting
         * @return boolean determining if content may be expanded
         */
        boolean canExpand(ExpandableView expandableView);

        /**
         * @param expandableView The ExpandableView object requesting
         * @return boolean determining if content may be collapsed
         */
        boolean canCollapse(ExpandableView expandableView);

        /**
         * Callback for when the ExpandableView's content view will expand
         *
         * @param expandableView The ExpandableView object requesting
         */
        void willExpand(ExpandableView expandableView);

        /**
         * Callback for when the ExpandableView's content view will collapse
         *
         * @param expandableView The ExpandableView object requesting
         */
        void willCollapse(ExpandableView expandableView);

        /**
         * Callback for when the ExpandableView's content view did collapse
         *
         * @param expandableView The ExpandableView object requesting
         */
        void didExpand(ExpandableView expandableView);

        /**
         * Callback for when the ExpandableView's content view did collapse
         *
         * @param expandableView The ExpandableView object requesting
         */
        void didCollapse(ExpandableView expandableView);

        /**
         * For use if you are animating a drawable as the height offset is changed
         *
         * @param offset offset of the current collapse/expand animation
         */
        void onHeightOffsetChanged(ExpandableView expandableView, float offset);
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        boolean mIsCollapsed;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mIsCollapsed = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mIsCollapsed ? 1 : 0);
        }
    }
}