package com.iflytek.drip.magicindicator;

/**
 * 整个框架的入口，核心
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public interface IMagicIndicator {

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);
}
