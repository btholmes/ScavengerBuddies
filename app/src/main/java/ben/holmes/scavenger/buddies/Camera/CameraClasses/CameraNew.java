package ben.holmes.scavenger.buddies.Camera.CameraClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import ben.holmes.scavenger.buddies.App.Tools.CheckPermissions;
import ben.holmes.scavenger.buddies.Camera.CameraSupport;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraNew implements CameraSupport {

    private CameraDevice camera;
    private CameraManager manager;
    private CheckPermissions checkPermissions;

    public CameraNew(final Context context) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public CameraSupport open(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            if(checkPermissions.hasPermission(CheckPermissions.CAMERA_PERMISSION)){
                manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        CameraNew.this.camera = camera;
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {
                        CameraNew.this.camera = camera;
                        // TODO handle
                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {
                        CameraNew.this.camera = camera;
                        // TODO handle
                    }
                }, null);
            }else{



            }

        } catch (Exception e) {
            // TODO handle
        }
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (Exception e) {
            // TODO handle
            return 0;
        }
    }
}
