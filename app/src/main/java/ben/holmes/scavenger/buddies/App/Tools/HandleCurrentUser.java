package ben.holmes.scavenger.buddies.App.Tools;

import ben.holmes.scavenger.buddies.Model.User;

public class HandleCurrentUser {

    private static HandleCurrentUser handleCurrentUser;
    private User user;

    public static HandleCurrentUser getInstance() {
        return handleCurrentUser == null ? new HandleCurrentUser() : handleCurrentUser;
    }

    private HandleCurrentUser(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
