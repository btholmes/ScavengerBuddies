package ben.holmes.scavenger.buddies.Friends.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class CustomFirebaseAdapter<T, VH extends RecyclerView.ViewHolder> extends FirebaseRecyclerAdapter {


    private OnItemClickListener mOnItemClickListener;
    private static Context ctx;

    public interface OnItemClickListener {
        void onItemClick(View view, User obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public CustomFirebaseAdapter(Class<T> modelClass, @LayoutRes int modelLayout, Class<VH> viewHolderClass, Query query){
        super(modelClass, modelLayout, viewHolderClass, query);
    }

    public void setContext(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, final Object model, final int position) {
        FriendHolder friendHolder = (FriendHolder)viewHolder;
        friendHolder.setUserInfo((User)model);

        friendHolder.getLytParent().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(v, (User)model, position);
                }
            }
        });
    }


    public static class FriendHolder extends RecyclerView.ViewHolder{

        public RelativeLayout lytParent;
        public ImageView image;
        public TextView name;
        public TextView emailName;
        public TextView subtitle;
        public TextView challengeButton;
        public TextView messageButton;
        public LinearLayout userContent;
        public LinearLayout facebookInfo;
        public LinearLayout emailInfo;

        public FriendHolder(View view){
            super(view);
            lytParent = view.findViewById(R.id.lyt_parent);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            emailName = view.findViewById(R.id.emailName);
            subtitle = view.findViewById(R.id.subtitle);
            challengeButton = view.findViewById(R.id.challengeButton);
            messageButton = view.findViewById(R.id.messageButton);
            hideButtons();
            userContent = view.findViewById(R.id.userContent);
            facebookInfo = view.findViewById(R.id.facebookInfo);
            emailInfo = view.findViewById(R.id.emailInfo);
        }

        private void hideButtons(){
            challengeButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
        }

        public void setUserInfo(User user){
            if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
                showEmailInfo();
                setEmailName(user.getNameHash());
            }else{
                showFacebookInfo();
                setName(user.getDisplayName());
                setSubtitle(user.getNameHash());
            }
            setImage(user.getPhotoUrl());
        }

        public RelativeLayout getLytParent() {
            return lytParent;
        }

//        public void setListeners(@Nullable final Fragment delegate){
//
//            setUserContentListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(delegate != null && delegate instanceof NewGameFragment){
//                        ((NewGameFragment)delegate).moveOn();
//                    }
//
//                }
//            });
//        }


        public void setImage(String url){
            if(url != null && url.length() > 0)
                Picasso.with(ctx).load(url).transform(new CircleTransform()).into(image);

//            image.setImageResource(resource);
        }

        public void setEmailName(String text){
            this.emailName.setText(text);
        }

        public void showEmailInfo(){
            emailInfo.setVisibility(View.VISIBLE);
            facebookInfo.setVisibility(View.GONE);
        }

        public void showFacebookInfo(){
            facebookInfo.setVisibility(View.VISIBLE);
            emailInfo.setVisibility(View.GONE);
        }

        public void setName(String name){
            this.name.setText(name);
        }

        public void setSubtitle(String subtitle){
            this.subtitle.setText(subtitle);
        }

        public void setUserContentListener(View.OnClickListener listener){
            this.userContent.setOnClickListener(listener);
        }

        public void setChallengeListner(View.OnClickListener listener){
            this.challengeButton.setOnClickListener(listener);
        }

        public void setMessageListener(View.OnClickListener listener){
            this.messageButton.setOnClickListener(listener);
        }

    }
}
