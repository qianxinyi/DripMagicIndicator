package net.lucode.hackware.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.IMeasurablePagerTitleView;

/**
 * 通用的指示器标题，子元素内容由外部提供。
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/7/3.
 */
public abstract class CommonPagerTitleView extends FrameLayout implements IMeasurablePagerTitleView {
    public CommonPagerTitleView(Context context) {
        super(context);
    }

    /**
     * 外部直接将布局设置进来
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        setContentView(contentView, null);
    }

    public void setContentView(View contentView, FrameLayout.LayoutParams lp) {
        removeAllViews();
        if (contentView != null) {
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            addView(contentView, lp);
        }
    }

    public void setContentView(int layoutId) {
        View child = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setContentView(child, null);
    }
    @Override
    public int getContentLeft() {
        return getLeft();
    }

    @Override
    public int getContentTop() {

        return getTop();
    }

    @Override
    public int getContentRight() {

        return getRight();
    }

    @Override
    public int getContentBottom() {

        return getBottom();
    }


}
