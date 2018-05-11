package ben.holmes.scavenger.buddies.App.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ben.holmes.scavenger.buddies.R;

public class GooglePay implements PurchasesUpdatedListener{

    private Context ctx;
    private BillingClient mBillingClient;
    private String in_app_price;



    public GooglePay(Context ctx){
        this.ctx = ctx;
    }

    public void connect(){
        // create new Person
        mBillingClient = BillingClient.newBuilder((Activity)ctx).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
//                    skuListResponse();
                    makePurchase("in_app_donation");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }

    private ArrayList<String> getInAppProducts(){
        ArrayList<String > result = null;
        String[] array = ctx.getResources().getStringArray(R.array.in_app_products);
        result = new ArrayList<>(Arrays.asList(array));
        return result;
    }

    private void skuListResponse(){
//        List skuList = new ArrayList();
//        skuList.add("in_app_donation");

        List skuList = getInAppProducts();

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List skuDetailsList) {
                        // Process the result.
                        if (responseCode == BillingClient.BillingResponse.OK
                                && skuDetailsList != null) {
                            for (Object skuDetails : skuDetailsList) {
                                String sku = ((SkuDetails)skuDetails).getSku();
                                String price = ((SkuDetails)skuDetails).getPrice();
                                if ("in_app_donation".equals(sku)) {
                                    in_app_price = price;
                                    makePurchase(sku);
                                }
                            }
                        }
                    }
                });
    }

    public void makePurchase(String skuId){
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(skuId)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build();
        int responseCode = mBillingClient.launchBillingFlow((Activity)ctx, flowParams);
    }


}
