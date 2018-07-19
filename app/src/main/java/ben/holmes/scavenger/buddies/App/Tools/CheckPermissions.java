package ben.holmes.scavenger.buddies.App.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class CheckPermissions {

    public static String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static int CAMERA_REQUEST_CODE = 99;

    private Context ctx;

    public CheckPermissions(Context ctx){
        this.ctx = ctx;
    }


    public boolean hasPermission(String permission){
        if (ContextCompat.checkSelfPermission((Activity)ctx, permission)
                != ctx.getPackageManager().PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return  true;
    }

    public void requestPermission(String permission, int requestCode){
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ctx,
                permission)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity)ctx,
                    new String[]{permission},
                    requestCode);

        }
    }






}
