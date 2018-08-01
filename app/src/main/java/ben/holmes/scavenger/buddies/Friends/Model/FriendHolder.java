//package ben.holmes.scavenger.buddies.Friends.Model;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
//import ben.holmes.scavenger.buddies.Friends.Activities.FriendDetailsActivity;
//import ben.holmes.scavenger.buddies.Model.User;
//import ben.holmes.scavenger.buddies.R;
//
//public static class FriendHolder extends RecyclerView.ViewHolder{
//
//    public ImageView image;
//    public TextView name;
//    public TextView emailName;
//    public TextView subtitle;
//    public TextView challengeButton;
//    public TextView messageButton;
//    public LinearLayout userContent;
//    public LinearLayout facebookInfo;
//    public LinearLayout emailInfo;
//
//    public FriendHolder(View view){
//        super(view);
//        image = view.findViewById(R.id.image);
//        name = view.findViewById(R.id.name);
//        emailName = view.findViewById(R.id.emailName);
//        subtitle = view.findViewById(R.id.subtitle);
//        challengeButton = view.findViewById(R.id.challengeButton);
//        messageButton = view.findViewById(R.id.messageButton);
//        userContent = view.findViewById(R.id.userContent);
//        facebookInfo = view.findViewById(R.id.facebookInfo);
//        emailInfo = view.findViewById(R.id.emailInfo);
//    }
//
//    public void setUserInfo(User user){
//        if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
//            showEmailInfo();
//            setEmailName(user.getNameHash());
//        }else{
//            showFacebookInfo();
//            setName(user.getDisplayName());
//            setSubtitle(user.getNameHash());
//        }
//        setImage(user.getPhotoUrl());
//        setListeners(user);
//    }
//
//    public void setListeners(final User user){
//        setUserContentListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FriendDetailsActivity.navigate(activity, user);
//            }
//        });
//    }
//
//
//    public void setImage(String url){
//        if(url != null && url.length() > 0)
//            Picasso.with(staticContext).load(url).transform(new CircleTransform()).into(image);
//
////            image.setImageResource(resource);
//    }
//
//    public void setEmailName(String text){
//        this.emailName.setText(text);
//    }
//
//    public void showEmailInfo(){
//        emailInfo.setVisibility(View.VISIBLE);
//        facebookInfo.setVisibility(View.GONE);
//    }
//
//    public void showFacebookInfo(){
//        facebookInfo.setVisibility(View.VISIBLE);
//        emailInfo.setVisibility(Go);
//    }
//
//    public void setName(String name){
//        this.name.setText(name);
//    }
//
//    public void setSubtitle(String subtitle){
//        this.subtitle.setText(subtitle);
//    }
//
//    public void setUserContentListener(View.OnClickListener listener){
//        this.userContent.setOnClickListener(listener);
//    }
//
//    public void setChallengeListner(View.OnClickListener listener){
//        this.challengeButton.setOnClickListener(listener);
//    }
//
//    public void setMessageListener(View.OnClickListener listener){
//        this.messageButton.setOnClickListener(listener);
//    }
//
//}
