package ben.holmes.scavenger.buddies.App.PopUp;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ben.holmes.scavenger.buddies.R;


public class ScavengerDialog extends Dialog {

    private Context ctx;
    private Dialog dialog;
    private LinearLayout headerContainer;
    private LinearLayout bannerContainer;
    private LinearLayout singleOkButton;
    private LinearLayout buttonContainer;


    private TextView headerText;
    private TextView bannerText;
    private TextView messageText;

    private TextView negativeButton;
    private TextView affirmativeButton;

    private ProgressBar progressBar;

    public ScavengerDialog(Context ctx){
        super(ctx);
        this.ctx = ctx;
        createDialog();
    }

    private void createDialog(){
        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.scavenger_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        headerContainer = dialog.findViewById(R.id.header_container);
        bannerContainer = dialog.findViewById(R.id.banner_container);
        singleOkButton = dialog.findViewById(R.id.single_ok_button);
        buttonContainer = dialog.findViewById(R.id.button_container);

        headerText = dialog.findViewById(R.id.header_text);
        bannerText = dialog.findViewById(R.id.banner_text);
        messageText = dialog.findViewById(R.id.message_text);
        negativeButton = dialog.findViewById(R.id.negative_button);
        affirmativeButton = dialog.findViewById(R.id.affirmative_button);

        progressBar = dialog.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        setDefaultNegativeClick();

    }

    private void setDefaultNegativeClick(){
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void show(){
        dialog.show();
    }

    public void hide(){
        dialog.hide();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public void setHeaderText(String text){
        headerText.setText(text);
    }

    public void setBannerText(String text){
        bannerText.setText(text);
    }

    public void setMessageText(String message) {
        messageText.setText(message);
    }

    public void setNegativeButtonText(String text){
        negativeButton.setText(text);
    }

    public void setAffirmativeButtonText(String text){
        affirmativeButton.setText(text);
    }

    public void setNegativeButtonClick(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }

    public void setAffirmativeButtonClick(View.OnClickListener listener){
        affirmativeButton.setOnClickListener(listener);
    }

    public void hideHeader(){
        headerContainer.setVisibility(View.GONE);
        bannerContainer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dialog_banner_border));
    }

    public void hideBanner(){
        bannerContainer.setVisibility(View.GONE);
    }

    public void showSingleOkButton(){
        singleOkButton.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
    }

    public void hideSingleOkButton(){
        singleOkButton.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
    }

    public void setSingleOkButtonClick(View.OnClickListener listener){
        singleOkButton.setOnClickListener(listener);
    }


    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

}















