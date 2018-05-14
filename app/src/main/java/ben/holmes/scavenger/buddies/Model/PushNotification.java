package ben.holmes.scavenger.buddies.Model;


/**
 * Created by btholmes on 4/16/17.
 */

public class PushNotification {

    private String msg;
    private String senderUID;
    private String senderDisplayName;
    private String senderPhotoURL;
    private String title;
    private String recipientUID;
    private String recipientDisplayName;


    public PushNotification(){

    }


    public PushNotification(
            String title,
            String msg,
            String senderUID,
            String senderDisplayName,
            String recipientUID,
            String recipientDisplayName,
            String senderPhotoURL){
        this.title = title;
        this.msg = msg;
        this.senderUID = senderUID;
        this.senderDisplayName = senderDisplayName;
        this.recipientUID = recipientUID;
        this.recipientDisplayName = recipientDisplayName;
        this.senderPhotoURL = senderPhotoURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getRecipientUID() {
        return recipientUID;
    }

    public void setRecipientUID(String recipientUID) {
        this.recipientUID = recipientUID;
    }

    public String getRecipientDisplayName() {
        return recipientDisplayName;
    }

    public void setRecipientDisplayName(String recipientDisplayName) {
        this.recipientDisplayName = recipientDisplayName;
    }

    public String getSenderPhotoURL() {
        return senderPhotoURL;
    }

    public void setSenderPhotoURL(String senderPhotoURL) {
        this.senderPhotoURL = senderPhotoURL;
    }
}
