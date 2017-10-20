package net.lucode.hackware.magicindicatordemo.example;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.FragmentsHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicatordemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FragmentContainerExampleActivity extends AppCompatActivity {
    private static final String[] CHANNELS = new String[]{"KITKAT", "NOUGAT", "DONUT","M"};
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
    private FragmentsHelper mFragmentsHelp;

    private static final String[] CHANNELS2 = new String[]{"首页", "消息", "联系人", "更多"};
    private static final int[] mUnselectedIds={
            R.mipmap.tab_home_unselect,R.mipmap.tab_speech_unselect,
            R.mipmap.tab_contact_unselect,R.mipmap.tab_more_unselect
    };
    private static final int[] mSelectedIds={
            R.mipmap.tab_home_select,R.mipmap.tab_speech_select,
            R.mipmap.tab_contact_select,R.mipmap.tab_more_select
    };
    private List<String> mDataList = Arrays.asList(CHANNELS2);
    private List<Fragment> mFragments2 = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container_example_layout);
        initFragments();
        initMagicIndicator1();
        initMagicIndicator2();
        //mFragmentContainerHelper.handlePageSelected(1, false);
        //switchPages(1);
    }

    private void switchPages(int index) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        for (int i = 0, j = mFragments.size(); i < j; i++) {
            if (i == index) {
                continue;
            }
            fragment = mFragments.get(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }
        fragment = mFragments.get(index);
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void initFragments() {
        for (int i = 0; i < CHANNELS.length; i++) {
            TestFragment testFragment = new TestFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TestFragment.EXTRA_TEXT, CHANNELS[i]);
            testFragment.setArguments(bundle);
            mFragments.add(testFragment);
        }
        for (int i = 0; i < CHANNELS2.length; i++) {
            TestFragment testFragment = new TestFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TestFragment.EXTRA_TEXT, CHANNELS2[i]);
            testFragment.setArguments(bundle);
            mFragments2.add(testFragment);
        }
    }

    private void initMagicIndicator1() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator1);
        mFragmentsHelp=new FragmentsHelper(magicIndicator,R.id.fragment_container,getSupportFragmentManager(),mFragments);
        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(CHANNELS[index]);
                clipPagerTitleView.setTextColor(Color.parseColor("#e94220"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mFragmentContainerHelper.handlePageSelected(index,true);
                        //switchPages(index);
                        mFragmentsHelp.setCurrentItem(index,true);
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

       // mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
    }

    private void initMagicIndicator2() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator2);
        magicIndicator.setBackgroundColor(Color.WHITE);
        final FragmentsHelper  mFragmentsHelp2=new FragmentsHelper(magicIndicator,R.id.fragment_container,getSupportFragmentManager(),mFragments2);
        final CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                BadgePagerTitleView badgePagerTitleView=new BadgePagerTitleView(context);
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

                // load custom layout
                View customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null);
                final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
                final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
                titleImg.setImageResource(mUnselectedIds[index]);
                titleText.setText(mDataList.get(index));

                commonPagerTitleView.setContentView(customLayout);
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(Color.parseColor("#00CED1"));
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(Color.LTGRAY);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        //titleImg.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
                        // titleImg.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
                        if(leavePercent>=0.8f)
                            titleImg.setImageResource(mUnselectedIds[index]);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        //titleImg.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
                        //titleImg.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
                        if(enterPercent>=0.8f)
                            titleImg.setImageResource(mSelectedIds[index]);
                    }
                });
                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragmentsHelp2.setCurrentItem(index,true);
                        titleImg.setImageResource(mSelectedIds[index]);
                        commonNavigator.setPagerTitleViewWithMsgBadge(2,""+new Random().nextInt(120));
                        commonNavigator.setPagerTitleViewWithDotBadge(1);
                    }
                });

                badgePagerTitleView.setInnerPagerTitleView(commonPagerTitleView);
                badgePagerTitleView.setXBadgeRule(BadgeAnchor.CONTENT_RIGHT,-40);
                badgePagerTitleView.setYBadgeRule(BadgeAnchor.CONTENT_TOP, 2);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);

    }

}
