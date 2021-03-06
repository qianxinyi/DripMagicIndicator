package com.iflytek.drip.magicindicatordemo.example;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.iflytek.drip.magicindicator.DripMagicIndicator;
import com.iflytek.drip.magicindicator.FragmentContainerHelper;
import com.iflytek.drip.magicindicator.MagicIndicator;
import com.iflytek.drip.magicindicator.ViewPagerHelper;
import com.iflytek.drip.magicindicator.buildins.UIUtil;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.CommonNavigator;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.CommonNavigatorAdapter;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.IPagerIndicator;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.IPagerTitleView;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import com.iflytek.drip.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import com.iflytek.drip.magicindicatordemo.R;
import com.iflytek.drip.magicindicatordemo.ext.titles.ScaleTransitionPagerTitleView;

import java.util.Arrays;
import java.util.List;

public class FixedTabExampleActivity extends AppCompatActivity {
    private static final String[] CHANNELS = new String[]{"KITKAT", "LOLLIPOP","NOUGAT", "DONUT","YOKU","WOWO"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_tab_example_layout);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mExamplePagerAdapter);

        initMagicIndicator1();
        initMagicIndicator2();
        initMagicIndicator3();
        initMagicIndicator4();
    }

    private void initMagicIndicator1() {
        DripMagicIndicator magicIndicator = (DripMagicIndicator) findViewById(R.id.magic_indicator1);
        DripMagicIndicator.Builder builder=magicIndicator.new Builder();
        builder.setAdjustMode(true)
               .setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#88ffffff"));
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#40c4ff"));
                return indicator;
            }
        })
               .build();
        magicIndicator.setDividerLines(15);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void initMagicIndicator2() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator2);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.parseColor("#616161"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#f57c00"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
                indicator.setYOffset(UIUtil.dip2px(context, 39));
                indicator.setLineHeight(UIUtil.dip2px(context, 1));
                indicator.setColors(Color.parseColor("#f57c00"));
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                if (index == 0) {
                    return 2.0f;
                } else if (index == 1) {
                    return 2.3f;
                } else if(index==2) {
                    return 2.0f;
                } else{
                        return 2.0f;

                }
            }
        });
        commonNavigator.setSkimOver(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void initMagicIndicator3() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator3);
        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(mDataList.get(index));
                clipPagerTitleView.setTextColor(Color.parseColor("#e94220"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = context.getResources().getDimension(R.dimen.common_navigator_height);
                float borderWidth = UIUtil.dip2px(context, 1);
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(lineHeight / 2);
                indicator.setYOffset(borderWidth);
                indicator.setColors(Color.parseColor("#bc2a2a"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void initMagicIndicator4() {
        DripMagicIndicator magicIndicator = (DripMagicIndicator) findViewById(R.id.magic_indicator4);
        DripMagicIndicator.Builder builder=magicIndicator.new Builder();
        builder.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(UIUtil.dip2px(context, 10));
                linePagerIndicator.setColors(Color.WHITE);
                return linePagerIndicator;
            }
        }).build();
        magicIndicator.setDividerLines(new ColorDrawable(Color.WHITE) {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(FixedTabExampleActivity.this, 0.5);
            }
        });
        ViewPagerHelper.bind(magicIndicator, mViewPager);
//        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(magicIndicator);
//        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
//        fragmentContainerHelper.setDuration(300);
//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                fragmentContainerHelper.handlePageSelected(position);
//            }
//        });
    }
}
