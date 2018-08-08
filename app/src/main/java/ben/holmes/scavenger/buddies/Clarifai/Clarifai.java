package ben.holmes.scavenger.buddies.Clarifai;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ben.holmes.scavenger.buddies.R;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
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
        void onSuccess(List<Concept> result);
        void onError(int error);
    }


    public void predictImageBitmap(byte[] bitmap, final ClarifiaResponse callback){
        Model<Concept> generalModel = client.getDefaultModels().generalModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(ClarifaiImage.of(bitmap))
        );

        request.executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
            @Override
            public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> outputs) {
                ClarifaiOutput<Concept> concept = outputs.get(0);
                List<Concept> result = new ArrayList<>(concept.data());
                callback.onSuccess(result);
            }

            @Override
            public void onClarifaiResponseUnsuccessful(int errorCode) {
                callback.onError(errorCode);
            }

            @Override
            public void onClarifaiResponseNetworkError(IOException e) {
                IOException a = e;
            }
        });

//
//        new AsyncTask<byte[], Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
//
//            // Model prediction
//            @Override
//            protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(byte[]... bitmaps) {
////                ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
////                byte[] byteArray = stream.toByteArray();
//                byte[] byteArray = bitmaps[0];
//                final ConceptModel general = client.getDefaultModels().generalModel();
//                return general.predict()
//                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
//                        .executeSync();
//            }
//
//            /*
//             * Handling API response and then collecting and printing tags
//             */
//            @Override
//            protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
//                final List<ClarifaiOutput<Concept>> predictions = response.get();
//                ClarifaiOutput<Concept> concept = predictions.get(0);
//                List<Concept> result = new ArrayList<>(concept.data());
//                callback.onSuccess(result);
////                if (!response.isSuccessful()) {
////                    Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////                final List<ClarifaiOutput<Concept>> predictions = response.get();
////                if (predictions.isEmpty()) {
////                    Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                final List<Concept> predictedTags = predictions.get(0).data();
////                for (int i = 0; i < predictedTags.size(); i++) {
////                    tags.add(predictedTags.get(i).name());
////                }
////                printTags();
////                checkMatch();
//            }
//        }.execute(bitmap);


//        new ByteArrayAsync().execute(bitmap);
    }


     public class ByteArrayAsync extends AsyncTask<byte[], Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>{

        // Model prediction
        @Override
        protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(byte[]... bytes) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
//            byte[] byteArray = stream.toByteArray();
            byte[] array =  bytes[0];
            final ConceptModel general = client.getDefaultModels().generalModel();
            return general.predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(array)))
                    .executeSync();
        }

        /*
         * Handling API response and then collecting and printing tags
         */
        @Override
        protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
            if (!response.isSuccessful()) {
//                Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
                return;
            }
            final List<ClarifaiOutput<Concept>> predictions = response.get();
            if (predictions.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
                return;
            }

            final List<Concept> predictedTags = predictions.get(0).data();
            for (int i = 0; i < predictedTags.size(); i++) {
//                tags.add(predictedTags.get(i).name());
            }
//            printTags();
//            checkMatch();
        }
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


