package ben.holmes.scavenger.buddies.Train;


/**
 * Created by akiel on 3/22/2017.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.R;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class dataCollectionActivity extends AppCompatActivity {

    private static final int READ_CONTACTS = 1000;
    private static final int WRITE_EXTERNAL_STORAGE = 1001;
    private static final int READ_EXTERNAL_STORAGE = 1002;

    public static boolean CAN_READ_CONTACTS = false;
    public static boolean CAN_WRITE_EXTERNAL_STORAGE = false;
    public static boolean CAN_READ_EXTERNAL_STORAGE = false;

    private static final int CAMERA_REQUEST = 1888;


    private ImageButton cameraButton;
    private TextView tagText;
    private ArrayList<String> tags = new ArrayList<>();

    ImageView mimageView;
    private Uri selectedImage;

    private ClarifaiClient clarifaiClient;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private DatabaseReference mFirebaseDatabaseReference;
    int index;
    Tag t;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect);

        clarifaiClient = new ClarifaiBuilder(getResources().getString(R.string.clarifai_api_key)).buildSync();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        tagText = (TextView) findViewById(R.id.tag_text);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        mimageView = (ImageView) this.findViewById(R.id.picture);
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ScavengerActivity.CAN_WRITE_EXTERNAL_STORAGE) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_CONTACTS = true;
                }
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_WRITE_EXTERNAL_STORAGE = true;
                }
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CAN_READ_EXTERNAL_STORAGE = true;
                }
            }
        }
    }


    /**
     * Clears tag values, tag TextView, and preview ImageView
     */
    public void clearFields() {
        tags.clear();
        tagText.setText("");
        ((ImageView) findViewById(R.id.picture)).setImageResource(android.R.color.transparent);
    }

    /**
     * Prints the first 10 tags for an image
     */
    public void printTags() {
        String results = "TAGS: " + tags.size();
        for (int i = 0; i < tags.size(); i++) {
            results += "\n" + tags.get(i);
        }
        tagText.setText(results);
    }

    /*
     * Check if the Clarifai tags matches the requested tags
     */
    public void checkMatch() {
        //TODO Upload data here

        for (index = 0; index < tags.size(); index++) {
            t = new Tag(tags.get(index));
            t.setConsistency(1);

            //mFirebaseDatabaseReference.child("TagData").child("TagList").child(tags.get(index)).setValue(t);

            new TagUpdate().execute(t);

        }
    }

    /*
     * Clarifai API Call
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
        }

        //check if image was collected successfully
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == RESULT_OK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            final ImageView preview = (ImageView) findViewById(R.id.picture);
            preview.setImageBitmap(bitmap);

            new AsyncTask<Bitmap, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {

                // Model prediction
                @Override
                protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Bitmap... bitmaps) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] byteArray = stream.toByteArray();
                    final ConceptModel general = clarifaiClient.getDefaultModels().generalModel();
                    return general.predict()
                            .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
                            .executeSync();
                }

                /*
                 * Handling API response and then collecting and printing tags
                 */
                @Override
                protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final List<ClarifaiOutput<Concept>> predictions = response.get();
                    if (predictions.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final List<Concept> predictedTags = predictions.get(0).data();
                    for (int i = 0; i < predictedTags.size(); i++) {
                        tags.add(predictedTags.get(i).name());
                    }
                    printTags();
                    checkMatch();
                }
            }.execute(bitmap);
        }
    }
}