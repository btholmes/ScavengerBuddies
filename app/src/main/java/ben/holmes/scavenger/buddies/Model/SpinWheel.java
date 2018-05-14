package ben.holmes.scavenger.buddies.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import ben.holmes.scavenger.buddies.R;

public class SpinWheel extends View {

    private Context ctx;
    private int dividers;

    // CONSTRUCTOR
    public SpinWheel(Context context) {
        super(context);
        this.ctx = ctx;
        setFocusable(true);

    }

    public SpinWheel(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
    }

    public SpinWheel(Context ctx, AttributeSet attrs, int style){
        super(ctx, attrs, style);
    }

    public void setDividers(int dividers){
        this.dividers = dividers;
    }


    private int getColor(int place){
        int color = R.color.black;
        switch (place){
            case 0:
                color = R.color.Red;
                break;
            case 1:
                color = R.color.Orange;
                break;
            case 2:
                color = R.color.LightGreen;
                break;
            case 3:
                color = R.color.Yellow;
                break;
            case 4:
                color = R.color.Pink;
                break;
            case 5:
                color = R.color.Purple;
                break;
            case 6:
                color = R.color.Blue;
                break;
            case 7:
                color = R.color.Lavender;
                break;
            case 8:
                color = R.color.Aquamarine;
                 break;
            case 9:
                color = R.color.DarkGray;
                break;
        }
        return color;
    }

    private Drawable getDrawable(int place){
        return ContextCompat.getDrawable(getContext(), R.drawable.check_mark);
    }

    private void setDrawable(Canvas canvas, int i, int sweepAngle, int startAngle){
        Drawable d = getDrawable(i);
        int[] midPoint = midPointOfPieSegment(sweepAngle);
        int[] arcMidPoint = getMidPointOfArc(midPoint[0], midPoint[1], startAngle);
        d.setBounds(arcMidPoint[0] - d.getIntrinsicWidth()/2,
                arcMidPoint[1]-d.getIntrinsicHeight()/2,
                arcMidPoint[0]+ d.getIntrinsicWidth()/2,
                arcMidPoint[1] + d.getIntrinsicHeight()/2);

//        canvas.rotate(sweepAngle * i);
        d.draw(canvas);
//        canvas.rotate(-sweepAngle * i);


    }

    private void setBackground(Canvas canvas){
        Drawable d = getResources().getDrawable(R.drawable.spin_wheel_background);
        d.setBounds(0, 0, getWidth(), getHeight());
        d.draw(canvas);
    }

    private void drawSegments(RectF rectF, Canvas canvas, Paint p){
        //        Segment border color
        p.setStyle(Paint.Style.FILL);
        int sweepAngle = 360/dividers;
        int startAngle = sweepAngle;
        for(int i = 0; i < dividers; i++){
            int angle = startAngle * i;
            p.setColor(ContextCompat.getColor(getContext(), getColor(i)));
            canvas.drawArc (rectF, angle, sweepAngle, true, p);
            setDrawable(canvas, i, sweepAngle, angle);
        }
    }

    private float calculateRadius(){
        float width = getWidth()/2;
        float height = getHeight()/2;
        if(width < height){
            return width;
        }else{
            return height;
        }
    }

    private int[] midPointOfPieSegment(float sweepAngle){
        int[] result = new int[2];
        result[0] = (int)((calculateRadius()/2)*Math.cos(Math.toRadians(sweepAngle/2))+(getWidth()/2));
        result[1] = (int)((calculateRadius()/2)*Math.sin(Math.toRadians(sweepAngle/2))+(getHeight()/2));

        return result;
    }

    private int[] getMidPointOfArc(int cX,int cY, int theta)
    {
        int[] result = new int[2];
        float rX = (float)(cX + calculateRadius()*(Math.cos(Math.PI/theta)));
        float rY = (float)(cY + calculateRadius()*(Math.sin(Math.PI/theta)));

        result[0] = (int)(rX+cX)/2;
        result[1] = (int)(rY+cY)/2;

        return  result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setBackground(canvas);

        Paint p = new Paint();
        // smooths
        p.setAntiAlias(true);

//        Border color
        p.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        // opacity
        //p.setAlpha(0x80); //

        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawOval(rectF, p);

        drawSegments(rectF, canvas, p);
    }
}
