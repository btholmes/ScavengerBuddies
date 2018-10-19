package ben.holmes.scavenger.buddies.Camera.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.App.Tools.InternetUtil;
import ben.holmes.scavenger.buddies.Camera.Handlers.ImageHandler;
import ben.holmes.scavenger.buddies.Camera.Model.CameraStateModel;
import ben.holmes.scavenger.buddies.Camera.Model.Prediction;
import ben.holmes.scavenger.buddies.Camera.TextureMovieEncoder;
import ben.holmes.scavenger.buddies.Camera.ThreadPool;
import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Games.Fragments.PlayFragment;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.Message;
import ben.holmes.scavenger.buddies.Model.SearchWord;
import ben.holmes.scavenger.buddies.R;
import clarifai2.dto.prediction.Concept;
import io.realm.Realm;
import pl.droidsonroids.gif.GifImageView;

public class Camera2Activity extends AppCompatActivity{

//    private ImageHandler imageHandler;
    private Context ctx;
    private View content;
    private String searchWord;
    private Game game;
    private Clarifai clarifai;
    private LinearLayout predictionBox;
    private TextureView textureView;
    // Camera filters; must match up with cameraFilterNames in strings.xml
    private Button takePictureButton;
    private Button tryAgainButton;
    private ProgressBar progressBar;

    private LinearLayout lookingForText;
    private TextView wordText;
    private GifImageView celebrateGif;

    /**
     * Used in OnResume and OnPause to restore, and save the current activity state.
     */
    private ArrayList<Pair<String, Float>> predictions;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSession;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private Handler imageHandler;
    private HandlerThread mBackgroudnThread;

    private static TextureMovieEncoder sVideoEncoder = new TextureMovieEncoder();

    private int mCameraPreviewWidth = 1280;
    private int mCameraPreviewHeight = 720;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_2_view);
        this.ctx = this;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            game = (Game)bundle.getSerializable(PlayFragment.GAME_KEY);
//            searchWord = bundle.getString(PlayFragment.SEARCH_WORD, null);
            searchWord = game.getCurrentWord();
        }


        content = findViewById(R.id.content);
        clarifai = new Clarifai(this);

        predictionBox = findViewById(R.id.prediciton_box);
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);

        takePictureButton = findViewById(R.id.btn_takepicture);
        tryAgainButton = findViewById(R.id.btn_try_again);
        progressBar = findViewById(R.id.progress_bar);

        lookingForText = findViewById(R.id.lookingForText);
        wordText = findViewById(R.id.wordText);
        celebrateGif = findViewById(R.id.celebrateGIF);

        setUpButtons();

    }

    /**f
     * Sets the text displayed at top of Camera activity. This is the word currently being sought
     * by the user.
     */
    private void setLookingForText(){
        lookingForText.setVisibility(View.VISIBLE);
        String word;
        if(game != null)
            word = game.getCurrentWord();
        else
            word = Realm.getDefaultInstance().where(SearchWord.class).equalTo("id", 0).findFirst().getWord();
        wordText.setText(word);
    }

    private void hideLookingForText(){
        lookingForText.setVisibility(View.GONE);
    }

    private void setCameraPreviewValues(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mCameraPreviewHeight = metrics.widthPixels;
        mCameraPreviewHeight = metrics.heightPixels;

    }

    private boolean isLoading;
    private void setUpButtons(){
        takePictureButton.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading){
                    isLoading = true;
                    progressBar.setVisibility(View.VISIBLE);
                    takePicture();
                }

            }
        });
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoading = false;
                if(!isFound()){
                    NotWordFoundOrTryAgainPressed = true;
                    deleteCameraStateModel();
                    showTakePictureButton();
                    updatePreview(false);
                }else{
                    /**
                     * This user just found a word, so update gameObj in firebase, then delete current stored state
                     * of CameraStateModel
                     */

                    deleteCameraStateModel();
                    saveWordFoundToFirebase();


//                    realm.executeTransactionAsync(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            realm.where(CameraStateModel.class).findAll().deleteAllFromRealm();
//                            saveWordFoundToFirebase();
//                        }
//                    });
                }
            }
        });
    }

    private void deleteCameraStateModel(){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CameraStateModel.class).findAll().deleteAllFromRealm();
            }
        });
    }

    /**
     * IF word was found then mimic a continue click to erase the current CameraStateModel, and
     * update game in Firbase
     */
    @Override
    public void onBackPressed() {
        NotWordFoundOrTryAgainPressed = true;
        if(isFound()){
            deleteCameraStateModel();
            saveWordFoundToFirebase();
        }

        super.onBackPressed();
    }

    /**
     * Updates the current game object notifying firebase that this person's turn is over,
     * and they have a given word, must also delete current state of CameraStateModel
     */
    private boolean NotWordFoundOrTryAgainPressed = false;
    private void saveWordFoundToFirebase(){
        NotWordFoundOrTryAgainPressed = true;
        Database database = Database.getInstance();

        /**
         * If true, means this user has just won the game, so go to main page
         */
        if(database.updateCurrentUserFoundWord(game)){
//            goToMain();
        }else{
            /**
             * Else user has just found a word, and has not won the game, so go back to PlayFragment, but
             * update page accordingly
             */
//            goToPlay();
        }

        goToMain();

    }

    private void goToPlay(){
        Intent intent = new Intent(Camera2Activity.this, NewGameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(game  != null)
            intent.putExtra(PlayFragment.GAME_KEY, game);
        startActivity(intent);
        finish();
    }

    private void goToMain(){
        Intent intent = new Intent(Camera2Activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showCongratulationsScreen(){
        celebrateGif.setVisibility(View.VISIBLE);
    }

    private void hideTakePictureButton(){
        hideLookingForText();
        takePictureButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.VISIBLE);
        if(isFound()){
            tryAgainButton.setText("CONTINUE");
            showCongratulationsScreen();
        }
        else
            tryAgainButton.setText("TRY AGAIN");

        predictionBox.setVisibility(View.VISIBLE);
        content.setVisibility(View.VISIBLE);
    }

    private void showTakePictureButton(){
        setLookingForText();
        takePictureButton.setVisibility(View.VISIBLE);
        predictionBox.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }


    /**
     * Goes through predictions returned from Clarifai api, and
     * adds them to the Prediction layout, while also adding a copy
     * to the predictions list.
     *
     * @param concepts result returned from Clarifai API call
     */
    private void updatePredictions(List<Pair<String, Float>> concepts){
        if(predictions == null)
            predictions = new ArrayList<>();
        predictions.clear();

        progressBar.setVisibility(View.GONE);
        predictionBox.setVisibility(View.VISIBLE);
        predictionBox.removeAllViews();
        for(int i = 0; i < concepts.size(); i++){
            String word = concepts.get(i).first;
            Float value = concepts.get(i).second;

            Prediction prediction = new Prediction(this);
            prediction.setPrediction(new Pair<String, Float>(word, value));
            predictions.add(new Pair<String, Float>(word, value));
            predictionBox.addView(prediction);
        }

        hideTakePictureButton();
    }

    public String getSearchWord(){
        return Realm.getDefaultInstance().where(SearchWord.class).equalTo("id",0).findFirst().getWord();
    }

    public void setFound(boolean found){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SearchWord.class).equalTo("id", 0).findFirst().setFound(found);
        realm.commitTransaction();
        realm.close();
    }

    public boolean isFound(){
        return Realm.getDefaultInstance().where(SearchWord.class).equalTo("id", 0).findFirst().isFound();
    }

    /**
     * Loops through all results, adds top 7, then continues looking for the matching word. If it is found, it is appended
     * at position 8.
     *
     * @param result
     * @return
     */
    private List<Pair<String, Float>> filterResult(List<Pair<String, Float>> result){
        ArrayList<Pair<String, Float>> copy = new ArrayList<>();

        for(Pair concept : result){
            if(((String)concept.first).equalsIgnoreCase("abstract")
                    || ((String)concept.first).equalsIgnoreCase("blur")
                    || ((String)concept.first).equalsIgnoreCase("no person")){
                continue;
            }else if(copy.size() < 7)
                copy.add(new Pair<String, Float>(((String)concept.first), ((Float)concept.second)));
            else{
                if(((String)concept.first).equalsIgnoreCase(getSearchWord())){
                    copy.add(new Pair<String, Float>(((String)concept.first), ((Float)concept.second)));
                    setFound(true);
                    break;
                }
            }
            if(((String)concept.first).equalsIgnoreCase(getSearchWord()))
                setFound(true);
        }
        return copy;
    }

    private void updateUI(final List<Pair<String, Float>> result){
        final List<Pair<String, Float>> copy = filterResult(result);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                updatePredictions(copy);
//                    if(isMatch(result)){
//                        showCongratulationsScreen();
//                    }else{
//                        hideTakePictureButton();
//                    }
            }
        });
    }


    private void showPredictions(byte[] byteArray){
        final Context ctx = this;
        try {
            clarifai.predictImageBitmap(byteArray, new Clarifai.ClarifiaResponse() {
                @Override
                public void onSuccess(List<Concept> result) {
//                Concept{id=ai_sTjX6dqC, name=abstract, createdAt=null, appID=main, value=0.99462897, language=null}
                    /**
                     * Have to sort through list and turn them into pairs, to provide consistency with restoring state
                     * from the onResume method, because updateUI needs to be called with list of Pair<String, Float>
                     */
                        List<Pair<String, Float>> items = new ArrayList<>();
                        for(Concept concept : result){
                            items.add(new Pair<String, Float>(concept.name(), concept.value()));
                        }
                        updateUI(items);
                }

                @Override
                public void onError(int error) {
                    int a = error;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    private boolean isMatch(List<Concept> concepts){
        for(int i = 0; i < 7; i++){
            String word = concepts.get(i).name();
            if(word.equals(searchWord))
                return true;
        }
        return false;
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            SurfaceTexture texture = surface;
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview(false);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

//    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
//        @Override
//        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//            super.onCaptureCompleted(session, request, result);
//            createCameraPreview();
//        }
//    };

    protected  void startBackgroundThread(){
        mBackgroudnThread = new HandlerThread("Camera Background");
        mBackgroudnThread.start();
        mBackgroundHandler = new Handler(mBackgroudnThread.getLooper());
        imageHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {

                byte[] bytes = (byte[]) msg.obj;
                showPredictions(bytes);
//                List<Concept> response = clarifai.predictInSync(bytes);
//                updateUI(res);
                int a = 0;
                return true;
            }
        });
    }

    protected void stopBackgroundThread(){
        if(mBackgroudnThread != null){
            mBackgroudnThread.quitSafely();
            try{
                mBackgroudnThread.join();
                mBackgroudnThread = null;
                mBackgroundHandler = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void showNoInternet(){
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        final ScavengerDialog dialog = new ScavengerDialog(this);
        dialog.hideHeader();
        dialog.showSingleOkButton();
        dialog.setBannerText("No Internet");
        dialog.setMessageText("There is no internet connection. Please " +
                "turn on mobile data, or connect to wifi in order to retrieve " +
                "image predictions.");
        dialog.setSingleOkButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getClarifaiResults(){
        progressBar.setVisibility(View.VISIBLE);
        Bitmap bitmap = textureView.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] byteArray = stream.toByteArray();
        bitmap.recycle();


        InternetUtil internetUtil = InternetUtil.getInstance(ctx);
        internetUtil.hasInternet(new InternetUtil.CallComplete() {
            @Override
            public void onComplete(Boolean result) {
                if(result)
                    showPredictions(byteArray);
                else{
                    progressBar.setVisibility(View.GONE);
                    showNoInternet();
                }
            }
        });
    }

    protected void takePicture(){
        if(cameraDevice == null){
            return;
        }
        createCameraPreview(true);
        getClarifaiResults();


//        final CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
//        try{
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
//            Size[] jpegSizes = null;
//            if(characteristics != null){
//                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
//            }
//            int width = mCameraPreviewWidth;
//            int height = mCameraPreviewHeight;
//            if(jpegSizes != null && jpegSizes.length > 0){
//                width = jpegSizes[0].getWidth();
//                height = jpegSizes[0].getHeight();
//            }
//
//            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
//            List<Surface> outputSurfaces = new ArrayList<>(2);
//            outputSurfaces.add(reader.getSurface());
//            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
//            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            captureBuilder.addTarget(reader.getSurface());
////            captureBuilder.addTarget(textureView.getSurfaceTexture());
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//
//            //Orientation
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));


//            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener(){
//                @Override
//                public void onImageAvailable(final ImageReader reader) {
//
//                    Bitmap bitmap = textureView.getBitmap();
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    bitmap.recycle();
//
//
//                    InternetUtil internetUtil = InternetUtil.getInstance(ctx);
//                    if(internetUtil.hasInternet())
//                        showPredictions(byteArray);
//                    else
//                        showNoInternet();
//
////                    List<Concept> response = clarifai.predictInSync(bytes);
////                    updateUI(response);
//
//
////                    android.os.Message msg = imageHandler.obtainMessage();
////                    msg.obj = bytes;
////                    msg.sendToTarget();
////                    stopBackgroundThread();
////                    imageHandler.handleState(bytes, ImageHandler.STATE_COMPLETE);
//
//
////                    showPredictions(bytes);
//
////                    Handler mHandler = new Handler(mBackgroudnThread.getLooper(), new Handler.Callback() {
////                        @Override
////                        public boolean handleMessage(android.os.Message msg) {
////                            ThreadPool.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Image image = reader.acquireLatestImage();
////                                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
////                                    final byte[] bytes = new byte[buffer.capacity()];
////                                    buffer.get(bytes);
////
////                                    showPredictions(bytes);
////                                }
////                            });
////                            return true;
////                        }
////                    });
//                }
//            };


//            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

//            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
//                @Override
//                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//                    super.onCaptureCompleted(session, request, result);
//                    createCameraPreview(true);
//                }
//            };
//
//            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession session) {
//                    try{
//                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
//                    }catch (Exception e){
//                    }
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//
//                }
//            }, mBackgroundHandler);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void stopRepeating(){
        try {
            cameraCaptureSession.stopRepeating();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected void createCameraPreview(final boolean pause){
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
//            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            texture.setDefaultBufferSize(mCameraPreviewWidth, mCameraPreviewHeight);
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if(cameraDevice == null)
                        return;

                    cameraCaptureSession = session;
                    updatePreview(pause);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);

        }catch (Exception e){

        }
    }

    private void openCamera(){
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Add permission
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Camera2Activity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);

        }catch (Exception e){

        }
    }

    protected void updatePreview(boolean pause){
        if(cameraDevice == null){
            Log.e("Error", "update preview");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try{
            if(pause)
                cameraCaptureSession.stopRepeating();
            else
                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeCamera(){
        if(cameraDevice != null){
            cameraDevice.close();
            cameraDevice = null;
        }
        if(imageReader != null){
            imageReader.close();
            imageReader = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //close
                finish();
            }
            openCamera();
        }
    }

    private void setSearchWord(){
        searchWord = Realm.getDefaultInstance().where(SearchWord.class).equalTo("id", 0).findFirst().getWord();
    }


    /**
     * This is default method for setting up Camera in Onresume, this method is used when there is
     * no saved CameraStateModel in realm.
     */
    private void normalOnResume(){
        showTakePictureButton();
        imageHandler = new ImageHandler(this);
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    /**
     * If a previous state exists, that means an attempt has been made to take a picture of the
     * current word, but user has not clicked continue, or try again buttons.
     *
     * In the case of attempting to match a picture, 1 of 2 things happens
     *
     * 1.) Picture was matched
     *      In this case no need to set up camera, so normalOnResume() is not called
     *
     * 2.) Picture was not matched
     *      In this case, user may want to take another attempt, so the camera is set up,
     *      and normalOnResume() is called
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        CameraStateModel previousState = getPreviousState();
        if (previousState != null){
            if(!previousState.isFoundWord())
                normalOnResume();

            searchWord = previousState.getCurrentWord();
            restoreState(previousState);
        }
        else{
            normalOnResume();
            setSearchWord();
        }
    }

    /**
     * Just restore the state to whatever was on screen before user pressed the overview button
     * @param model
     */
    private void restoreState(CameraStateModel model){
        List<Pair<String, Float>> predictionsCopy = new ArrayList<>();

        List<String> words = model.getPredictionWords();
        List<Float> percentages = model.getPredictionPercentages();

        if(words != null && !words.isEmpty()){
            for(int i = 0; i < words.size(); i++){
                Pair pair = new Pair(words.get(i), percentages.get(i));
                predictionsCopy.add(pair);
            }
        }
        if(!predictionsCopy.isEmpty())
            updatePredictions(predictionsCopy);

        if(model.isFoundWord()){
            setFound(true);
            hideTakePictureButton();
            showCongratulationsScreen();
            tryAgainButton.setText("Continue");
        }
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        if(!NotWordFoundOrTryAgainPressed)
            savePageState();
        else
            deleteCameraStateModel();
        super.onPause();
    }

    private CameraStateModel getPreviousState(){
        CameraStateModel cameraStateModel = null;
        Realm realm = Realm.getDefaultInstance();

        if(realm.where(CameraStateModel.class).equalTo("id", 0).count() > 0)
            cameraStateModel = realm.where(CameraStateModel.class).equalTo("id", 0).findFirst();

        return cameraStateModel;
    }

    private void savePageState(){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CameraStateModel cameraStateModel;
                if(realm.where(CameraStateModel.class).equalTo("id", 0).count() <= 0){
                    cameraStateModel = realm.createObject(CameraStateModel.class, 0);
                }else
                    cameraStateModel = realm.where(CameraStateModel.class).equalTo("id", 0).findFirst();
                if(game != null){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    cameraStateModel.setData(game, user.getUid(), predictions, isFound());
                }
            }
        });

    }


}
