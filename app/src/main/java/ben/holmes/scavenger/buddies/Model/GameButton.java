package ben.holmes.scavenger.buddies.Model;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ben.holmes.scavenger.buddies.R;

public class GameButton extends RelativeLayout{

    private Context ctx;
    private View root;

    public GameButton(Context ctx){
        super(ctx);
        sharedConstructor(ctx);

    }

    public GameButton(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx);
    }

    private void sharedConstructor(Context ctx){
        this.ctx = ctx;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        root = inflater.inflate(R.layout.button_new_game, null);
        inflate(ctx, R.layout.button_new_game,  this);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}


