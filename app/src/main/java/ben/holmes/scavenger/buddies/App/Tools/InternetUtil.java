package ben.holmes.scavenger.buddies.App.Tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetUtil {

    private static InternetUtil internetUtil;
    private Context ctx;
    private ConnectivityManager connectivityManager;
    private boolean connected = false;

    public static InternetUtil getInstance(Context ctx){
        if (internetUtil == null) {
            synchronized (ctx){
                if (internetUtil == null)
                    internetUtil = new InternetUtil(ctx);
            }
        }
        return internetUtil;
    }

    private InternetUtil(Context ctx){
        this.ctx = ctx;
    }


    public boolean hasInternet(final CallComplete callback) {
        boolean online = false;
        try {
            connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            if (connected) {
//                if (isInternetWorking()) online = true;
                Connect connect = new Connect(callback);
                connect.execute();
            }
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return online;
    }

//    public boolean isInternetWorking() {
//        boolean success = false;
//
//        return success;
//    }

    public interface CallComplete{
        void onComplete(Boolean result);
    }

    public class Connect extends AsyncTask<Void, Void, Boolean>{

        private CallComplete callback;

        public Connect(CallComplete callback){
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
//            CallComplete callback = params[0];
            Boolean success = false;
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Android");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.connect();
                if (connection.getResponseCode() == 200) success = true;
                connection.disconnect();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean hasConnection) {
            super.onPostExecute(hasConnection);
            callback.onComplete(hasConnection);
        }
    }

}
