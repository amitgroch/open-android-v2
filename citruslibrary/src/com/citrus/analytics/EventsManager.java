package com.citrus.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.citrus.mobile.Config;
import com.citrus.retrofit.RetroFitClient;
import com.citrus.sdk.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.logger.Logger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public class EventsManager {


    private final static String WEBVIEW_EVENTS = "WEBVIEW_EVENTS";

    private final static String PAYMENT_EVENTS = "PAYMENT_EVENTS";


    private final static String INIT_EVENTS = "SDK_VERSION";
    /**
     * This function will be called to log WebView related events
     * @param activity
     * @param webViewEvents
     * @param paymentType
     */
    public static void logWebViewEvents(Activity activity, WebViewEvents webViewEvents,PaymentType paymentType) {
        ConnectionType connectionType = ConnectionManager.getNetworkClass(activity);
        Tracker t = ((CitrusLibraryApp) activity.getApplication()).getTracker(
                CitrusLibraryApp.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
                .setAction(WEBVIEW_EVENTS).setLabel(getWebViewEventLabel(webViewEvents, connectionType, paymentType))
                .setValue(getWebViewEventValue(webViewEvents, connectionType, paymentType)).build());
        //WebViewEvent*ConnectionType*PaymentType*BuildVersion
    }

    /**
     * This function will be called to log payment related events
     * @param activity
     * @param paymentType
     * @param transactionType
     */
    public static void logPaymentEvents(Activity activity, PaymentType paymentType, TransactionType transactionType) {
        ConnectionType connectionType = ConnectionManager.getNetworkClass(activity);
        Tracker t = ((CitrusLibraryApp) activity.getApplication()).getTracker(
                CitrusLibraryApp.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
                .setAction(PAYMENT_EVENTS).setLabel(getPaymentEventLabel(connectionType, paymentType, transactionType))
                .setValue(getPaymentEventValue(connectionType, paymentType, transactionType)).build());
        //ConnectionType*PaymentType*BuildVersion*TransactionType
    }


    public static void logInitSDKEvents(final Context context){

        RetroFitClient.setEndPoint(Config.getBaseURL());
        RetroFitClient.getCitrusRetroFitClient().getMerchantName(Config.getVanity(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Logger.d("Merchant Name is ****" + s);

                Tracker t = ((CitrusLibraryApp) context.getApplicationContext()).getTracker(
                        CitrusLibraryApp.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory(s)
                        .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION))
                        .setValue(Long.valueOf(Constants.SDK_VERSION)).build());
                RetroFitClient.resetEndPoint();
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.d("Failed to get Merchant name *****");
                Tracker t = ((CitrusLibraryApp) context.getApplicationContext()).getTracker(
                        CitrusLibraryApp.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
                        .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION))
                        .setValue(Long.valueOf(Constants.SDK_VERSION)).build());
                RetroFitClient.resetEndPoint();
            }
        });



    }

    public static void logInitSDKEvents(Activity activity){
        Logger.d("VANITY**** " + Config.getVanity());
        Tracker t = ((CitrusLibraryApp) activity.getApplication()).getTracker(
                CitrusLibraryApp.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(Config.getVanity())
                .setAction(INIT_EVENTS).setLabel(String.valueOf(Constants.SDK_VERSION))
                .setValue(Long.valueOf(Constants.SDK_VERSION)).build());
    }


    /**
     * This function will return value for webview events
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return WebViewEvent*ConnectionType*PaymentType*BuildVersion
     */
    private static long getWebViewEventValue(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        //long value = webViewEvents.getValue()*connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT);
      //  return 5L;
        switch(webViewEvents) {
            case OPEN:
                return 1L;
            case BACK_KEY:
                return 2L;
            case CLOSE:
                return 3L;
            default:
                return 0L;
        }

    }

    /**
     * This function will return value for Payment events
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return ConnectionType*PaymentType*BuildVersion*TransactionType
     */
    private static long getPaymentEventValue(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        //long value = connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT)*transactionType.getValue();
        switch(transactionType) {
            case SUCCESS:
                return 4L;
            case FAIL:
                return 5L;
            default:
                return 0L;
        }
    }

    /**
     * This function will return Label for WebViewEvents
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return consolidated String to log into events
     */
    public static String getWebViewEventLabel(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        String eventLabel = webViewEvents.toString()+"_" +connectionType.toString()+"_"+paymentType.toString()+"_"+String.valueOf(Build.VERSION.SDK_INT)+ "_"+String.valueOf(Constants.SDK_VERSION);
        return eventLabel;
    }

    /**
     * This function will return Label for PaymentEvents
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return consolidated String to log into events
     */
    public static String getPaymentEventLabel(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        String eventLabel = connectionType.toString()+"_"+paymentType.toString()+"_"+String.valueOf(Build.VERSION.SDK_INT)+"_" + transactionType.toString()+"_"+String.valueOf(Constants.SDK_VERSION);
        return eventLabel;
    }



}
