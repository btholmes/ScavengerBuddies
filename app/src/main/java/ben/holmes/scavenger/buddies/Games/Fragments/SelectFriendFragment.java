package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.R;

public class SelectFriendFragment extends ScavengerFragment {

    private Context ctx;
    private RecyclerView recyclerView;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_select_friend, container, false);
        recyclerView  = rootView.findViewById(R.id.recycler_view);
        populateView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void populateView(){


    }


    @Override
    public String getToolbarTitle() {
        return "Select Friend";
    }

    @Override
    public int getToolbarColor() {
        return R.color.colorPrimary;
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

        public RecyclerAdapter(){

        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View v){
                super(v);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

}
