package ben.holmes.scavenger.buddies.App.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class FacebookUtil {
    private Context ctx;
    private Prefs prefs;

    public FacebookUtil(Context ctx){
        this.ctx = ctx;
        prefs = new Prefs(ctx);
    }

    public void getFriends(){
        LoginResult loginResult = prefs.getFacebookLoginResult();
        if(loginResult != null){
//            User has already logged in with facebook, so we get their friends
        }else{
//            Request access by having them login
        }

//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                accessToken,
//                "/{user-id}/friends",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        // Insert your code here
//                    }
//                });
//
//        request.executeAsync();
    }

    public interface FacebookFriendsCallback{
        void onComplete(GraphResponse response);
    }

    /**
     * Get Facebook friends who have signed into this app
     */
    public void getUserFriends(final FacebookFriendsCallback callback){

        LoginResult loginResult = prefs.getFacebookLoginResult();
        AccessToken accessToken = loginResult.getAccessToken();
        String userId = loginResult.getAccessToken().getUserId();
        String path = "/" + userId + "/friends";

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                path,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        GraphResponse copy = response;
                        callback.onComplete(response);

                    }
                });

        request.executeAsync();

    }

    public interface FacebookCredentialCallback{
        void onComplete(boolean isSuccessful);
    }

    public void linkFacebookCredential(AuthCredential credential, Activity activity, final FacebookCredentialCallback callback){
        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Success", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            callback.onComplete(true);
                        } else {
                            Log.w("Failure", "linkWithCredential:failure", task.getException());
                            callback.onComplete(false);
                        }
                    }
                });
    }


    public interface FacebookBasicInfoCallback{
        void onComplete(String email, String firstName,
                        String lastName, String profileUrl);
    }

    public void getBasicInfoFromFacebook(final FacebookBasicInfoCallback callback){
        LoginResult loginResult = prefs.getFacebookLoginResult();
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                Log.i("Response", response.toString());
                                String email = response.getJSONObject().getString("email");
                                String firstName = response.getJSONObject().getString("first_name");
                                String lastName = response.getJSONObject().getString("last_name");
                                String profileURL = "";
                                if (Profile.getCurrentProfile() != null) {
                                    profileURL = ImageRequest.getProfilePictureUri(Profile.getCurrentProfile().getId(), 400, 400).toString();
                                }
                                callback.onComplete(email, firstName, lastName, profileURL);

                                //TODO put your code here
                            } catch (Exception e) {
                                e.printStackTrace();
//                                Toast.makeText(ActivitySignIn.this, R.string.error_occurred_try_again, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,email,first_name,last_name");
            request.setParameters(parameters);
            request.executeAsync();
    }
}
