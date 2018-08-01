//package ben.holmes.scavenger.buddies.App.Tools;
//
//import android.content.Context;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.widget.RelativeLayout;
//
//import ben.holmes.scavenger.buddies.Friends.Views.FriendSearchView;
//import ben.holmes.scavenger.buddies.Main.MainActivity;
//
//public class HideSearchHelper implements View.OnClickListener {
//
//    private Context ctx;
//    private View searchHolder;
//    private FriendSearchView friendSearchView;
//
//    /**
//     * On Touch variables
//     */
//    float y1 = 0;
//    float y2 = 0;
//    float startingPosition = -1;
//    float hiddenPosition = -1;
//    float searchHolderHeight = -1;
//
//    public HideSearchHelper(Context ctx, View searchHolder){
//        this.ctx = ctx;
//        this.searchHolder = searchHolder;
//    }
//
//    public void setFriendSearchView(FriendSearchView friendSearchView){
//        this.friendSearchView = friendSearchView;
//    }
//
//    public void setSearchHolderInitialY(){
//        if(startingPosition == -1){
//            searchHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if(startingPosition == -1){
//                        startingPosition = searchHolder.getY();
//                        hiddenPosition = startingPosition - searchHolder.getMeasuredHeight();
//                        searchHolderHeight = searchHolder.getMeasuredHeight();
//                        if(friendSearchView != null){
//                            RelativeLayout friendView = friendSearchView.getMainContent();
//                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)friendView.getLayoutParams();
//                            params.height += searchHolderHeight;
//                            friendView.setLayoutParams(params);
//                        }
//                        searchHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                }
//            });
//        }
//    }
//
//
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if(startingPosition == -1) return false;
//
//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            y1 = event.getY();
//        }
//        if(event.getAction() == MotionEvent.ACTION_MOVE){
//            y2 = event.getY();
//
//            float difference = y2-y1;
//
//            if(difference < 0){
////                upward swipe
//                ((MainActivity)getActivity()).adjustViewPagerHeight((int)searchHolderHeight);
//
//                if(mainContent.getY() + difference >= hiddenPosition){
//                    float newY = mainContent.getY() + difference;
//                    mainContent.setY(newY);
//                }else{
//                    if(mainContent.getY() != hiddenPosition){
//                        mainContent.setY(hiddenPosition);
//                    }
//                }
//            }else{
////                Downward swipe
//                if(mainContent.getY() + difference <= startingPosition){
//                    float newY = mainContent.getY() + difference;
//                    mainContent.setY(newY);
//                }else{
//                    if(mainContent.getY() != startingPosition){
//                        mainContent.setY(startingPosition);
//                        ((MainActivity)getActivity()).setViewPagerHeightNormal();
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//
//}
