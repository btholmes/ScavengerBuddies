package ben.holmes.scavenger.buddies.Camera.Handlers;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.List;

import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import clarifai2.dto.prediction.Concept;

public class ImageHandler extends Handler{

    public static int STATE_COMPLETE = 1;
    private Context ctx;
    private Handler mHandler;
    private Clarifai clarifai;

    public ImageHandler(Context ctx){
        this.ctx = ctx;
        clarifai = new Clarifai(ctx);
        this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                byte[] image = (byte[])msg.obj;
                if(image != null)
                    getPredictions(image);

                return true;
            }
        });
    }

    public void handleState(byte[] image, int state){

        if(state == STATE_COMPLETE) {
            Message msg = mHandler.obtainMessage();
            msg.obj = image;
            msg.sendToTarget();
        }
    }


    private void getPredictions(byte[] byteArray){
//        clarifai.predictImageBitmap(byteArray, new Clarifai.ClarifiaResponse() {
//            @Override
//            public void onSuccess(List<Concept> result) {
////                Concept{id=ai_sTjX6dqC, name=abstract, createdAt=null, appID=main, value=0.99462897, language=null}
////                if(this != null)
////                    updateUI(result);
//                int a = 0;
//            }
//
//            @Override
//            public void onError(int error) {
//                int a = error;
//            }
//        });
        List<Concept> response = clarifai.predictInSync(byteArray);
        int a = 0;
    }


}
