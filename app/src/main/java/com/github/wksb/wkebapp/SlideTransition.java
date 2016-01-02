package com.github.wksb.wkebapp;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Michael on 29.11.2015.
 */
public class SlideTransition extends Visibility {

    private Edge mSlideEdge;

    private static final int SLIDE_OUT_DURATION = 400;
    private static final int SLIDE_IN_DURATION = 400;

    public SlideTransition(Edge slideEdge) {
        mSlideEdge = slideEdge;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        int screenHeight = view.getContext().getResources().getDisplayMetrics().heightPixels;
        int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;

        ObjectAnimator animator = null;

        if (mSlideEdge == Edge.BOTTOM) {
            animator = ObjectAnimator.ofFloat(view, View.Y, view.getY());
            view.setY(sceneRoot.getBottom());
        } else if (mSlideEdge == Edge.TOP) {
            animator = ObjectAnimator.ofFloat(view, View.Y, view.getY());
            view.setY(sceneRoot.getY()-view.getHeight());
        } else if (mSlideEdge == Edge.LEFT) {
            animator = ObjectAnimator.ofFloat(view, View.X, view.getX());
            view.setX(0-view.getWidth());
        } else if (mSlideEdge == Edge.RIGHT) {
            animator = ObjectAnimator.ofFloat(view, View.X, view.getX());
            view.setX(screenWidth);
        }

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(SLIDE_IN_DURATION);
        return animator;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        int[] locationOnScree = {0,0};
        view.getLocationOnScreen(locationOnScree);

        int screenHeight = view.getContext().getResources().getDisplayMetrics().heightPixels;
        int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;

        ObjectAnimator animator = null;

        if (mSlideEdge == Edge.BOTTOM) {
            animator = ObjectAnimator.ofFloat(view, View.Y, sceneRoot.getBottom());
        } else if (mSlideEdge == Edge.TOP) {
            animator = ObjectAnimator.ofFloat(view, View.Y, sceneRoot.getY()-view.getHeight());
        } else if (mSlideEdge == Edge.LEFT) {
            animator = ObjectAnimator.ofFloat(view, View.X, -view.getWidth());
        } else if (mSlideEdge == Edge.RIGHT) {
            animator = ObjectAnimator.ofFloat(view, View.X, screenWidth);
        }

        animator.setDuration(SLIDE_OUT_DURATION);
        return animator;
    }
}