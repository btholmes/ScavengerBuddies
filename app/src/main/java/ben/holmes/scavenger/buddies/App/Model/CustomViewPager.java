package ben.holmes.scavenger.buddies.App.Model;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class CustomViewPager extends ViewPager {

    private boolean canSwipe = true;

    public CustomViewPager(Context ctx){
        super(ctx);
    }


    public CustomViewPager(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
    }

    public void setCanSwipe(boolean canSwipe){
        this.canSwipe = canSwipe;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!canSwipe)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }
}
