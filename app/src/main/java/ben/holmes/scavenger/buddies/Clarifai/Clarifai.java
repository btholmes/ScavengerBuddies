package ben.holmes.scavenger.buddies.Clarifai;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ben.holmes.scavenger.buddies.R;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class Clarifai {

    private Context ctx;
    private ClarifaiClient client;

    public Clarifai(Context ctx){
        this.ctx = ctx;
        client = new ClarifaiBuilder(ctx.getResources().getString(R.string.clarifai_api_key)).buildSync();
    }

    private void createClientWithCustomHTTP(){
//        ClarifaiClient client = new ClarifaiBuilder(ctx.getResources().getString(R.string.clarifai_api_key))
//                .client(new OkHttpClient().Builder()
//                        .connectTimeout(60, TimeUnit.SECONDS)
//                        .readTimeout(60, TimeUnit.SECONDS)
//                        .writeTimeout(60, TimeUnit.SECONDS)
//                        .addInterceptor(new Interceptor.Chain(::info).setLevel(Interceptor.Level.BASIC))
//                        .build()
//                )
//                .buildSync();
    }


    public interface ClarifiaResponse{
        void onSuccess();
        void onError(int error);
    }


    public void predictImageBitmap(byte[] bitmap, final ClarifiaResponse callback){
        Model<Concept> generalModel = client.getDefaultModels().generalModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(ClarifaiImage.of(bitmap))
        );
        request.executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
            @Override
            public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                callback.onSuccess();
            }

            @Override
            public void onClarifaiResponseUnsuccessful(int errorCode) {
                callback.onError(errorCode);
            }

            @Override
            public void onClarifaiResponseNetworkError(IOException e) {

            }
        });
    }

    public void predictImageURL(String url, final ClarifiaResponse callback){
        Model<Concept> generalModel = client.getDefaultModels().generalModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(url)
        );
        request.executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
            @Override
            public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {

            }

            @Override
            public void onClarifaiResponseUnsuccessful(int errorCode) {
                callback.onError(errorCode);
            }

            @Override
            public void onClarifaiResponseNetworkError(IOException e) {

            }
        });
    }

    public void predicImageFile(File file, final ClarifiaResponse callback){
        Model<Concept> generalModel = client.getDefaultModels().generalModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(ClarifaiImage.of(file))
        );
       request.executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
            @Override
            public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {

            }

            @Override
            public void onClarifaiResponseUnsuccessful(int errorCode) {
                callback.onError(errorCode);
            }

            @Override
            public void onClarifaiResponseNetworkError(IOException e) {

            }
        });
    }


}


