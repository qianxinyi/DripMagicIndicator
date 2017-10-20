package net.lucode.hackware.magicindicator;

/**
 * 自定义滚动状态，消除对ViewPager的依赖
 * Created by hackware on 2016/8/27.
 */

public interface ScrollBehavior {
    int SCROLL_AUTOADJUST = 1;
    int SCROLL_SKIMOVER = 1<<1;
    int SCROLL_FOLLOWTOUCH = 1<<2;
    int SCROLL_SMOOTHSCROLL = 1<<3;
}
