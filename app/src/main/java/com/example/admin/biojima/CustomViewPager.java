package com.example.admin.biojima;

        import android.content.Context;
        import android.support.v4.view.ViewPager;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.MotionEvent;

        import java.io.PrintWriter;
        import java.io.StringWriter;

/**
 * Created by adslbna2 on 15. 10. 24..
 */
public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}