package ben.holmes.scavenger.buddies.Model;

/**
 * Created by btholmes on 3/26/17.
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import static android.R.attr.id;

public class Game implements Serializable{

    private String lastPictureTaken;
    @Required
    @PrimaryKey
    private String gameID;
    @Required
    private String challenger;
    @Required
    private String opponent;
    @Required
//    private String words;
    private List<String> words = new ArrayList<>();
    @Required
//    private String opponentWordsLeft;
    private List<String> opponentWordsLeft = new ArrayList<>();
    @Required
//    private String challengerWordsLeft;
   private List<String> challengerWordsLeft = new ArrayList<>();


    private String challengerDisplayName;
    private String opponentDisplayName;
    private String winner;

    private boolean stillInPlay;
    private boolean challengerTurn;
    private int round;
    private boolean opponentHasAccepted;
    private long opponentTimeElapsed;
    private long challengerTimeElapsed;
    private String date;
    private Friend friend;
    private String text = null;
    private int photo = -1;


    public Game() {

    }

    public Game(String challenger, String opponent, List<String> words ){
        Random random = new Random();
        String gameID = System.currentTimeMillis() +""+ random.nextInt(100000);

//        this.words = words.toString();
        this.words = words;
        this.gameID = gameID;
        date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.challenger = challenger;
        this.opponent = opponent;
        this.winner = null;
        stillInPlay = true;
        opponentHasAccepted = false;
        challengerTurn = true;
        round = 1;
    }

//    public Game(boolean stillInPlay, boolean opponentHasAccepted, String opponentUID, String gameID, String dateCreated, String challengerUID){
//        this.stillInPlay = stillInPlay;
//        this.opponentHasAccepted = opponentHasAccepted;
//        this.opponent = opponentUID;
//        this.gameID = gameID;
//        this.date = dateCreated;
//        this.challenger = challengerUID;
//    }


    public boolean isChallengerTurn() {
        return challengerTurn;
    }

    public void setChallengerTurn(boolean challengerTurn) {
        this.challengerTurn = challengerTurn;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }

    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

    public String getChallengerDisplayName() {
        return challengerDisplayName;
    }

    public void setChallengerDisplayName(String challengerDisplayName) {
        this.challengerDisplayName = challengerDisplayName;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public boolean isOpponentHasAccepted() {return opponentHasAccepted; }

    public boolean isOpponent(String uid){return this.opponent.equals(uid); }

    public String getGameID() {
        return gameID;
    }

    public String getDate() {
        return date;
    }

    public String getChallenger() {
        return challenger;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isStillInPlay() {
        return stillInPlay;
    }


    public void setOpponentHasAccepted(boolean accepted) {this.opponentHasAccepted = accepted; }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setStillInPlay(boolean stillInPlay) {
        this.stillInPlay = stillInPlay;
    }

    public String getLastPictureTaken() {
        return lastPictureTaken;
    }

    public void setLastPictureTaken(String lastPictureTaken) {
        this.lastPictureTaken = lastPictureTaken;
    }

    public List<String> getOpponentWordsLeft() {
        return opponentWordsLeft;
    }

    public void setOpponentWordsLeft(List<String> opponentWordsLeft) {
        this.opponentWordsLeft = opponentWordsLeft;
    }

    public List<String> getChallengerWordsLeft() {
        return challengerWordsLeft;
    }

    public void setChallengerWordsLeft(List<String> challengerWordsLeft) {
        this.challengerWordsLeft = challengerWordsLeft;
    }

    public long getOpponentTimeElapsed() {
        return opponentTimeElapsed;
    }

    public void setOpponentTimeElapsed(long opponentTimeElapsed) {
        this.opponentTimeElapsed = opponentTimeElapsed;
    }

    public long getChallengerTimeElapsed() {
        return challengerTimeElapsed;
    }

    public void setChallengerTimeElapsed(long challengerTimeElapsed) {
        this.challengerTimeElapsed = challengerTimeElapsed;
    }



    public long getId() {
        return id;
    }



    public String getText() {
        return text;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setPhoto(int photo){
        this.photo = photo;
    }

}
