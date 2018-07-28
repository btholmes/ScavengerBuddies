package ben.holmes.scavenger.buddies.App.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;

public class ListHeaderView extends RelativeLayout {

    private Context ctx;
    private View root;

    private LinearLayout content;
    private TextView text;

    public ListHeaderView(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }

    public ListHeaderView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);
    }


    @SuppressLint("ResourceType")
    private void sharedConstructor(Context ctx, @Nullable  AttributeSet attrs){
        this.ctx = ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.list_header_view, this);
        content = root.findViewById(R.id.content);
        text = root.findViewById(R.id.text);

        if(attrs != null){
            TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.ShadowButton);
            for(int i = 0; i < ta.getIndexCount(); i++){
                int type = ta.getType(i);
                switch (type){
                    case R.styleable.ShadowButton_text:
                        String text = ta.getString(i);
                        setText(text);
                        break;
                    case R.styleable.ShadowButton_text_color:
                        int color = ta.getResourceId(i, R.color.black);
                        setTextColor(color);
                        break;
                    case R.styleable.ShadowButton_background_color:
                        int backgroundColor = ta.getResourceId(i, R.color.colorPrimary);
                        setBackgroundColor(backgroundColor);
                        break;
                }
            }
            ta.recycle();
        }
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public void setTextColor(int color){
        this.text.setTextColor(ContextCompat.getColor(ctx, color));
    }

    @SuppressLint("ResourceType")
    public void setBackgroundColor(int color){
        this.content.setBackgroundColor(ContextCompat.getColor(ctx, color));
    }
}
