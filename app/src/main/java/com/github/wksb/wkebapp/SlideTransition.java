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

    private static final int SLIDE_OUT_DURATION_DEFAULT = 400;
    private static final int SLIDE_IN_DURATION_DEFAULT = 400;

    public SlideTransition(Edge slideEdge) {
        mSlideEdge = slideEdge;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
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
            view.setX(sceneRoot.getRight());
        }

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setStartDelay(getDuration() < 0 ? SLIDE_IN_DURATION_DEFAULT : getDuration() / 2);
        animator.setDuration(getDuration() < 0 ? SLIDE_IN_DURATION_DEFAULT : getDuration()/2);
        return animator;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        ObjectAnimator animator = null;

        if (mSlideEdge == Edge.BOTTOM) {
            animator = ObjectAnimator.ofFloat(view, View.Y, sceneRoot.getBottom());
        } else if (mSlideEdge == Edge.TOP) {
            animator = ObjectAnimator.ofFloat(view, View.Y, sceneRoot.getY()-view.getHeight());
        } else if (mSlideEdge == Edge.LEFT) {
            animator = ObjectAnimator.ofFloat(view, View.X, -view.getWidth());
        } else if (mSlideEdge == Edge.RIGHT) {
            animator = ObjectAnimator.ofFloat(view, View.X, sceneRoot.getRight());
        }
        
        animator.setDuration(getDuration() < 0 ? SLIDE_OUT_DURATION_DEFAULT : getDuration()/2);
        return animator;
    }
}