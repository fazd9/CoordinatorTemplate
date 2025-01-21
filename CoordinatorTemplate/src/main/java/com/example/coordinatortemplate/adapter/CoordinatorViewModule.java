package com.example.coordinatortemplate.adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.widget.NestedScrollView;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Create by zhuyang 2025-01-16
 */
public class CoordinatorViewModule {
    private AppBarLayout appBarLayout;
    private View viewInCollapsingToolbarLayout;
    private View viewInToolBar;
    private NestedScrollView nestedScrollView;
    private float DAMPING_FACTOR = 0.3f;
    private final int LIST_TOP = 1, LIST_MOVE = 0, LIST_BOTTON = 2;
    private float lastprogress = -1, lastverticalOffset = 1;
    private boolean flag = true, isSupportMoving = true;
    private static final String TAG = "WordClockMoveViewModule";
    private int isTop = 1;
    private boolean LIST_VIEW_SUPPORT_SPRING_BACK = true;
    private int clickTime = 500;
    private int listTime = 500;
    private long lastClickTime = 0;
    private Context context;
    private float click_end_position = 0.8f;
    private float viewInCollapsingToolbarScaleX = 0.5f, viewInCollapsingToolbarScaleY = 0.5f, viewInCollapsingToolbarTranslationY = 0.1f;
    private float viewInToolBarScaleX = 0.2f, viewInToolBarScaleY = 0.2f;
    private int viewInCollapsingToolbarAlpha = 5;
    private float viewInToolBarAlpha = 0.1f;

    public CoordinatorViewModule(Context context, NestedScrollView nestedScrollView){
        this.nestedScrollView = nestedScrollView;
        this.context = context;
        if (LIST_VIEW_SUPPORT_SPRING_BACK) {
            setListViewListener(nestedScrollView);
        }
        setScrollListener(nestedScrollView);
    }

    public CoordinatorViewModule(Context context, AppBarLayout appBarLayout, View viewInCollapsingToolbarLayout, View viewInToolBar, NestedScrollView nestedScrollView) {
        this.appBarLayout = appBarLayout;
        this.viewInCollapsingToolbarLayout = viewInCollapsingToolbarLayout;
        this.viewInToolBar = viewInToolBar;
        this.nestedScrollView = nestedScrollView;
        this.context = context;
        setViewClickLineare();
        if (LIST_VIEW_SUPPORT_SPRING_BACK) {
            setListViewListener(nestedScrollView);
        }
        setScrollListener(nestedScrollView);
        setAppBarLayoutChangedListener(appBarLayout);
    }

    /**
     * 设置外层视图的变化倍率
     *
     * @param viewInCollapsingToolbarScaleX       X轴缩放
     * @param viewInCollapsingToolbarScaleY       Y轴缩放
     * @param viewInCollapsingToolbarTranslationY Y轴中心点偏移
     */
    public void setviewInCollapsingToolbar(float viewInCollapsingToolbarScaleX, float viewInCollapsingToolbarScaleY, float viewInCollapsingToolbarTranslationY) {
        this.viewInCollapsingToolbarScaleX = viewInCollapsingToolbarScaleX;
        this.viewInCollapsingToolbarScaleY = viewInCollapsingToolbarScaleY;
        this.viewInCollapsingToolbarTranslationY = viewInCollapsingToolbarTranslationY;
    }

    /**
     * 设置TooBar视图的变化倍率
     *
     * @param viewInToolBarScaleX X轴缩放
     * @param viewInToolBarScaleY Y轴缩放
     */
    public void setviewInToolBar(float viewInToolBarScaleX, float viewInToolBarScaleY) {
        this.viewInToolBarScaleX = viewInToolBarScaleX;
        this.viewInToolBarScaleY = viewInToolBarScaleY;
    }

    /**
     * 是否支持列表回弹
     *
     * @param isSupport 布尔值(默认true)
     */
    public void setListReboundAnimation(boolean isSupport) {
        this.LIST_VIEW_SUPPORT_SPRING_BACK = isSupport;
    }

    /**
     * 设置点击动画持续时间
     *
     * @param clickTime 时间（单位毫秒）
     */
    public void setClickOnAnimationTime(int clickTime) {
        this.clickTime = clickTime;
    }

    /**
     * 设置列表回弹动画持续时间
     *
     * @param listTime 时间（单位毫秒）
     */
    public void setListOnAnimationTime(int listTime) {
        this.listTime = listTime;
    }

    /**
     * 设置列表跟随手指移动倍率
     *
     * @param damping 倍率值（推荐 < 0.5f）
     */
    public void setDampingFactor(float damping) {
        this.DAMPING_FACTOR = damping;
    }

    /**
     * 获取列表状态
     *
     * @return 1：在顶部 0：在移动 2：在底部
     */
    public int getListState() {
        return isTop;
    }

    /**
     * 获取滑动进度
     *
     * @return 0.0--1.0 折叠--展开
     */
    public float getLastprogress() {
        return lastprogress;
    }

    /**
     * 获取滑动偏移量
     *
     * @return 移动偏移量
     */
    public float getLastverticalOffset() {
        return lastverticalOffset;
    }

    /**
     * 设置点击后视图的缩放倍率
     *
     * @param endFactor 0.0--1.0
     */
    public void setClickEndFactor(float endFactor) {
        this.click_end_position = endFactor;
    }

    /**
     * 设置CollapsingToolbar透明度改变速率倍数
     * @param value 倍数
     */
    public void setCollapsingToolbarAlphaChange(int value){
        this.viewInCollapsingToolbarAlpha = value;
    }

    /**
     * 设置ToolBar内容显示时机
     * @param value 推荐值（0.0--0.2）
     */
    public void setToolBarAlphaChange(float value){
        this.viewInToolBarAlpha = value;
    }

    @SuppressLint({"ClickableViewAccessibility", "CheckResult"})
    private void setViewClickLineare() {
        appBarLayout.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > clickTime) {
                lastClickTime = currentTime;
                isSupportMoving = false;
                setViewClockWithAnimation();
            }
        });

        viewInToolBar.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > clickTime) {
                lastClickTime = currentTime;
                if (lastverticalOffset == 0) {
                    isSupportMoving = false;
                    setViewClockWithAnimation();
                }
            }
        });
    }

    private void setAppBarLayoutChangedListener(AppBarLayout appBarLayout) {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float progress = Math.abs(verticalOffset) * 1.0f / totalScrollRange;
//                Log.d(TAG, "onOffsetChanged: verticalOffset = " + verticalOffset);
//                Log.d(TAG, "onOffsetChanged: progress = " + progress);
                lastverticalOffset = verticalOffset;
                lastprogress = progress;
                if (progress == 0.0 && !isSupportMoving) {
                    return;
                }
                setViewPosition(viewInCollapsingToolbarLayout, viewInToolBar, progress);
                if (progress == 1.0) {
                    endViewPosition(viewInCollapsingToolbarLayout, viewInToolBar);
                }
            }
        });
    }

    private void setScrollListener(NestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isTop != LIST_MOVE) {
                    isTop = LIST_MOVE;
                }
                if (!v.canScrollVertically(-1) && scrollY <= 0) {
                    isTop = LIST_TOP;
                    Log.d(TAG, "onScrollChange: Top!!!");
                }

                if (!v.canScrollVertically(1) && scrollY > v.getChildAt(0).getHeight() - v.getHeight()) {
                    isTop = LIST_BOTTON;
                    Log.d(TAG, "onScrollChange: Bootn!!!");
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setListViewListener(NestedScrollView nestedScrollView) {
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            private float startY = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isSupportMoving) {
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        resetPositionWithAnimation(nestedScrollView);
                        startY = -1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (startY == -1) {
                            startY = event.getY();
                        }
                        float currentY = event.getY();
                        float deltaY = currentY - startY;

                        if (deltaY > 0) {
                            applyBounceEffectToListItems(nestedScrollView, deltaY);
                        } else if (lastverticalOffset == -appBarLayout.getTotalScrollRange()) {
                            applyBounceEffectToListItems(nestedScrollView, deltaY);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                }
                return false;
            }

            private void applyBounceEffectToListItems(NestedScrollView view, float deltaY) {
                int itemCount = view.getChildCount();
                for (int i = 0; i < itemCount; i++) {
                    View listItem = view.getChildAt(i);
                    listItem.setTranslationY(deltaY * DAMPING_FACTOR);
                }
            }

            private void resetPositionWithAnimation(NestedScrollView view) {
                int itemCount = view.getChildCount();
                for (int i = 0; i < itemCount; i++) {
                    View listItem = view.getChildAt(i);
                    ValueAnimator animator = ValueAnimator.ofFloat(listItem.getTranslationY(), 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            listItem.setTranslationY((float) animation.getAnimatedValue());
                        }
                    });
                    animator.setDuration(listTime);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();
                }
            }
        });
    }

    private void setViewPosition(View viewInCollapsingToolbarLayout, View viewInToolBar, float progress) {
        viewInCollapsingToolbarLayout.setScaleX(1 - progress * viewInCollapsingToolbarScaleX);
        viewInCollapsingToolbarLayout.setScaleY(1 - progress * viewInCollapsingToolbarScaleY);
        viewInToolBar.setScaleX(1 - progress * viewInToolBarScaleX);
        viewInToolBar.setScaleY(1 - progress * viewInToolBarScaleY);
        float translationY = -(viewInCollapsingToolbarLayout.getHeight() * progress * viewInCollapsingToolbarTranslationY);
        viewInCollapsingToolbarLayout.setTranslationY(translationY);

        if (flag) {
            viewInCollapsingToolbarLayout.setAlpha(1 - viewInCollapsingToolbarAlpha * progress);
            viewInToolBar.setAlpha(progress - viewInToolBarAlpha);
        }
    }


    private void setViewClockWithAnimation() {
        View fadeOutView, fadeInView;

        if (flag) {
            fadeOutView = viewInCollapsingToolbarLayout;
            fadeInView = viewInToolBar;
            animateView(fadeOutView, 1.0f, 1.0f, click_end_position, click_end_position, 1, 0, clickTime);
            animateView(fadeInView, click_end_position, click_end_position, 1, 1, 0, 1, clickTime);
        } else {
            fadeOutView = viewInToolBar;
            fadeInView = viewInCollapsingToolbarLayout;
            animateView(fadeOutView, 1.0f, 1f, click_end_position, click_end_position, 1, 0, clickTime);
            animateView(fadeInView, click_end_position, click_end_position, 1, 1, 0, 1, clickTime);
        }
        flag = !flag;
    }

    private void animateView(View view, float startScaleX, float startScaleY, float endScaleX, float endScaleY, float startAlpha, float endAlpha, long duration) {
        view.setAlpha(startAlpha);
        view.setScaleX(startScaleX);
        view.setScaleY(startScaleY);

        view.animate()
                .scaleX(endScaleX)
                .scaleY(endScaleY)
                .alpha(endAlpha)
                .setDuration(duration)
                .withEndAction(() -> {
                    view.setScaleX(endScaleX);
                    view.setScaleY(endScaleY);
                    view.setAlpha(endAlpha);
                    isSupportMoving = true;
                })
                .start();
    }


    private void endViewPosition(View viewInCollapsingToolbarLayout, View viewInToolBar) {
        Log.d(TAG, "endViewPosition: ");
        viewInCollapsingToolbarLayout.setScaleX(1 - viewInCollapsingToolbarScaleX);
        viewInCollapsingToolbarLayout.setScaleY(1 - viewInCollapsingToolbarScaleY);
        viewInCollapsingToolbarLayout.setAlpha(0);
        viewInCollapsingToolbarLayout.setTranslationY(-viewInCollapsingToolbarLayout.getHeight() * viewInCollapsingToolbarTranslationY);
        viewInToolBar.setScaleX(1 - viewInToolBarScaleX);
        viewInToolBar.setScaleY(1 - viewInToolBarScaleY);
        viewInToolBar.setAlpha(1);
    }
}
