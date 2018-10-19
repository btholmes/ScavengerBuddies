package ben.holmes.scavenger.buddies.Camera.Model;

import android.graphics.Paint;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import ben.holmes.scavenger.buddies.Model.Game;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CameraStateModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String currentPlayer;
    private String gameID;
    private String currentWord;

    private RealmList<String> predictionWords;
    private RealmList<Float> predictionPercentages;

    private boolean foundWord = false;

    public CameraStateModel(){
        predictionWords = new RealmList<>();
        predictionPercentages = new RealmList<>();
    }

    public void setData(Game game, String currentPlayer, ArrayList<Pair<String, Float>> predictions, boolean foundWord){
        this.currentPlayer = currentPlayer;
        this.gameID = game.getGameID();
        this.currentWord = game.getCurrentWord();
        this.foundWord = foundWord;

        if(predictions != null){
            predictionWords.clear();
            predictionPercentages.clear();

            for(Pair item : predictions){
                if(item != null){
                    predictionWords.add((String)item.first);
                    predictionPercentages.add((Float)item.second);
                }
            }
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public List<String> getPredictionWords() {
        return predictionWords;
    }

    public void setPredictionWords(List<String> predictionWords) {
        if(this.predictionWords == null)
            this.predictionWords = new RealmList<>();

        this.predictionWords.clear();

        this.predictionWords.addAll(predictionWords);
    }

    public List<Float> getPredictionPercentages() {
        return predictionPercentages;
    }

    public void setPredictionPercentages(List<Float> predictionPercentages) {
        if(this.predictionPercentages == null)
            this.predictionPercentages = new RealmList<>();

        this.predictionPercentages.clear();

        this.predictionPercentages.addAll(predictionPercentages);
    }

    public boolean isFoundWord() {
        return foundWord;
    }

    public void setFoundWord(boolean foundWord) {
        this.foundWord = foundWord;
    }
}
