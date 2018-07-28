package ben.holmes.scavenger.buddies.Friends.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import ben.holmes.scavenger.buddies.R;

public class FriendSearchView extends RelativeLayout {

    private Context ctx;
    private View root;
    private RecyclerView recyclerView;

    public FriendSearchView(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }

    public FriendSearchView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);

    }

    private void sharedConstructor(Context ctx, @Nullable AttributeSet attrs){
        this.ctx = ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.friend_search_view, this);
        recyclerView = root.findViewById(R.id.recycler_view);

//        if(attrs != null){
//            TypedArray ta = ctx.obtainStyledAttributes()
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
