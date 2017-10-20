package com.iflytek.drip.magicindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.List;


/**
 *  offer to user simply using
 *
 * Created by xyqian2 on 2017/10/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentsHelper {
    private IMagicIndicator magicIndicator;
    private int mContainerViewId;
    private FragmentManager fragmentManager;
    private List<Fragment> fragments;
    private ValueAnimator mScrollAnimator;
    private int mLastSelectedIndex;
    private int mDuration = 150;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_IDLE);
            mScrollAnimator = null;
        }
    };

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float positionOffsetSum = (Float) animation.getAnimatedValue();
            int position = (int) positionOffsetSum;
            float positionOffset = positionOffsetSum - position;
            if (positionOffsetSum < 0) {
                position = position - 1;
                positionOffset = 1.0f + positionOffset;
            }
            dispatchPageScrolled(position, positionOffset, 0);
        }
    };

    public FragmentsHelper(final IMagicIndicator magicIndicator, int mContainerViewId, FragmentManager fragmentManager, List<Fragment> fragments) {
        this.magicIndicator = magicIndicator;
        this.mContainerViewId = mContainerViewId;
        this.fragmentManager = fragmentManager;
        this.fragments = fragments;
        initFragments();
    }
    public void setCurrentItem(int selectedIndex, boolean smooth) {
        handlePageSelected(selectedIndex, smooth);
        setFragments(selectedIndex);
    }
    public void setDuration(int duration) {
        mDuration = duration;
    }
    public void setInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        } else {
            mInterpolator = interpolator;
        }
    }
    private void initFragments() {
        for (Fragment fragment : fragments) {
            fragmentManager.beginTransaction().add(mContainerViewId, fragment).commit();
        }
        setCurrentItem(0, false);
    }
    private void setFragments(int selectedIndex) {
        for (int i = 0; i < fragments.size(); i++) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment fragment = fragments.get(i);
            if (i == selectedIndex) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
    }
    private void handlePageSelected(int selectedIndex, boolean smooth) {
        if (mLastSelectedIndex == selectedIndex) {
            return;
        }
        if (smooth) {
            if (mScrollAnimator == null || !mScrollAnimator.isRunning()) {
                dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_SETTLING);
            }
            dispatchPageSelected(selectedIndex);
            float currentPositionOffsetSum = mLastSelectedIndex;
            if (mScrollAnimator != null) {
                currentPositionOffsetSum = (Float) mScrollAnimator.getAnimatedValue();
                mScrollAnimator.cancel();
                mScrollAnimator = null;
            }
            mScrollAnimator = new ValueAnimator();
            mScrollAnimator.setFloatValues(currentPositionOffsetSum, selectedIndex);    // position = selectedIndex, positionOffset = 0.0f
            mScrollAnimator.addUpdateListener(mAnimatorUpdateListener);
            mScrollAnimator.addListener(mAnimatorListener);
            mScrollAnimator.setInterpolator(mInterpolator);
            mScrollAnimator.setDuration(mDuration);
            mScrollAnimator.start();
        } else {
            dispatchPageSelected(selectedIndex);
            if (mScrollAnimator != null && mScrollAnimator.isRunning()) {
                dispatchPageScrolled(mLastSelectedIndex, 0.0f, 0);
            }
            dispatchPageScrollStateChanged(ScrollState.SCROLL_STATE_IDLE);
            dispatchPageScrolled(selectedIndex, 0.0f, 0);
        }
        mLastSelectedIndex = selectedIndex;
    }
    private void dispatchPageSelected(int pageIndex) {
        magicIndicator.onPageSelected(pageIndex);
    }
    private void dispatchPageScrollStateChanged(int state) {
        magicIndicator.onPageScrollStateChanged(state);
    }
    private void dispatchPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }
}
