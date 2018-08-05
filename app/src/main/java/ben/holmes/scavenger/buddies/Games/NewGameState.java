package ben.holmes.scavenger.buddies.Games;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NewGameState extends RealmObject {

    @PrimaryKey
    int id = 0;
    private boolean friendKey;
    private boolean opponentKey;

    private boolean fiveWordKey;
    private boolean tenWordKey;

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

    public boolean isOpponentKey() {
        return opponentKey;
    }

    public void setOpponentKey(boolean opponentKey) {
        this.opponentKey = opponentKey;
    }

    public boolean isTenWordKey() {
        return tenWordKey;
    }

    public void setTenWordKey(boolean tenWordKey) {
        this.tenWordKey = tenWordKey;
    }
}
