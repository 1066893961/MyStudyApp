package application.app.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private ViewGroup view1, view2, view3;
    private MagicIndicator magicIndicatorReal;
    private View tablayoutRealFl;
    private View tablayoutHolder;
    private LinearLayout container;
    private List<String> tabStr = new ArrayList<>();
    private List<ViewGroup> anchorList = new ArrayList<>();
    //判读是否是scrollview主动引起的滑动，true-是，false-否，由tablayout引起的
    private boolean isScroll;
    //记录上一次位置，防止在同一内容块里滑动 重复定位到tablayout
    private int lastPos = 0;
    //监听判断最后一个模块的高度，不满一屏时让最后一个模块撑满屏幕
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private CustomScrollView scrollView;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.rv_layout, null);
        view2 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.rv_layout2, null);
        view3 = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.rv_layout3, null);

        tv1 = view1.findViewById(R.id.tv1);

        magicIndicatorReal = (MagicIndicator) findViewById(R.id.magic_indicator_real);
        scrollView = (CustomScrollView) findViewById(R.id.scrollView);
        tablayoutRealFl = (View) findViewById(R.id.tablayout_real_fl);
        tablayoutHolder = (View) findViewById(R.id.tablayout_holder);
        container = (LinearLayout) findViewById(R.id.container);
        tabStr.add("13sd132");
        tabStr.add("13s1s32");
        tabStr.add("131sd32");



        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //一开始让实际的tablayout 移动到 占位的tablayout处，覆盖占位的tablayout
                tablayoutRealFl.setTranslationY(tablayoutHolder.getTop());
                tablayoutRealFl.setVisibility(View.VISIBLE);
                container.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                scrollView.scrollBy(0, 1);//不明白为什么tablayoutRealFl在开始的时候会显示出来，位置完全不对应，但是滚动的时候位置又是正确的，想半天没想明白，所以让scrollview滚动一下让tabLayoutRealFl消失

            }
        };
        container.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //这种方法会被内容布局拦截掉ACTION_DOWN，所以注释掉用下面的写法
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    isScroll = true;
//                }
                if (!isScroll) {
                    isScroll = true;
                }
                return false;
            }
        });

        //监听scrollview滑动
        scrollView.setCallbacks(new CustomScrollView.Callbacks() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                //根据滑动的距离y(不断变化的) 和 holderTabLayout距离父布局顶部的距离(这个距离是固定的)对比，
                //当y < holderTabLayout.getTop()时，holderTabLayout 仍在屏幕内，realTabLayout不断移动holderTabLayout.getTop()距离，覆盖holderTabLayout
                //当y > holderTabLayout.getTop()时，holderTabLayout 移出，realTabLayout不断移动y，相对的停留在顶部，看上去是静止的
                int top = tablayoutHolder.getTop();
                int translation = Math.max(y, top);
                tablayoutRealFl.setTranslationY(translation);

                if (isScroll) {
                    for (int i = tabStr.size() - 1; i >= 0; i--) {
                        //需要y减去顶部内容区域的高度(具体看项目的高度，这里demo写死的200dp)
                        if (!anchorList.isEmpty()) {
                            int anchorViewTop = anchorList.get(i).getTop();
                            if (y - top > anchorViewTop) {
                                setScrollPos(i);
                                break;
                            }
                        }
                    }
                }

            }
        });

        tv1.setText("this is the first view");

        setTabs();

        anchorList.add(view1);
        anchorList.add(view2);
        anchorList.add(view3);

        container.addView(view1);
        container.addView(view2);
        container.addView(view3);

        scrollView.smoothScrollTo(0, 0);
//        TextView textView = (TextView)findViewById(R.id.textview);

//        textView.setText(Html.fromHtml("<font color=\"#ff0000\">红色</font>其它颜色"));
//        textView.setText(getResources().getString(R.string.str_text1,"张里","张里"));
//        textView.setText(Html.fromHtml(getResources().getString(R.string.distribution_tip,"张翠山","张翠山")));
    }


    private void setScrollPos(int newPos) {
        if (lastPos != newPos) {
            magicIndicatorReal.onPageSelected(newPos);
            magicIndicatorReal.onPageScrolled(newPos, 0.0F, 0);
        }
        lastPos = newPos;
    }


    /**
     * 设置indicator数据
     */
    private void setTabs() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#333333"));
                colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#30a6ae"));
                colorTransitionPagerTitleView.setText(tabStr.get(index));
                colorTransitionPagerTitleView.setTextSize(15);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //实际的tab的点击切换
                        magicIndicatorReal.onPageSelected(index);
                        magicIndicatorReal.onPageScrolled(index, 0.0F, 0);
                        selectTab(colorTransitionPagerTitleView.getText(), index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(60);
                linePagerIndicator.setLineHeight(4);
                linePagerIndicator.setColors(Color.parseColor("#41bcbc"));
                return linePagerIndicator;
            }

        });
        magicIndicatorReal.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return 8;
            }
        });
    }


    private void selectTab(CharSequence title, int pos) {
        isScroll = false;
        if (!anchorList.isEmpty()) {
            int top = anchorList.get(pos).getTop();
            //同样这里滑动要加上顶部内容区域的高度(这里写死的高度)
            scrollView.smoothScrollTo(0, top + tablayoutHolder.getTop());
        }

    }
}
