package com.github.gfranks.expandable.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListView extends ListView implements ExpandableView.ExpandableViewListener {

    private boolean mKeepViewsExpanded;
    private List<Integer> mExpandedViews;
    private ExpandableView.ExpandableViewListener mListener;

    public ExpandableListView(Context context) {
        super(context);
        mExpandedViews = new ArrayList<Integer>();
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mExpandedViews = new ArrayList<Integer>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableListView, defStyleAttr, 0);
        mKeepViewsExpanded = a.getBoolean(R.styleable.ExpandableListView_ev_keepViewsExpanded, false);
        a.recycle();
    }

    /**
     * Listener for ExpandableView callbacks. This allows the container to still receive these callbacks without breaking functionality
     * @param listener the ExpandableViewListener to receive callbacks
     */
    public void setExpandableViewListener(ExpandableView.ExpandableViewListener listener) {
        mListener = listener;
    }

    /**
     *
     * @return boolean determining if multiple views may be expanded or just a single one
     * @see #setKeepViewsExpanded(boolean)
     */
    public boolean isKeepViewsExpanded() {
        return mKeepViewsExpanded;
    }

    /**
     *
     * @param keepViewsExpanded boolean setting if multiple views may be expanded or just a single one
     * true for multiple, false for single
     */
    public void setKeepViewsExpanded(boolean keepViewsExpanded) {
        mKeepViewsExpanded = keepViewsExpanded;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mExpandedViews = mExpandedViews;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mExpandedViews = ss.mExpandedViews;
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        boolean drawChild = super.drawChild(canvas, child, drawingTime);

        ExpandableView expandableView;
        if (child instanceof ExpandableView) {
            expandableView = (ExpandableView) child;
        } else {
            expandableView = (ExpandableView) child.findViewWithTag(ExpandableView.class.getName());
        }

        if (expandableView != null) {
            final int position = getPositionForView(child);
            expandableView.setId(position);

            if (mExpandedViews.contains(position)) {
                expandableView.setExpanded(true, false);
            } else {
                expandableView.setExpanded(false, false);
            }

            expandableView.setExpandableViewListener(this);
        }

        return drawChild;
    }

    /**
     * *************************************
     * ExpandableView.ExpandableViewListener
     * *************************************
     */
    @Override
    public boolean canExpand(ExpandableView expandableView) {
        if (mListener != null) {
            return mListener.canExpand(expandableView);
        }

        return true;
    }

    @Override
    public boolean canCollapse(ExpandableView expandableView) {
        if (mListener != null) {
            return mListener.canCollapse(expandableView);
        }

        return true;
    }

    @Override
    public void willExpand(ExpandableView expandableView) {
        mExpandedViews.add(expandableView.getId());
        if (mListener != null) {
            mListener.willExpand(expandableView);
        }
        if (isKeepViewsExpanded()) {
            return;
        }

        final int currentPosition = expandableView.getId();
        for (int i=getFirstVisiblePosition(); i<=getLastVisiblePosition(); i++) {
            View child = getChildAt(i);
            if (child instanceof ExpandableView) {
                expandableView = (ExpandableView) child;
            } else {
                expandableView = (ExpandableView) child.findViewWithTag(ExpandableView.class.getName());
            }
            if (expandableView != null && i != currentPosition) {
                expandableView.collapseContent();
            }
        }
    }

    @Override
    public void willCollapse(ExpandableView expandableView) {
        if (mListener != null) {
            mListener.willCollapse(expandableView);
        }
    }

    @Override
    public void didExpand(ExpandableView expandableView) {
        if (mListener != null) {
            mListener.didExpand(expandableView);
        }
    }

    @Override
    public void didCollapse(ExpandableView expandableView) {
        mExpandedViews.remove((Integer) expandableView.getId());
        if (mListener != null) {
            mListener.didCollapse(expandableView);
        }
    }

    @Override
    public void onHeightOffsetChanged(ExpandableView expandableView, float offset) {
        if (mListener != null) {
            mListener.onHeightOffsetChanged(expandableView, offset);
        }
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

        List<Integer> mExpandedViews;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpandedViews = in.readArrayList(ArrayList.class.getClassLoader());
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(mExpandedViews);
        }
    }
}
