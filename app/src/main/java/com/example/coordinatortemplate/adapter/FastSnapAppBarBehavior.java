package com.example.coordinatortemplate.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Method;

public class FastSnapAppBarBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "FastSnapAppBarBehavior";

    public FastSnapAppBarBehavior() {
        super();
    }

    public FastSnapAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout abl, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);

        // 使用反射来访问 getTopBottomOffsetForScrollingSibling 方法
        int currentOffset = getOffsetUsingReflection(abl);
        Log.d(TAG, "currentOffset: " + currentOffset);

        // 目标偏移量，可以根据当前的偏移量来判断
        int targetOffset;

        // 判断当前偏移量，选择收回还是展开
        if (currentOffset == 0) {
            // 如果当前偏移量为0，说明已经收回，设置目标为展开
            targetOffset = -abl.getTotalScrollRange();  // 展开，通常是最大偏移
        } else {
            // 否则，设置目标为收回位置（0）
            targetOffset = 0;
        }

        // 在这里使用反射调用 animateOffsetTo，传入计算出的目标偏移量
        Log.d(TAG, "targetOffset: " + targetOffset);
        animateOffsetWithCustomDuration(coordinatorLayout, abl, targetOffset, 5f);  // 使用自定义动画时长
    }

    private int getOffsetUsingReflection(AppBarLayout appBarLayout) {
        try {
            // 获取 getTopBottomOffsetForScrollingSibling 方法
            Method method = AppBarLayout.Behavior.class.getSuperclass().getDeclaredMethod("getTopBottomOffsetForScrollingSibling");
            method.setAccessible(true);

            // 调用该方法并返回其结果
            return (int) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception: ", e);
            return 0;  // 出错时返回0
        }
    }

    private void animateOffsetWithCustomDuration(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int offset, float velocity) {
        try {
            // 获取父类中的 animateOffsetTo 方法
            Method animateOffsetTo = AppBarLayout.Behavior.class.getSuperclass()
                    .getDeclaredMethod("animateOffsetTo", CoordinatorLayout.class, AppBarLayout.class, int.class, float.class);
            animateOffsetTo.setAccessible(true);

            // 使用反射调用 animateOffsetTo 方法
            animateOffsetTo.invoke(this, coordinatorLayout, appBarLayout, offset, velocity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception: ", e);
        }
    }



    private void getMethod() {
        Method[] methods = AppBarLayout.Behavior.class.getDeclaredMethods();
        for (Method method : methods) {
            Log.d(TAG, "Declared Method in AppBarLayout.Behavior: " + method.getName());
        }

        Class<?> superClass = AppBarLayout.Behavior.class.getSuperclass();
        if (superClass != null) {
            methods = superClass.getDeclaredMethods();
            for (Method method : methods) {
                Log.d(TAG, "Declared Method in Parent Class: " + method.getName());
            }
        }
    }

}
