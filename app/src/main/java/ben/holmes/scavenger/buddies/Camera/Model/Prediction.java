package ben.holmes.scavenger.buddies.Camera.Model;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;
import clarifai2.dto.prediction.Concept;

public class Prediction extends RelativeLayout {
    private Context ctx;
    private View rootView;
    private TextView text;
    private TextView percent;
    private View progress;

    public Prediction(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }


    public Prediction(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);
    }

    private void sharedConstructor(Context ctx, AttributeSet attrs){
        this.ctx = ctx;
        rootView = LayoutInflater.from(ctx).inflate(R.layout.view_prediction, this);
        percent = rootView.findViewById(R.id.percent);
        text = rootView.findViewById(R.id.text);
        progress = rootView.findViewById(R.id.progress);
    }

    public void setPercent(float value){
        value *= 100;
        value = Math.round(value); 
        percent.setText(value + "%");
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public void setProgress(float percent){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) progress.getLayoutParams();
        int width =(int)(params.width * percent);
        params.width = width;
        progress.setLayoutParams(params);
    }

    public void setPrediction(Concept concept){
        setText(concept.name());
        setPercent(concept.value());
        setProgress(concept.value());
    }

}
