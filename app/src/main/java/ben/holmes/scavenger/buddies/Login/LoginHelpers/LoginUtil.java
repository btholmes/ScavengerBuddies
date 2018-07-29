package ben.holmes.scavenger.buddies.Login.LoginHelpers;

import com.google.firebase.auth.FirebaseAuth;

public class LoginUtil {

    public static void logOut(){
        if(FirebaseAuth.getInstance() != null)
            FirebaseAuth.getInstance().signOut();
        if(com.facebook.login.LoginManager.getInstance() != null)
            com.facebook.login.LoginManager.getInstance().logOut();
    }
}
