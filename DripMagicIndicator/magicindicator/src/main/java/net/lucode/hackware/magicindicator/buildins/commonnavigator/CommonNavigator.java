package net.lucode.hackware.magicindicator.buildins.commonnavigator;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.NavigatorHelper;
import net.lucode.hackware.magicindicator.R;
import net.lucode.hackware.magicindicator.ScrollState;
import net.lucode.hackware.magicindicator.abs.IPagerNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的指示器，包含PagerTitle和PagerIndicator
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class CommonNavigator extends FrameLayout implements IPagerNavigator, NavigatorHelper.OnNavigatorScrollListener {
    private HorizontalScrollView mScrollView;
    private LinearLayout mTitleContainer;
    private LinearLayout mIndicatorContainer;
    private IPagerIndicator mIndicator;

    private CommonNavigatorAdapter mAdapter;
    private NavigatorHelper mNavigatorHelper;

    /**
     * 提供给外部的参数配置
     */
    /****************************************************/
    private boolean mAdjustMode;   // 自适应模式，适用于数目固定的、少量的title
    private boolean mEnablePivotScroll; // 启动中心点滚动
    private float mScrollPivotX = 0.5f; // 滚动中心点 0.0f - 1.0f
    private boolean mSmoothScroll = true;   // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
    private boolean mFollowTouch = true;    // 是否手指跟随滚动
    private boolean mSkimOver;  // 跨多页切换时，中间页是否显示 "掠过" 效果[只针对指示器而已]

    private int mRightPadding;
    private int mLeftPadding;
    private boolean mIndicatorOnTop;    // 指示器是否在title上层，默认为下层
    private boolean mReselectWhenLayout = true; // PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确
    /****************************************************/

    // 保存每个title的位置信息，为扩展indicator提供保障
    private List<PositionData> mPositionDataList = new ArrayList<PositionData>();

    private DataSetObserver mObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            mNavigatorHelper.setTotalCount(mAdapter.getCount());    // 如果使用helper，应始终保证helper中的totalCount为最新
            init();
        }

        @Override
        public void onInvalidated() {
            // 没什么用，暂不做处理
        }
    };

    public CommonNavigator(Context context) {
        super(context);
        mNavigatorHelper = new NavigatorHelper();
        mNavigatorHelper.setNavigatorScrollListener(this);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean isAdjustMode() {
        return mAdjustMode;
    }

    public CommonNavigator setAdjustMode(boolean is) {
        mAdjustMode = is;
        return this;
    }

    public CommonNavigatorAdapter getAdapter() {
        return mAdapter;
    }

    public CommonNavigator setAdapter(CommonNavigatorAdapter adapter) {
        if (mAdapter == adapter) {
            return this;
        }
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
            mNavigatorHelper.setTotalCount(mAdapter.getCount());
            if (mTitleContainer != null) {  // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mNavigatorHelper.setTotalCount(0);
            init();
        }
        return this;
    }

    private void init() {
        removeAllViews();

        View root;
        if (mAdjustMode) {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout_no_scroll, this);
        } else {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout, this);
        }

        mScrollView = (HorizontalScrollView) root.findViewById(R.id.scroll_view);   // mAdjustMode为true时，mScrollView为null

        mTitleContainer = (LinearLayout) root.findViewById(R.id.title_container);
        mTitleContainer.setPadding(mLeftPadding, 0, mRightPadding, 0);

        mIndicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        if (mIndicatorOnTop) {
            mIndicatorContainer.getParent().bringChildToFront(mIndicatorContainer);
        }

        initTitlesAndIndicator();
    }

    /**
     * 初始化title和indicator
     */
    private void initTitlesAndIndicator() {
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            IPagerTitleView v = mAdapter.getTitleView(getContext(), i);
            if (v instanceof View) {
                View view = (View) v;
                LinearLayout.LayoutParams lp;
                if (mAdjustMode) {
                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = mAdapter.getTitleWeight(getContext(), i);
                } else {
                    lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                mTitleContainer.addView(view, lp);
            }
        }
        if (mAdapter != null) {
            mIndicator = mAdapter.getIndicator(getContext());
            if (mIndicator instanceof View) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mIndicatorContainer.addView((View) mIndicator, lp);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter != null) {
            preparePositionData();
            if (mIndicator != null) {
                mIndicator.onPositionDataProvide(mPositionDataList);
            }
            if (mReselectWhenLayout && mNavigatorHelper.getScrollState() == ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.getCurrentIndex());
                onPageScrolled(mNavigatorHelper.getCurrentIndex(), 0.0f, 0);
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private void preparePositionData() {
        mPositionDataList.clear();
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            PositionData data = new PositionData();
            View v = mTitleContainer.getChildAt(i);
            if (v != null) {
                data.mLeft = v.getLeft();
                data.mTop = v.getTop();
                data.mRight = v.getRight();
                data.mBottom = v.getBottom();
                if (v instanceof IMeasurablePagerTitleView) {
                    IMeasurablePagerTitleView view = (IMeasurablePagerTitleView) v;
                    data.mContentLeft = view.getContentLeft();
                    data.mContentTop = view.getContentTop();
                    data.mContentRight = view.getContentRight();
                    data.mContentBottom = view.getContentBottom();
                } else {
                    data.mContentLeft = data.mLeft;
                    data.mContentTop = data.mTop;
                    data.mContentRight = data.mRight;
                    data.mContentBottom = data.mBottom;
                }
            }
            mPositionDataList.add(data);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (mIndicator != null) {
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size() > 0 && position >= 0 && position < mPositionDataList.size()) {
                if (mFollowTouch) {
                    int currentPosition = Math.min(mPositionDataList.size() - 1, position);
                    int nextPosition = Math.min(mPositionDataList.size() - 1, position + 1);
                    PositionData current = mPositionDataList.get(currentPosition);
                    PositionData next = mPositionDataList.get(nextPosition);
                    float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    float nextScrollTo = next.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    mScrollView.scrollTo((int) (scrollTo + (nextScrollTo - scrollTo) * positionOffset), 0);
                } else if (!mEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    public float getScrollPivotX() {
        return mScrollPivotX;
    }

    public void setScrollPivotX(float scrollPivotX) {
        mScrollPivotX = scrollPivotX;
    }

    @Override
    public void onPageSelected(int position) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageSelected(position);
            if (mIndicator != null) {
                mIndicator.onPageSelected(position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageScrollStateChanged(state);
            if (mIndicator != null) {
                mIndicator.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onAttachToMagicIndicator() {
        init(); // 将初始化延迟到这里
    }

    @Override
    public void onDetachFromMagicIndicator() {
    }

    public IPagerIndicator getPagerIndicator() {
        return mIndicator;
    }

    public boolean isEnablePivotScroll() {
        return mEnablePivotScroll;
    }

    public void setEnablePivotScroll(boolean is) {
        mEnablePivotScroll = is;
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onEnter(index, totalCount, enterPercent, leftToRight);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onLeave(index, totalCount, leavePercent, leftToRight);
        }
    }

    public boolean isSmoothScroll() {
        return mSmoothScroll;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }

    public boolean isFollowTouch() {
        return mFollowTouch;
    }

    public CommonNavigator setFollowTouch(boolean followTouch) {
        mFollowTouch = followTouch;
        return this;
    }

    public boolean isSkimOver() {
        return mSkimOver;
    }

    public CommonNavigator setSkimOver(boolean skimOver) {
        mSkimOver = skimOver;
        mNavigatorHelper.setSkimOver(skimOver);
        return this;
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onSelected(index, totalCount);
        }
        if (!mAdjustMode && !mFollowTouch && mScrollView != null && mPositionDataList.size() > 0) {
            int currentIndex = Math.min(mPositionDataList.size() - 1, index);
            PositionData current = mPositionDataList.get(currentIndex);
            if (mEnablePivotScroll) {
                float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                if (mSmoothScroll) {
                    mScrollView.smoothScrollTo((int) (scrollTo), 0);
                } else {
                    mScrollView.scrollTo((int) (scrollTo), 0);
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView.getScrollX() > current.mLeft) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mLeft, 0);
                    } else {
                        mScrollView.scrollTo(current.mLeft, 0);
                    }
                } else if (mScrollView.getScrollX() + getWidth() < current.mRight) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mRight - getWidth(), 0);
                    } else {
                        mScrollView.scrollTo(current.mRight - getWidth(), 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onDeselected(index, totalCount);
        }
    }

    public IPagerTitleView getPagerTitleView(int index) {
        if (mTitleContainer == null || mTitleContainer.getChildCount() == 0) return null;
        int childCount = mTitleContainer.getChildCount();
        if (index < 0 || index >= childCount) return null;
        return (IPagerTitleView) mTitleContainer.getChildAt(index);
    }

    public LinearLayout getTitleContainer() {
        return mTitleContainer;
    }

    public int getRightPadding() {
        return mRightPadding;
    }

    public CommonNavigator setRightPadding(int rightPadding) {

        mRightPadding = rightPadding;
        return this;
    }

    public int getLeftPadding() {
        return mLeftPadding;
    }

    public CommonNavigator setLeftPadding(int leftPadding) {
        mLeftPadding = leftPadding;
        return this;
    }

    public boolean isIndicatorOnTop() {
        return mIndicatorOnTop;
    }

    public CommonNavigator setIndicatorOnTop(boolean indicatorOnTop) {
        mIndicatorOnTop = indicatorOnTop;
        return this;
    }

    public boolean isReselectWhenLayout() {
        return mReselectWhenLayout;
    }

    public void setReselectWhenLayout(boolean reselectWhenLayout) {
        mReselectWhenLayout = reselectWhenLayout;
    }
    /**
     * offer to set badge
     */
    public void setPagerTitleViewWithMsgBadge(int index, int badgeResLayout, String msg) {
        if(msg==null) return;
        IPagerTitleView view = getPagerTitleView(index);
        if(view==null) return;
        if (!(view instanceof BadgePagerTitleView)) return;
        BadgePagerTitleView badgePagerTitleView = (BadgePagerTitleView) view;
        Context context=this.getContext();
        TextView badgeTextView = (TextView) LayoutInflater.from(context).inflate(badgeResLayout, null);
        if(badgeTextView==null) return;
        badgeTextView.setText(msg);
        badgePagerTitleView.setBadgeView(badgeTextView);
    }
    public void setPagerTitleViewWithMsgBadge(int index, String msg) {
        int layout;
        if(msg==null) return;
        layout=R.layout.simple_count_badge_layout;
        setPagerTitleViewWithMsgBadge(index,layout,msg);
    }
    public void setPagerTitleViewWithDotBadge(int index, int badgeResLayout) {
        IPagerTitleView view = getPagerTitleView(index);
        if(view==null) return;
        if (!(view instanceof BadgePagerTitleView)) return;
        BadgePagerTitleView badgePagerTitleView = (BadgePagerTitleView) view;
        Context context = this.getContext();
        View badgeView = LayoutInflater.from(context).inflate(badgeResLayout, null);
        if (badgeView == null) return;
        badgePagerTitleView.setBadgeView(badgeView);
    }
    public void setPagerTitleViewWithDotBadge(int index){
          setPagerTitleViewWithDotBadge(index,R.layout.simple_red_dot_badge_layout);
    }
// just for builder mode
//
//    public static class  CommonNavigatorBuilder{
//        private Context context;
//        private boolean mAdjustMode;   // 自适应模式，适用于数目固定的、少量的title
//        private boolean mEnablePivotScroll; // 启动中心点滚动
//        private float mScrollPivotX = 0.5f; // 滚动中心点 0.0f - 1.0f
//        private boolean mSmoothScroll = true;   // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
//        private boolean mFollowTouch = true;    // 是否手指跟随滚动
//        private int mRightPadding;
//        private int mLeftPadding;
//        private boolean mIndicatorOnTop;    // 指示器是否在title上层，默认为下层
//        private boolean mSkimOver;  // 跨多页切换时，中间页是否显示 "掠过" 效果[只针对指示器而已]
//        private boolean mReselectWhenLayout = true; //
//
//        public CommonNavigatorBuilder setContext(Context context) {
//            this.context = context;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setAdjustMode(boolean mAdjustMode) {
//            this.mAdjustMode = mAdjustMode;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setEnablePivotScroll(boolean mEnablePivotScroll) {
//            this.mEnablePivotScroll = mEnablePivotScroll;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setScrollPivotX(float mScrollPivotX) {
//            this.mScrollPivotX = mScrollPivotX;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setSmoothScroll(boolean mSmoothScroll) {
//            this.mSmoothScroll = mSmoothScroll;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setFollowTouch(boolean mFollowTouch) {
//            this.mFollowTouch = mFollowTouch;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setRightPadding(int mRightPadding) {
//            this.mRightPadding = mRightPadding;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setLeftPadding(int mLeftPadding) {
//            this.mLeftPadding = mLeftPadding;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setIndicatorOnTop(boolean mIndicatorOnTop) {
//            this.mIndicatorOnTop = mIndicatorOnTop;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setSkimOver(boolean mSkimOver) {
//            this.mSkimOver = mSkimOver;
//            return this;
//        }
//
//        public CommonNavigatorBuilder setReselectWhenLayout(boolean mReselectWhenLayout) {
//            this.mReselectWhenLayout = mReselectWhenLayout;
//            return this;
//        }
//
//        public CommonNavigator build(){
//            CommonNavigator commonNavigator=new CommonNavigator(context);
//            commonNavigator.setAdjustMode(this.mAdjustMode);
//            commonNavigator.setFollowTouch(this.mFollowTouch);
//            commonNavigator.setEnablePivotScroll(this.mEnablePivotScroll);
//            commonNavigator.setIndicatorOnTop(this.mIndicatorOnTop);
//            commonNavigator.setSkimOver(this.mSkimOver);
//            commonNavigator.setScrollPivotX(this.mScrollPivotX);
//            commonNavigator.setSmoothScroll(this.mSmoothScroll);
//            commonNavigator.setReselectWhenLayout(this.mReselectWhenLayout);
//            commonNavigator.setLeftPadding(this.mLeftPadding);
//            commonNavigator.setRightPadding(this.mRightPadding);
//            return commonNavigator;
//        }
//
//    }



}
