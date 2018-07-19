package ben.holmes.scavenger.buddies.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;

import ben.holmes.scavenger.buddies.App.Tools.CheckPermissions;
import ben.holmes.scavenger.buddies.Model.Preview;

public class CameraHelper {

    private Context ctx;
    private Activity activity;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public Camera oldCamera;
    public Preview cameraPreview;

    public CameraHelper(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        cameraPreview = new Preview(ctx);
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ctx.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void openCamera(int id){
        OpenCamera open = new OpenCamera();
        open.execute(id);
    }


    /**
     * Open camera with async task so it doesn't "bog down" the UI thread
     */
    public class OpenCamera extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... ids) {

            //Represent
            int id = ids[0];
//            if(Build.VERSION.SDK_INT >= 21){
//                openNewCamera(id);
//            }else{
//                openOldCamera(id);
//            }
            openOldCamera(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void openNewCamera(int id){

    }

    private void openOldCamera(int id){
        CheckPermissions permissions = new CheckPermissions(ctx);
        if(permissions.hasPermission(CheckPermissions.CAMERA_PERMISSION)){
            if(safeCameraOpen(id)){
                cameraPreview.setCamera(oldCamera);
            }
        }else{
            permissions.requestPermission(CheckPermissions.CAMERA_PERMISSION, CheckPermissions.CAMERA_REQUEST_CODE);
        }

    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            oldCamera = Camera.open();
            qOpened = (oldCamera != null);
        } catch (Exception e) {
            Log.e( "Open Camera error", "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        cameraPreview.setCamera(null);
        if (cameraPreview.getCamera() != null) {
            cameraPreview.getCamera().release();
            cameraPreview.setCamera(null);
        }
    }




}
