package ben.holmes.scavenger.buddies.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import ben.holmes.scavenger.buddies.Database.Database;
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

    private int permittedGameDuration = 3;


    private String challengerDisplayName;
    private String opponentDisplayName;

    private String challengerPhotoUrl;
    private String opponentPhotoUrl;

    private int opponentScore;
    private int challengerScore;

    private String challengersLastPlayedTurn;
    private String opponentsLastPlayedTurn;

    private String winner;

    private boolean stillInPlay;
    private boolean yourTurn;
    private int round;
    private boolean opponentHasAccepted;
    private long opponentTimeElapsed;
    private long challengerTimeElapsed;
    private String date;
    private Friend friend;
    private String text = null;
    private int photo = -1;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private TimeZone challengerTimeZone;

    public Game() {

    }

    public Game(String challenger, String opponent, List<String> words ){
        Random random = new Random();
        String gameID = System.currentTimeMillis() +""+ random.nextInt(100000);
        this.gameID = gameID;

//        this.words = words.toString();
        this.words = words;
        setOpponentWordsLeft(words);
        setChallengerWordsLeft(words);
        setOpponentScore(0);
        setChallengerScore(0);

        challengerTimeZone = TimeZone.getDefault();
        Date currentDate = Calendar.getInstance(challengerTimeZone).getTime();
        String dateString = new SimpleDateFormat(dateFormat).format(currentDate);
        setDate(dateString);
        setChallengersLastPlayedTurn(dateString);
        setOpponentsLastPlayedTurn("");

        this.challenger = challenger;
        this.opponent = opponent;
        this.winner = null;
        stillInPlay = true;
        opponentHasAccepted = false;
        yourTurn = true;
        round = 1;

    }





    public interface setInfoCallback{
        void onComplete();
    }


    private void setChallengerInfo(final setInfoCallback callback){
        Database.getInstance().getUser(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getDisplayName() == null || user.getDisplayName().length() <= 0)
                    setChallengerDisplayName(user.getNameHash());
                else
                    setChallengerDisplayName(user.getDisplayName());

                setChallengerPhotoUrl(user.getPhotoUrl());
                callback.onComplete();
            }
        }, getChallenger());
    }

    private void setOpponentInfo(final setInfoCallback callback){
        Database.getInstance().getUser(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getDisplayName() == null || user.getDisplayName().length() <= 0)
                    setOpponentDisplayName(user.getNameHash());
                else
                    setOpponentDisplayName(user.getDisplayName());

                setOpponentPhotoUrl(user.getPhotoUrl());
                callback.onComplete();
            }
        }, getOpponent());
    }

    public void setPlayerInfo(final setInfoCallback callback){
      setChallengerInfo(new setInfoCallback() {
          @Override
          public void onComplete() {
              setOpponentInfo(new setInfoCallback() {
                  @Override
                  public void onComplete() {
                      callback.onComplete();
                  }
              });
          }
      });

    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public int getChallengerScore() {
        return challengerScore;
    }

    public void setChallengerScore(int challengerScore) {
        this.challengerScore = challengerScore;
    }

    public String getChallengerPhotoUrl() {
        return challengerPhotoUrl;
    }

    public void setChallengerPhotoUrl(String challengerPhotoUrl) {
        this.challengerPhotoUrl = challengerPhotoUrl;
    }

    public String getOpponentPhotoUrl() {
        return opponentPhotoUrl;
    }

    public void setOpponentPhotoUrl(String opponentPhotoUrl) {
        this.opponentPhotoUrl = opponentPhotoUrl;
    }

    public boolean getYourTurn() {
        return this.yourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
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

    public String getDate(){
        return this.date;
    }

    public String getCreationDate() {
        String result = "";
        TimeZone tz = TimeZone.getDefault();
        try{
            Date creationDate = new SimpleDateFormat(dateFormat).parse(this.date);
            Date today = Calendar.getInstance(tz).getTime();
            int days = daysBetween(creationDate, today);
            String plural = " days ";
            if(days == 1)
                plural = " day ";
            if(days == 0)
                result = "Created today";
            else
                result = "Created " + plural + days + " ago";
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public String getTimeLeft(String uid){
        String result = "";
        TimeZone tz = TimeZone.getDefault();
        try{
            Date creationDate;
            if(uid.equals(getChallenger()))
                creationDate = new SimpleDateFormat(dateFormat).parse(getChallengersLastPlayedTurn());
            else
                creationDate = new SimpleDateFormat(dateFormat).parse(getOpponentsLastPlayedTurn());

            Date today = Calendar.getInstance(tz).getTime();
            int days = permittedGameDuration - daysBetween(creationDate, today);
            String plural = " days ";
            if(days <= 1)
                plural = " day ";

            result = "You have " + days + plural + "left to play";
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public int daysBetween(Date date1, Date date2){
        return (int)Math.floor(date2.getTime() - date1.getTime())/(1000*60*60*24);
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


    public String getChallengersLastPlayedTurn() {
        return challengersLastPlayedTurn;
    }

    public void setChallengersLastPlayedTurn(String challengersLastPlayedTurn) {
        this.challengersLastPlayedTurn = challengersLastPlayedTurn;
    }

    public void setChallengerPlayed(){
        Date currentDate = Calendar.getInstance(TimeZone.getDefault()).getTime();
        this.challengersLastPlayedTurn = new SimpleDateFormat(dateFormat).format(currentDate);
    }

    public String getOpponentsLastPlayedTurn() {
        return opponentsLastPlayedTurn;
    }

    public void setOpponentsLastPlayedTurn(String opponentsLastPlayedTurn) {
        this.opponentsLastPlayedTurn = opponentsLastPlayedTurn;
    }

    public void setOpponentPlayed(){
        Date currentDate = Calendar.getInstance(TimeZone.getDefault()).getTime();
        this.opponentsLastPlayedTurn = new SimpleDateFormat(dateFormat).format(currentDate);
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
