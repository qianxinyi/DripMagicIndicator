package com.iflytek.drip.magicindicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.iflytek.drip.magicindicator.buildins.commonnavigator.CommonNavigator;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.CommonNavigatorAdapter;

/**
 * Common 适配器，简化用户设置
 * Created by xyqian2 on 2017/10/20.
 */
public class DripMagicIndicator extends FrameLayout implements IMagicIndicator{
    private CommonNavigator mNavigator;
    public DripMagicIndicator(Context context) {
        super(context);
        if (mNavigator == null)
            mNavigator = new CommonNavigator(context);
    }

    public DripMagicIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mNavigator == null)
            mNavigator = new CommonNavigator(context);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mNavigator != null) {
            mNavigator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }
    @Override
    public void onPageSelected(int position) {
        if (mNavigator != null) {
            mNavigator.onPageSelected(position);
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        if (mNavigator != null) {
            mNavigator.onPageScrollStateChanged(state);
        }
    }

    public CommonNavigator getNavigator() {
        return mNavigator;
    }

    private void setNavigator() {
        if (mNavigator != null) {
            mNavigator.onDetachFromMagicIndicator();
        }
        removeAllViews();
        if (mNavigator instanceof View) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(mNavigator, lp);
            mNavigator.onAttachToMagicIndicator();
        }
    }

    public class Builder {
        public Builder setEnablePivotScroll(boolean enablePivotScroll) {
            mNavigator.setEnablePivotScroll(enablePivotScroll);
            return this;
        }

        public Builder setScrollPivotX(float scrollPivotX) {
            mNavigator.setScrollPivotX(scrollPivotX);
            return this;
        }

        public Builder setSmoothScroll(boolean smoothScroll) {
            mNavigator.setSmoothScroll(smoothScroll);
            return this;
        }

        public Builder setFollowTouch(boolean followTouch) {
            mNavigator.setFollowTouch(followTouch);
            return this;
        }

        public Builder setSkimOver(boolean skimOver) {
            mNavigator.setSkimOver(skimOver);
            return this;
        }

        public Builder setIndicatorOnTop(boolean indicatorOnTop) {
            mNavigator.setIndicatorOnTop(indicatorOnTop);
            return this;
        }

        public Builder setReselectWhenLayout(boolean reselectWhenLayout) {
            mNavigator.setReselectWhenLayout(reselectWhenLayout);
            return this;
        }


        public Builder setAdjustMode(boolean adjustMode) {
            mNavigator.setAdjustMode(adjustMode);
            return this;
        }


        public Builder setLeftPadding(int leftPadding) {
            mNavigator.setLeftPadding(leftPadding);
            return this;
        }

        public Builder setRightPadding(int rightPadding) {
            mNavigator.setRightPadding(rightPadding);
            return this;
        }

        public Builder setAdapter(CommonNavigatorAdapter adapter) {
            mNavigator.setAdapter(adapter);
            return this;
        }

        public void build() {
            setNavigator();
        }

    }

    /**
     * offer to set badge
     */
    public void setPagerTitleViewWithMsgBadge(int index, int badgeResLayout, String msg) {
       mNavigator.setPagerTitleViewWithMsgBadge(index,badgeResLayout,msg);
    }
    public void setPagerTitleViewWithMsgBadge(int index, String msg) {
        mNavigator.setPagerTitleViewWithMsgBadge(index,msg);
    }
    public void setPagerTitleViewWithDotBadge(int index, int badgeResLayout) {
       mNavigator.setPagerTitleViewWithDotBadge(index,badgeResLayout);
    }
    public void setPagerTitleViewWithDotBadge(int index){
      mNavigator.setPagerTitleViewWithDotBadge(index);
    }


}