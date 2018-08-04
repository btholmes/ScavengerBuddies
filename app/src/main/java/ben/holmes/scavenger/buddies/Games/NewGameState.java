package ben.holmes.scavenger.buddies.Games;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewGameState extends RealmObject {

    @PrimaryKey
    int id = 0;
    private boolean friendKey;
    private boolean fiveWordKey;

    public NewGameState(){ }

    public boolean isFriendKey() {
        return friendKey;
    }

    public void setFriendKey(boolean friendKey) {
        this.friendKey = friendKey;
    }

    public boolean isFiveWordKey() {
        return fiveWordKey;
    }

    public void setFiveWordKey(boolean fiveWordKey) {
        this.fiveWordKey = fiveWordKey;
    }
}
