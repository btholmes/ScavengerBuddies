package ben.holmes.scavenger.buddies.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by btholmes on 11/4/17.
 */

/**
 * This is user class used to retrieve data from Firebase. All info is parsed and stored in Realm as user-specific
 */
public class User {

    private String displayName = null;
    private String email = null;
    private String photoUrl = "https://scontent.xx.fbcdn.net/v/t1.0-1/s100x100/1378871_10151890982237420_1347727989_n.jpg?oh=f8522e0ea7aee1411b67ee45988d5456&oe=5A6FA4C9";
    private String uid = null;

    private int wins = 0;
    private int losses = 0;

//    private List<HashMap<String, Game>> games = new ArrayList<>();
    private HashMap<String, Game> games = new HashMap<>();
    private String userToken = null;
    private List<PushNotification> notifications = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private String hasGames = "No";


    public User() {

    }

    public User(String uid, String email){
        this.uid = uid;
        this.email = email;
        this.displayName = email;

    }

    public User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;

    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public String getUid(){
        return uid;
    }

    public String getEmail(){
        return email;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
//
//    public List<HashMap<String, Game>> getGames() {
//        if(games.size() >=1){
//            hasGames = "yes";
//        }
//        return games;
//    }

//    public void setGames(List<Game> games) {
//        if(games!= null && games.size() >= 1){
//            this.games = games;
//            hasGames = "no";
//        }

//    }


    public HashMap<String, Game> getGames() {
        if(games.size() >= 1){
            hasGames = "yes";
        }
        return games;
    }

    public void setGames(HashMap<String, Game> games) {
        if(games!= null && games.size() >= 1){
            this.games = games;
            hasGames = "no";
        }
    }

    public List<PushNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<PushNotification> notifications) {
        this.notifications = notifications;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getHasGames() {
        return hasGames;
    }

    public void setHasGames(String hasGames) {
        this.hasGames = hasGames;
    }


}