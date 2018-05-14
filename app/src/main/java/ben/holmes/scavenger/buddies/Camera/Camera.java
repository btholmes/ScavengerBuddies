package ben.holmes.scavenger.buddies.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class Camera {

    private Context ctx;
    private Activity activity;
    public static final int REQUEST_IMAGE_CAPTURE = 1;


    public Camera(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
