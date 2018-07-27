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

    public ShadowButton(Context ctx, @Nullable AttributeSet attrs, int style){
        super(ctx, attrs, style);
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
            setText(text);

            int textColor = ta.getResourceId(R.styleable.ShadowButton_text_color, R.color.white);
            if(textColor != -1) setTextColor(textColor);

            int shadowColor = ta.getResourceId(R.styleable.ShadowButton_shadow_color, R.color.black);
            if(shadowColor != -1) setShadowColor(shadowColor);

            int borderColor = ta.getResourceId(R.styleable.ShadowButton_border_color, -1);
            if(borderColor != -1) setBorderColor(borderColor);

            int backgroundColor = ta.getResourceId(R.styleable.ShadowButton_background_color, R.color.colorPrimary);
            if(backgroundColor != -1) setButtonBackgroundColor(backgroundColor);

            int rightIcon = ta.getResourceId(R.styleable.ShadowButton_right_icon,-1);
            if(rightIcon != -1) setRightIcon(rightIcon);

            int rightIconTint = ta.getResourceId(R.styleable.ShadowButton_right_icon_tint, R.color.white);
            if(rightIconTint != -1) setRightIconColor(rightIconTint);

            int leftIcon = ta.getResourceId(R.styleable.ShadowButton_left_icon, -1);
            if(leftIcon != -1) setLeftIcon(leftIcon);

            int leftIconTint = ta.getResourceId(R.styleable.ShadowButton_left_icon_tint, R.color.white);
            if(leftIconTint != -1) setLeftIconColor(leftIconTint);

            ta.recycle();
        }
    }

    public void setText(String value){
        this.text.setText(value);
    }

    public void setTextColor(int color){
        this.text.setTextColor(ContextCompat.getColor(ctx, color));
//        this.text.setTextColor(color);
    }

    public void setLeftIcon(int resource){
        this.leftIcon.setImageDrawable(ContextCompat.getDrawable(ctx, resource));
    }

    public void setLeftIconColor(int color){
        this.leftIcon.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.MULTIPLY);
//        this.leftIcon.setColorFilter(color);
//        this.leftIcon.setBackgroundColor(color);

    }

    public void setRightIcon(int resource){
        this.rightIcon.setImageDrawable(ContextCompat.getDrawable(ctx, resource));
    }

    public void setRightIconColor(int color){
        this.rightIcon.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.MULTIPLY);
//        this.rightIcon.setColorFilter(color);
//        this.rightIcon.setBackgroundColor(color);

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
        ((GradientDrawable)background).setStroke(ctx.getResources().getDimensionPixelSize(R.dimen.button_border_width), ContextCompat.getColor(ctx, color));

    }

    public void setButtonBackgroundColor(int color){
        Drawable background = this.buttonContainer.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable)background;
        gradientDrawable.setColor(ContextCompat.getColor(ctx, color));
//        gradientDrawable.setColor(color);
    }

    public void setBorderColor(int color){
        Drawable background = this.buttonContainer.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable)background;
//        gradientDrawable.setStroke(ctx.getResources().getDimensionPixelSize(R.dimen.button_border_width), ContextCompat.getColor(getContext(), R.color.Red));
        gradientDrawable.setStroke(ctx.getResources().getDimensionPixelSize(R.dimen.button_border_width), ContextCompat.getColor(getContext(), color));

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
