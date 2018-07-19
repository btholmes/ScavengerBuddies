package ben.holmes.scavenger.buddies.Camera;

public interface CameraSupport {
    CameraSupport open(int cameraId);
    int getOrientation(int cameraId);
}