package io.github.httpmattpvaughn.hnapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class DisableableViewPager extends ViewPager {

    private boolean isSwipingEnabled = true;

    public DisableableViewPager(@NonNull Context context) {
        super(context);
    }

    public DisableableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSwipingEnabled(boolean swipingEnabled) {
        this.isSwipingEnabled = swipingEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipingEnabled) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSwipingEnabled) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
