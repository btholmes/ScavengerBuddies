package ben.holmes.scavenger.buddies.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;

public class FriendItem extends RelativeLayout{

    private Context ctx;
    private View root;
    private ImageView image;
    private TextView name;
    private TextView subtitle;

    public FriendItem(Context ctx){
        super(ctx);
        SharedConstructor(ctx);
    }

    public FriendItem(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        SharedConstructor(ctx);
    }

    public void SharedConstructor(Context ctx){
        this.ctx = ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.item_friend, this);
        image = root.findViewById(R.id.image);
        name = root.findViewById(R.id.name);
        subtitle = root.findViewById(R.id.subtitle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(int res) {
        this.image.setImageResource(res);
    }

    public TextView getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public TextView getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle.setText(subtitle);
    }


}
