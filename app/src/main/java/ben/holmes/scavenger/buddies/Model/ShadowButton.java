package ben.holmes.scavenger.buddies.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;

public class ShadowButton extends RelativeLayout{

    private Context ctx;
    private TextView text;
    private ImageView leftIcon;
    private ImageView rightIcon;
    private RelativeLayout buttonContainer;
    private FrameLayout shadow;
    private boolean isElevated;
    private boolean isChecked;

    private View checkMark;

    private Paint mPaint;
    private Rect mRect;


    public ShadowButton(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }

    public ShadowButton(Context ctx, @Nullable AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);
    }


    private void sharedConstructor(Context ctx, @Nullable AttributeSet attrs){
        this.ctx = ctx;
        inflate(ctx, R.layout.button_shadow, this);
        this.text = findViewById(R.id.text);
        this.leftIcon = findViewById(R.id.leftIcon);
        this.rightIcon = findViewById(R.id.rightIcon);
        this.buttonContainer = findViewById(R.id.button_container);
        this.shadow = findViewById(R.id.shadow);
        this.checkMark = findViewById(R.id.check_mark);
        this.isElevated = true;
        isChecked = false;

        if(attrs != null){
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRect = new Rect();

            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowButton);
            String text = ta.getString(R.styleable.ShadowButton_text);
            int textColor = ta.getColor(R.styleable.ShadowButton_text_color, ContextCompat.getColor(getContext(), R.color.white));
            int backgroundColor = ta.getColor(R.styleable.ShadowButton_background_color, ContextCompat.getColor(getContext(), R.color.white));
            int rightIcon = ta.getInteger(R.styleable.ShadowButton_right_icon, R.drawable.ic_add);
            int leftIcon = ta.getInteger(R.styleable.ShadowButton_left_icon, R.drawable.ic_add);
            ta.recycle();
        }
    }

    public void setText(String value){
        this.text.setText(value);
    }

    public void setTextColor(int color){
        this.text.setTextColor(ContextCompat.getColor(ctx, color));
    }

    public void setLeftIcon(int resource){
        this.leftIcon.setImageDrawable(ContextCompat.getDrawable(ctx, resource));
    }

    public void setLeftIconColor(int color){
        this.leftIcon.setColorFilter(ContextCompat.getColor(ctx, color));

    }

    public void setRightIcon(int resource){
        this.rightIcon.setImageDrawable(ContextCompat.getDrawable(ctx, resource));
    }

    public void setRightIconColor(int color){
        this.rightIcon.setColorFilter(ContextCompat.getColor(ctx, color));

    }

    public void hideLeftIcon(){
        this.leftIcon.setVisibility(GONE);
    }
    public void hideRightIcon(){
        this.rightIcon.setVisibility(GONE);
    }
    public void showLeftIcon(){
        this.leftIcon.setVisibility(VISIBLE);
    }
    public void showRightIcon(){
        this.rightIcon.setVisibility(VISIBLE);
    }


    public void setElevated(){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)buttonContainer.getLayoutParams();
        params.gravity = Gravity.TOP;
        buttonContainer.setLayoutParams(params);
        isElevated = true;
    }

    public void setClicked(){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)buttonContainer.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        buttonContainer.setLayoutParams(params);
        isElevated = false;
    }

    public void setUnChecked(){
        this.checkMark.setVisibility(GONE);
        isChecked = false;
    }

    public void setChecked(){
        this.checkMark.setVisibility(VISIBLE);
        this.checkMark.bringToFront();
        isChecked = true;
    }

    public boolean isChecked(){
        return isChecked;
    }

    public void toggleElevation(){
        if(isElevated)
            setClicked();
        else
            setElevated();
    }

    public void setShadowColor(int color){
        Drawable background = this.shadow.getBackground();
        ((GradientDrawable)background).setColor(ContextCompat.getColor(ctx, color));
    }

    public void setButtonBackgroundColor(int color){
        Drawable background = this.buttonContainer.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable)background;
        gradientDrawable.setColor(ContextCompat.getColor(ctx, color));
    }

    public void setBorderColor(int color){
        Drawable background = this.buttonContainer.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable)background;
        gradientDrawable.setStroke(ctx.getResources().getDimensionPixelSize(R.dimen.button_border_width), ContextCompat.getColor(ctx, color));

    }



    public interface  QuickClick{
        void onSuccess();
    }

    public void quickClick(final QuickClick callback){
        toggleElevation();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleElevation();
                callback.onSuccess();
            }
        }, 50);
    }

}
