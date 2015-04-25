package com.citrus.analytics;

import android.app.Activity;
import android.os.Build;

import com.citrus.mobile.Config;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by MANGESH KADAM on 4/24/2015.
 */
public class EventsManager {


    private final static String WEBVIEW_EVENTS = "WEBVIEW_EVENTS";

    private final static String PAYMENT_EVENTS = "PAYMENT_EVENTS";


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
                .setValue(getPaymentEventValue(connectionType, paymentType,transactionType)).build());
        //ConnectionType*PaymentType*BuildVersion*TransactionType
    }


    /**
     * This function will return value for webview events
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return WebViewEvent*ConnectionType*PaymentType*BuildVersion
     */
    private static long getWebViewEventValue(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        long value = webViewEvents.getValue()*connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT);
        return value;
    }

    /**
     * This function will return value for Payment events
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return ConnectionType*PaymentType*BuildVersion*TransactionType
     */
    private static long getPaymentEventValue(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        long value = connectionType.getValue()*paymentType.getValue()*APILevel.getValue(Build.VERSION.SDK_INT)*transactionType.getValue();
        return value;
    }

    /**
     * This function will return Label for WebViewEvents
     * @param webViewEvents
     * @param connectionType
     * @param paymentType
     * @return
     */
    public static String getWebViewEventLabel(WebViewEvents webViewEvents, ConnectionType connectionType, PaymentType paymentType) {
        String eventLabel = webViewEvents.toString()+"_" +connectionType.toString()+"_"+paymentType.toString()+"_"+String.valueOf(Build.VERSION.SDK_INT);
        return eventLabel;
    }

    /**
     * This function will return Label for PaymentEvents
     * @param connectionType
     * @param paymentType
     * @param transactionType
     * @return
     */
    public static String getPaymentEventLabel(ConnectionType connectionType, PaymentType paymentType, TransactionType transactionType) {
        String eventLabel = connectionType.toString()+"_"+paymentType.toString()+"_"+String.valueOf(Build.VERSION.SDK_INT)+"_" + transactionType.toString();
        return eventLabel;
    }



}
