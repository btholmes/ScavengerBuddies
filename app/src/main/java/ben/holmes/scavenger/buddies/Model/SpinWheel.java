package ben.holmes.scavenger.buddies.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import ben.holmes.scavenger.buddies.R;

public class SpinWheel extends View {

    private Context ctx;
    private int dividers;
    private Canvas canvas;
    private Paint p;
    private int sweepAngle;

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
            default:
                break;
        }
        return color;
    }

    private Drawable getDrawable(int place){
        Drawable result = null;
        switch (place){
            case 0:
                result = ContextCompat.getDrawable(getContext(), R.drawable.icon3);
                break;
            case 1:
                result = ContextCompat.getDrawable(getContext(), R.drawable.icon1);
                break;
            case 2:
                result = ContextCompat.getDrawable(getContext(), R.drawable.icon2);
                break;
            case 3:
                result = ContextCompat.getDrawable(getContext(), R.drawable.icon4);
                break;
            case 4:
                result = ContextCompat.getDrawable(getContext(), R.drawable.icon5);
                break;
            case 5:
                result = ContextCompat.getDrawable(getContext(), R.drawable.ic_six);
                break;
            case 6:
                result = ContextCompat.getDrawable(getContext(), R.drawable.ic_six);
                break;
            case 7:
                result = ContextCompat.getDrawable(getContext(), R.drawable.ic_six);
                break;
            case 8:
                result = ContextCompat.getDrawable(getContext(), R.drawable.ic_six);
                break;
            case 9:
                result = ContextCompat.getDrawable(getContext(), R.drawable.ic_six);
                break;
            default:
                break;

        }
        return result;
    }

    /**
     * Rotates drawable through a matrix
     * @param d
     * @param rotation
     * @param cX
     * @param cY
     */
    private void rotateDrawable( Drawable d, int rotation, int cX, int cY){
        Matrix matrix = new Matrix();
        Rect bounds = d.getBounds();
        int width = (int)Math.abs(bounds.right -bounds.left);
        int height = (int)Math.abs(bounds.top -bounds.bottom);
        rotation += 90;

//        Rotation occurs in bitmap space
        matrix.postRotate(rotation, width/2,height/2);
//        Translate to position on canvas
        matrix.postTranslate(cX-width/2, cY-height/2);
        Bitmap bitmap = drawableToBitmap(d);
        bitmap = Bitmap.createScaledBitmap(bitmap, width,  height, true);

        canvas.drawBitmap(bitmap, matrix, null);
    }

    private void setDrawable(int i, int startAngle){
        Drawable d = getDrawable(i);
        int width = 100;
        int[] arcMidPoint = getCenterOfSegment( startAngle + (sweepAngle/2), getWidth()/3);
        d.setBounds(arcMidPoint[0] - width/2,
                arcMidPoint[1] - width/2,
                arcMidPoint[0]  + width/2,
                arcMidPoint[1] + width/2);

        rotateDrawable(d, startAngle + sweepAngle/2, d.getBounds().centerX(), d.getBounds().centerY());
    }

    /**
     * Converts from polar coordinates to cartesian coordinates
     * @param angle   the angle of the pie segment
     * @param r  the radius, usually getWidth/4
     * @return
     */
    private int[] getCenterOfSegment(int angle, int r){
        int[] result = new int[2];
        int cX = getWidth()/2;
        int cY = getHeight()/2;

        //Convert angle to radians
        double trad = angle * (Math.PI/180);

        result[0] = cX + (int)(r * Math.cos(trad));
        result[1] = cY + (int)(r * Math.sin(trad));

        return result;
    }

    private void setBackground(Canvas canvas){
        Drawable d = getResources().getDrawable(R.drawable.spin_wheel_background);
        d.setBounds(0, 0, getWidth(), getHeight());
        d.draw(canvas);
    }

    private void drawSegments(RectF rectF, Paint p){

        sweepAngle = 360/dividers;
        int startAngle = sweepAngle;
        for(int i = 0; i < dividers; i++){
            int angle = startAngle * i;
            p.setColor(ContextCompat.getColor(getContext(), getColor(i)));
            canvas.drawArc(rectF, angle, sweepAngle, true, p);
            setDrawable(i, angle);
        }
    }

    public int getDividers(){
        return  dividers;
    }

    public int getSweepAngle(){
        return sweepAngle;
    }

    public void fillSegment(int angle){
        invalidate();
        p.setStyle(Paint.Style.FILL);
        angle = angle % 360;
        int place = angle-sweepAngle;
        p.setColor(ContextCompat.getColor(getContext(), getColor(place)));
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(rectF, angle, sweepAngle, true, p);
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


    @Override
    protected void onDraw(Canvas canvasA) {

//        setBackground(canvas);

        this.canvas = canvasA;
        p = new Paint();

        this.p = p;
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

        drawSegments(rectF, p);
//        addArrow(canvas);
    }


    private void addArrow(Canvas canvas){
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_up_arrow);
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;

        int spinWheelWidth = getResources().getDimensionPixelSize(R.dimen.spin_wheel_center_width);

        d.setBounds(centerX-50,
                centerY+spinWheelWidth+50,
                centerX+50,
                centerY);

        d.draw(canvas);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        Rect bounds = drawable.getBounds();

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                bitmap = bitmapDrawable.getBitmap();
                return bitmapDrawable.getBitmap();
            }
        }

//        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
//            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
//        } else {
//            bitmap = Bitmap.createBitmap(Math.abs(bounds.left - bounds.right), Math.abs(bounds.top - bounds.bottom), Bitmap.Config.ARGB_8888);
//        }
//
//        bitmap = Bitmap.createBitmap(Math.abs(bounds.left - bounds.right), Math.abs(bounds.top - bounds.bottom), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
//        drawable.draw(canvas);
        return bitmap;
    }
}
