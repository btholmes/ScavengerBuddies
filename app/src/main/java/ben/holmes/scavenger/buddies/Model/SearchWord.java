package ben.holmes.scavenger.buddies.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchWord extends RealmObject {

    @PrimaryKey
    public int id;
    public String word;
    public boolean found;

    public SearchWord(){

    }

    public SearchWord(String word){
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
        this.found = false;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
