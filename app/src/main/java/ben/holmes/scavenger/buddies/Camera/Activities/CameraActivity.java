package ben.holmes.scavenger.buddies.Camera.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ben.holmes.scavenger.buddies.Camera.CameraUtils;
import ben.holmes.scavenger.buddies.Camera.Renderer;
import ben.holmes.scavenger.buddies.Camera.TextureMovieEncoder;
import ben.holmes.scavenger.buddies.Camera.gles.FullFrameRect;
import ben.holmes.scavenger.buddies.Camera.gles.Texture2dProgram;
import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import ben.holmes.scavenger.buddies.Games.Fragments.PlayFragment;
import ben.holmes.scavenger.buddies.R;
import clarifai2.dto.prediction.Concept;

public class CameraActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener{

    private Clarifai clarifai;
    private TextView predictionBox;
    private boolean takePicture = false;
    private boolean updating = false;
    // Camera filters; must match up with cameraFilterNames in strings.xml
    static final int FILTER_NONE = 0;
    static final int FILTER_BLACK_WHITE = 1;
    static final int FILTER_BLUR = 2;
    static final int FILTER_SHARPEN = 3;
    static final int FILTER_EDGE_DETECT = 4;
    static final int FILTER_EMBOSS = 5;

    private Button takePictureButton;
    private Button tryAgainButton;
    private GLSurfaceView mGLView;
    private CameraSurfaceRenderer mRenderer;
    private android.hardware.Camera mCamera;
    private CameraHandler mCameraHandler;
    private boolean mRecordingEnabled;

    public static volatile boolean sReleaseInCallback = true;

    private Renderer renderer;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
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
    private HandlerThread mBackgroudnThread;

    private static TextureMovieEncoder sVideoEncoder = new TextureMovieEncoder();

    private ProgressBar progressBar;

    private int mCameraPreviewWidth = 1280;
    private int mCameraPreviewHeight = 720;

    private String searchWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            searchWord = bundle.getString(PlayFragment.SEARCH_WORD, null);
        }

        predictionBox = findViewById(R.id.prediciton_box);

        File outputFile = new File(getFilesDir(), "camera-test.mp4");
//        TextView fileText = (TextView) findViewById(R.id.cameraOutputFile_text);
//        fileText.setText(outputFile.toString());

        mGLView = findViewById(R.id.cameraPreview_surfaceView);

        // Define a handler that receives camera-control messages from other threads.  All calls
        // to Camera must be made on the same thread.  Note we create this before the renderer
        // thread, so we know the fully-constructed object will be visible.
        mCameraHandler = new CameraHandler(this);

        mRecordingEnabled = false;

        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL context.
        mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        mRenderer = new CameraSurfaceRenderer(mCameraHandler, sVideoEncoder, outputFile);
        mGLView.setRenderer(mRenderer);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        takePictureButton = findViewById(R.id.btn_takepicture);
        tryAgainButton = findViewById(R.id.btn_try_again);

        setUpButtons();

    }

    private void setCameraPreviewValues(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mCameraPreviewHeight = metrics.widthPixels;
        mCameraPreviewHeight = metrics.heightPixels;

    }

    private void setUpButtons(){
        takePictureButton.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture = true;
            }
        });
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTakePictureButton();
                mGLView.onResume();
                takePicture = false;
            }
        });
    }


    private void hideTakePictureButton(){
        takePictureButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.VISIBLE);
        predictionBox.setVisibility(View.VISIBLE);
    }

    private void showTakePictureButton(){
        takePictureButton.setVisibility(View.VISIBLE);
        predictionBox.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
    }


    private void updatePage(final Bitmap bitmap){
        if(takePicture){
            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
//            showPredictions(bitmap);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showPredictions(bitmap);
                }
            });
        }
    }

    private void updatePredictions(List<Concept> concepts){
        predictionBox.setVisibility(View.VISIBLE);
        predictionBox.setText("");
        for(int i = 0; i < 7; i++){
            String word = concepts.get(i).name();
            predictionBox.setText(predictionBox.getText().toString()+  ", " + word);
        }
    }

    private void showPredictions(Bitmap bitmap){
        try {
            byte[] byteArray = getBytes(bitmap);
            clarifai.predictImageBitmap(byteArray, new Clarifai.ClarifiaResponse() {
                @Override
                public void onSuccess(List<Concept> result) {
//                Concept{id=ai_sTjX6dqC, name=abstract, createdAt=null, appID=main, value=0.99462897, language=null}
                    progressBar.setVisibility(View.GONE);
                    updatePredictions(result);

                    if(isMatch(result)){
                        showCongratulationsScreen();
                    }else{
                        hideTakePictureButton();
                    }
                    waitingOnResponse = false;
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

    private void showCongratulationsScreen(){

    }


    private boolean isMatch(List<Concept> concepts){
        for(int i = 0; i < 7; i++){
            String word = concepts.get(i).name();
            if(word.equals(searchWord))
                return true;
        }
        return false;
    }

    private boolean waitingOnResponse = false;
    private void getPredictions(Bitmap bitmap) throws Exception{
        if(!waitingOnResponse){
            waitingOnResponse = true;
//            showPredictions(bitmap);
        }
    }

    private byte[] getBytes(Bitmap bitmap) throws Exception{
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        stream.close();
        return byteArray;
    }


    /**
     * Opens a camera, and attempts to establish preview mode at the specified width and height.
     * <p>
     * Sets mCameraPreviewWidth and mCameraPreviewHeight to the actual width/height of the preview.
     */
    private void openCamera(int desiredWidth, int desiredHeight) {
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized");
        }

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int numCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = android.hardware.Camera.open(i);
                break;
            }
        }
        if (mCamera == null) {
//            Log.d(TAG, "No front-facing camera found; opening default");
            mCamera = android.hardware.Camera.open();    // opens first back-facing camera
        }
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }

        android.hardware.Camera.Parameters parms = mCamera.getParameters();

        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        // leave the frame rate set to default
        mCamera.setParameters(parms);

        int[] fpsRange = new int[2];
        android.hardware.Camera.Size mCameraPreviewSize = parms.getPreviewSize();
        parms.getPreviewFpsRange(fpsRange);
        String previewFacts = mCameraPreviewSize.width + "x" + mCameraPreviewSize.height;
        if (fpsRange[0] == fpsRange[1]) {
            previewFacts += " @" + (fpsRange[0] / 1000.0) + "fps";
        } else {
            previewFacts += " @[" + (fpsRange[0] / 1000.0) +
                    " - " + (fpsRange[1] / 1000.0) + "] fps";
        }
//        TextView text = (TextView) findViewById(R.id.cameraParams_text);
//        text.setText(previewFacts);

        mCameraPreviewWidth = mCameraPreviewSize.width;
        mCameraPreviewHeight = mCameraPreviewSize.height;


//        AspectFrameLayout layout = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
//            layout.setAspectRatio((double) mCameraPreviewHeight / mCameraPreviewWidth);
        } else if(display.getRotation() == Surface.ROTATION_270) {
//            layout.setAspectRatio((double) mCameraPreviewHeight/ mCameraPreviewWidth);
            mCamera.setDisplayOrientation(180);
        } else {
            // Set the preview aspect ratio.
//            layout.setAspectRatio((double) mCameraPreviewWidth / mCameraPreviewHeight);
        }
    }

    /**
     * Stops camera preview, and releases the camera to the system.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        setCameraPreviewValues();
        clarifai = new Clarifai(this);

//        updateControls();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (mCamera == null) {
                openCamera(1280, 720);      // updates mCameraPreviewWidth/Height
            }
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }

        mGLView.onResume();
        mGLView.queueEvent(new Runnable() {
            @Override public void run() {
                mRenderer.setCameraPreviewSize(mCameraPreviewWidth, mCameraPreviewHeight);
            }
        });
    }

    @Override
    protected void onPause() {
//        Log.d(TAG, "onPause -- releasing camera");
        super.onPause();
        releaseCamera();
        mGLView.queueEvent(new Runnable() {
            @Override public void run() {
                // Tell the renderer that it's about to be paused so it can clean up.
                mRenderer.notifyPausing();
            }
        });
        mGLView.onPause();
//        Log.d(TAG, "onPause complete");
    }

    @Override
    protected void onDestroy() {
//        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mCameraHandler.invalidateHandler();     // paranoia
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //close
                finish();
            }else{
                openCamera(mCameraPreviewWidth, mCameraPreviewHeight);
            }
        }
    }



    /**
     * Renderer object for our GLSurfaceView.
     * <p>
     * Do not call any methods here directly from another thread -- use the
     * GLSurfaceView#queueEvent() call.
     */
    class CameraSurfaceRenderer implements GLSurfaceView.Renderer {
        private static final String TAG = "Camera renderer";
        private static final boolean VERBOSE = false;

        private static final int RECORDING_OFF = 0;
        private static final int RECORDING_ON = 1;
        private static final int RECORDING_RESUMED = 2;

        private CameraActivity.CameraHandler mCameraHandler;
        private TextureMovieEncoder mVideoEncoder;
        private File mOutputFile;

        private FullFrameRect mFullScreen;

        private final float[] mSTMatrix = new float[16];
        private int mTextureId;

        private SurfaceTexture mSurfaceTexture;
        private boolean mRecordingEnabled;
        private int mRecordingStatus;
        private int mFrameCount;

        // width/height of the incoming camera preview frames
        private boolean mIncomingSizeUpdated;
        private int mIncomingWidth;
        private int mIncomingHeight;

        private int mCurrentFilter;
        private int mNewFilter;


        /**
         * Constructs CameraSurfaceRenderer.
         * <p>
         * @param cameraHandler Handler for communicating with UI thread
         * @param movieEncoder video encoder object
         * @param outputFile output file for encoded video; forwarded to movieEncoder
         */
        public CameraSurfaceRenderer(CameraActivity.CameraHandler cameraHandler,
                                     TextureMovieEncoder movieEncoder, File outputFile) {
            mCameraHandler = cameraHandler;
            mVideoEncoder = movieEncoder;
            mOutputFile = outputFile;

            mTextureId = -1;

            mRecordingStatus = -1;
            mRecordingEnabled = false;
            mFrameCount = -1;

            mIncomingSizeUpdated = false;
            mIncomingWidth = mIncomingHeight = -1;

            // We could preserve the old filter mode, but currently not bothering.
            mCurrentFilter = -1;
            mNewFilter = CameraActivity.FILTER_NONE;
        }

        /**
         * Notifies the renderer thread that the activity is pausing.
         * <p>
         * For best results, call this *after* disabling Camera preview.
         */
        public void notifyPausing() {
            if (mSurfaceTexture != null) {
                Log.d(TAG, "renderer pausing -- releasing SurfaceTexture");
                mSurfaceTexture.release();
                mSurfaceTexture = null;
            }
            if (mFullScreen != null) {
                mFullScreen.release(false);     // assume the GLSurfaceView EGL context is about
                mFullScreen = null;             //  to be destroyed
            }
            mIncomingWidth = mIncomingHeight = -1;
        }

        /**
         * Notifies the renderer that we want to stop or start recording.
         */
        public void changeRecordingState(boolean isRecording) {
            Log.d(TAG, "changeRecordingState: was " + mRecordingEnabled + " now " + isRecording);
            mRecordingEnabled = isRecording;
        }

        /**
         * Changes the filter that we're applying to the camera preview.
         */
        public void changeFilterMode(int filter) {
            mNewFilter = filter;
        }

        /**
         * Updates the filter program.
         */
        public void updateFilter() {
            Texture2dProgram.ProgramType programType;
            float[] kernel = null;
            float colorAdj = 0.0f;

            Log.d(TAG, "Updating filter to " + mNewFilter);
            switch (mNewFilter) {
                case CameraActivity.FILTER_NONE:
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT;
                    break;
                case CameraActivity.FILTER_BLACK_WHITE:
                    // (In a previous version the TEXTURE_EXT_BW variant was enabled by a flag called
                    // ROSE_COLORED_GLASSES, because the shader set the red channel to the B&W color
                    // and green/blue to zero.)
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT_BW;
                    break;
                case CameraActivity.FILTER_BLUR:
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT_FILT;
                    kernel = new float[] {
                            1f/16f, 2f/16f, 1f/16f,
                            2f/16f, 4f/16f, 2f/16f,
                            1f/16f, 2f/16f, 1f/16f };
                    break;
                case CameraActivity.FILTER_SHARPEN:
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT_FILT;
                    kernel = new float[] {
                            0f, -1f, 0f,
                            -1f, 5f, -1f,
                            0f, -1f, 0f };
                    break;
                case CameraActivity.FILTER_EDGE_DETECT:
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT_FILT;
                    kernel = new float[] {
                            -1f, -1f, -1f,
                            -1f, 8f, -1f,
                            -1f, -1f, -1f };
                    break;
                case CameraActivity.FILTER_EMBOSS:
                    programType = Texture2dProgram.ProgramType.TEXTURE_EXT_FILT;
                    kernel = new float[] {
                            2f, 0f, 0f,
                            0f, -1f, 0f,
                            0f, 0f, -1f };
                    colorAdj = 0.5f;
                    break;
                default:
                    throw new RuntimeException("Unknown filter mode " + mNewFilter);
            }

            // Do we need a whole new program?  (We want to avoid doing this if we don't have
            // too -- compiling a program could be expensive.)
            if (programType != mFullScreen.getProgram().getProgramType()) {
                mFullScreen.changeProgram(new Texture2dProgram(programType));
                // If we created a new program, we need to initialize the texture width/height.
                mIncomingSizeUpdated = true;
            }

            // Update the filter kernel (if any).
            if (kernel != null) {
                mFullScreen.getProgram().setKernel(kernel, colorAdj);
            }

            mCurrentFilter = mNewFilter;
        }

        /**
         * Records the size of the incoming camera preview frames.
         * <p>
         * It's not clear whether this is guaranteed to execute before or after onSurfaceCreated(),
         * so we assume it could go either way.  (Fortunately they both run on the same thread,
         * so we at least know that they won't execute concurrently.)
         */
        public void setCameraPreviewSize(int width, int height) {
            Log.d(TAG, "setCameraPreviewSize");
            mIncomingWidth = width;
            mIncomingHeight = height;
            mIncomingSizeUpdated = true;
        }

        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            Log.d(TAG, "onSurfaceCreated");

            // We're starting up or coming back.  Either way we've got a new EGLContext that will
            // need to be shared with the video encoder, so figure out if a recording is already
            // in progress.
            mRecordingEnabled = mVideoEncoder.isRecording();
            if (mRecordingEnabled) {
                mRecordingStatus = RECORDING_RESUMED;
            } else {
                mRecordingStatus = RECORDING_OFF;
            }

            // Set up the texture blitter that will be used for on-screen display.  This
            // is *not* applied to the recording, because that uses a separate shader.
            mFullScreen = new FullFrameRect(
                    new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));

            mTextureId = mFullScreen.createTextureObject();

            // Create a SurfaceTexture, with an external texture, in this EGL context.  We don't
            // have a Looper in this thread -- GLSurfaceView doesn't create one -- so the frame
            // available messages will arrive on the main thread.
            mSurfaceTexture = new SurfaceTexture(mTextureId);

            // Tell the UI thread to enable the camera preview.
            mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                    CameraActivity.CameraHandler.MSG_SET_SURFACE_TEXTURE, mSurfaceTexture));
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            Log.d(TAG, "onSurfaceChanged " + width + "x" + height);
        }



        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onDrawFrame(final GL10 unused) {
            if (VERBOSE) Log.d(TAG, "onDrawFrame tex=" + mTextureId);
            boolean showBox = false;

            if(takePicture){
//                BitmapAsync async = new BitmapAsync();
//                async.execute(unused);
                Bitmap bitmap = createBitmapFromGLSurface(0, 0, mGLView.getWidth(), mGLView.getHeight(), unused);
//                byte[] bitmap = createBitmapFromGLSurface(0, 0, mGLView.getWidth(), mGLView.getHeight(), unused);
                updatePage(bitmap);
                mGLView.onPause();
            }else{
                // Latch the latest frame.  If there isn't anything new, we'll just re-use whatever
                // was there before.
                mSurfaceTexture.updateTexImage();


                // If the recording state is changing, take care of it here.  Ideally we wouldn't
                // be doing all this in onDrawFrame(), but the EGLContext sharing with GLSurfaceView
                // makes it hard to do elsewhere.
                if (mRecordingEnabled) {
                    switch (mRecordingStatus) {
                        case RECORDING_OFF:
                            Log.d(TAG, "START recording");
                            // start recording
                            mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                                    mOutputFile, 640, 480, 1000000, EGL14.eglGetCurrentContext()));
                            mRecordingStatus = RECORDING_ON;
                            break;
                        case RECORDING_RESUMED:
                            Log.d(TAG, "RESUME recording");
                            mVideoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                            mRecordingStatus = RECORDING_ON;
                            break;
                        case RECORDING_ON:
                            // yay
                            break;
                        default:
                            throw new RuntimeException("unknown status " + mRecordingStatus);
                    }
                } else {
                    switch (mRecordingStatus) {
                        case RECORDING_ON:
                        case RECORDING_RESUMED:
                            // stop recording
                            Log.d(TAG, "STOP recording");
                            mVideoEncoder.stopRecording();
                            mRecordingStatus = RECORDING_OFF;
                            break;
                        case RECORDING_OFF:
                            // yay
                            break;
                        default:
                            throw new RuntimeException("unknown status " + mRecordingStatus);
                    }
                }

                // Set the video encoder's texture name.  We only need to do this once, but in the
                // current implementation it has to happen after the video encoder is started, so
                // we just do it here.
                //
                // TODO: be less lame.
                mVideoEncoder.setTextureId(mTextureId);

                // Tell the video encoder thread that a new frame is available.
                // This will be ignored if we're not actually recording.
                mVideoEncoder.frameAvailable(mSurfaceTexture);

                if (mIncomingWidth <= 0 || mIncomingHeight <= 0) {
                    // Texture size isn't set yet.  This is only used for the filters, but to be
                    // safe we can just skip drawing while we wait for the various races to resolve.
                    // (This seems to happen if you toggle the screen off/on with power button.)
                    Log.i(TAG, "Drawing before incoming texture size set; skipping");
                    return;
                }
                // Update the filter, if necessary.
                if (mCurrentFilter != mNewFilter) {
                    updateFilter();
                }
                if (mIncomingSizeUpdated) {
                    mFullScreen.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
                    mIncomingSizeUpdated = false;
                }

                // Draw the video frame.
                mSurfaceTexture.getTransformMatrix(mSTMatrix);
                mFullScreen.drawFrame(mTextureId, mSTMatrix);

                // Draw a flashing box if we're recording.  This only appears on screen.
                showBox = (mRecordingStatus == RECORDING_ON);
                if (showBox && (++mFrameCount & 0x04) == 0) {
                    drawBox();
                }
            }

        }

        /**
         * Draws a red box in the corner.
         */
        private void drawBox() {
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(0, 0, 100, 100);
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }


        private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
                throws OutOfMemoryError {
            int bitmapBuffer[] = new int[w * h];
            int bitmapSource[] = new int[w * h];
            IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
            intBuffer.position(0);

            try {
                gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
                int offset1, offset2;
                for (int i = 0; i < h; i++) {
                    offset1 = i * w;
                    offset2 = (h - i - 1) * w;
                    for (int j = 0; j < w; j++) {
                        int texturePixel = bitmapBuffer[offset1 + j];
                        int blue = (texturePixel >> 16) & 0xff;
                        int red = (texturePixel << 16) & 0x00ff0000;
                        int pixel = (texturePixel & 0xff00ff00) | red | blue;
                        bitmapSource[offset2 + j] = pixel;
                    }
                }
            } catch (GLException e) {
                return null;
            }
//            return convert(bitmapSource);
            return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        }

        private byte [] convert(int [] array) {
            if (array == null || array.length == 0) {
                return new byte[0];
            }

            return writeInts(array);
        }

        private byte [] writeInts(int [] array) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(array.length * 4);
                DataOutputStream dos = new DataOutputStream(bos);
                for (int i = 0; i < array.length; i++) {
                    dos.writeInt(array[i]);
                }

                return bos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private class BitmapAsync extends  AsyncTask<GL10, Void, Void>{
            @Override
            protected Void doInBackground(GL10... gl10s) {
                GL10 gl10 = gl10s[0];
//                Bitmap bitmap = createBitmapFromGLSurface(0, 0, mGLView.getWidth(), mGLView.getHeight(), gl10);
//                byte[] byteArray = createBitmapFromGLSurface(0, 0, mGLView.getWidth(), mGLView.getHeight(), gl10);
//                updatePage(byteArray);
                return null;
            }
        }
    }



    /**
     * Handles camera operation requests from other threads.  Necessary because the Camera
     * must only be accessed from one thread.
     * <p>
     * The object is created on the UI thread, and all handlers run there.  Messages are
     * sent from other threads, using sendMessage().
     */
    static class CameraHandler extends Handler {
        public static final int MSG_SET_SURFACE_TEXTURE = 0;

        // Weak reference to the Activity; only access this from the UI thread.
        private WeakReference<CameraActivity> mWeakActivity;

        public CameraHandler(CameraActivity activity) {
            mWeakActivity = new WeakReference<CameraActivity>(activity);
        }

        /**
         * Drop the reference to the activity.  Useful as a paranoid measure to ensure that
         * attempts to access a stale Activity through a handler are caught.
         */
        public void invalidateHandler() {
            mWeakActivity.clear();
        }

        @Override  // runs on UI thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
//            Log.d(TAG, "CameraHandler [" + this + "]: what=" + what);

            CameraActivity activity = mWeakActivity.get();
            if (activity == null) {
//                Log.w(TAG, "CameraHandler.handleMessage: activity is null");
                return;
            }

            switch (what) {
                case MSG_SET_SURFACE_TEXTURE:
                    activity.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
                    break;
                default:
                    throw new RuntimeException("unknown msg " + what);
            }
        }
    }


    /**
     * Connects the SurfaceTexture to the Camera preview output, and starts the preview.
     */
    private void handleSetSurfaceTexture(SurfaceTexture st) {
        st.setOnFrameAvailableListener(this);
        try {
            mCamera.setPreviewTexture(st);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mCamera.startPreview();
    }


    @Override
    public void onFrameAvailable(SurfaceTexture st) {
        // The SurfaceTexture uses this to signal the availability of a new frame.  The
        // thread that "owns" the external texture associated with the SurfaceTexture (which,
        // by virtue of the context being shared, *should* be either one) needs to call
        // updateTexImage() to latch the buffer.
        //
        // Once the buffer is latched, the GLSurfaceView thread can signal the encoder thread.
        // This feels backward -- we want recording to be prioritized over rendering -- but
        // since recording is only enabled some of the time it's easier to do it this way.
        //
        // Since GLSurfaceView doesn't establish a Looper, this will *probably* execute on
        // the main UI thread.  Fortunately, requestRender() can be called from any thread,
        // so it doesn't really matter.
//        if (VERBOSE) Log.d(TAG, "ST onFrameAvailable");
        mGLView.requestRender();
    }
}
