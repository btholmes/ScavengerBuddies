package ben.holmes.scavenger.buddies.Camera.Activities;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;

public class Camera2OpenGL extends AppCompatActivity {

    private TextView predictionBox;
    private GLSurfaceView glSurfaceView;
    private Button takePictureButton;
    private MainRenderer renderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        predictionBox = findViewById(R.id.prediciton_box);
        glSurfaceView = findViewById(R.id.cameraPreview_surfaceView);
        takePictureButton = findViewById(R.id.btn_takepicture);

        setUp();
    }

    private void setUp(){
        renderer = new MainRenderer(glSurfaceView);
        glSurfaceView.setEGLContextClientVersion ( 2 );
        glSurfaceView.setRenderer ( renderer );
        glSurfaceView.setRenderMode ( GLSurfaceView.RENDERMODE_WHEN_DIRTY );
    }


    @Override
    protected void onResume() {
        super.onResume();
        renderer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        renderer.onPause();
    }
}
