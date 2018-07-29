package ben.holmes.scavenger.buddies.Model;


public class FacebookProfile {

    private String email;
    private String firstName;
    private String lastName;
    private String profilePicURL;

    public FacebookProfile(String email, String firstName, String lastName, String profilePicURL){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicURL = profilePicURL;
    }

}
