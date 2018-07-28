package ben.holmes.scavenger.buddies.Train;

/**
 * Created by akiel on 3/23/2017.
 */

public class Tag {
    private String tag;
    private int consistency = 0;

    public Tag() {

    }

    public Tag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public int getConsistency() {
        return consistency;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setConsistency(int consistency) {
        this.consistency = consistency;
    }

}