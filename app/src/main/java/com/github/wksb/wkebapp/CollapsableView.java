package com.github.wksb.wkebapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Michael on 30.12.2015.
 */
public class CollapsableView extends LinearLayout {

    private CollapsableViewState mState;
    private int mLayoutInflatedId;
    private int mLayoutCollapsedId;

    public CollapsableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributesArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CollapsableView,
                0, 0);

        try {
            mLayoutInflatedId = attributesArray.getResourceId(R.styleable.CollapsableView_layoutInflated, -1);
            mLayoutCollapsedId = attributesArray.getResourceId(R.styleable.CollapsableView_layoutCollapsed, -1);
        } finally {
            attributesArray.recycle();
        }

        setVisibility(View.INVISIBLE);
        setState(CollapsableViewState.INVISIBLE);

        removeAllViews();
        addView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mLayoutInflatedId, null));
        requestLayout();
    }


    public void show(Edge slideEdge) {
        if (mState == CollapsableViewState.INVISIBLE) {
            removeAllViews();
            addView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mLayoutInflatedId, null));
            requestLayout();

            setVisibility(View.VISIBLE);
            setState(CollapsableViewState.INFLATED);

            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;

            ObjectAnimator animator = null;

            if (slideEdge == Edge.TOP) {
                animator = ObjectAnimator.ofFloat(this, View.Y, getY());
                setY(-getHeight());
            } else if (slideEdge == Edge.BOTTOM) {
                animator = ObjectAnimator.ofFloat(this, View.Y, getY());
                setY(screenHeight);
            } else if (slideEdge == Edge.LEFT) {
                animator = ObjectAnimator.ofFloat(this, View.X, getX());
                setX(-getWidth());
            } else if (slideEdge == Edge.RIGHT) {
                animator = ObjectAnimator.ofFloat(this, View.X, getX());
                setX(screenWidth);
            }

            animator.start();
        }
    }

    public void collapse() {
        if (mState == CollapsableViewState.INFLATED) {
            Scene collapsed = Scene.getSceneForLayout(this, mLayoutCollapsedId, getContext());

            TransitionManager.go(collapsed, new SlideTransition(Edge.BOTTOM));
        }
    }

    public void setState(CollapsableViewState newState) {
        mState = newState;
    }

    public enum CollapsableViewState {
        INVISIBLE,
        INFLATED,
        COLLAPSED
    }
}
