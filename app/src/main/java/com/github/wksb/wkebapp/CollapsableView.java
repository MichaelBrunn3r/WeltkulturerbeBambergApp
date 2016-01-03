package com.github.wksb.wkebapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

        setVisibility(GONE);
        setState(CollapsableViewState.INVISIBLE);
    }


    public void show(Edge slideEdge) {
        if(mState == CollapsableViewState.INVISIBLE || mState == CollapsableViewState.COLLAPSED) {
            Scene inflated = Scene.getSceneForLayout(this, mLayoutInflatedId, getContext());

            SlideTransition inflateTransition = new SlideTransition(slideEdge);
            inflateTransition.setDuration(500);

            TransitionManager.go(inflated, inflateTransition);

            setVisibility(VISIBLE);
            setState(CollapsableViewState.INFLATED);
        }
    }

    public void collapse(Edge slideEge) {
        if (mState == CollapsableViewState.INFLATED) {
            Scene collapsed = Scene.getSceneForLayout(this, mLayoutCollapsedId, getContext());

            TransitionSet collapseTransition = new TransitionSet();

            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.addTarget(this);
            SlideTransition slideTransition = new SlideTransition(slideEge);

            collapseTransition.addTransition(changeBounds);
            collapseTransition.addTransition(slideTransition);
            collapseTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);
            collapseTransition.setDuration(1000);

            TransitionManager.go(collapsed, collapseTransition);

            setState(CollapsableViewState.COLLAPSED);
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
