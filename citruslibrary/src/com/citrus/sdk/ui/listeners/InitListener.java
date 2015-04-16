package com.citrus.sdk.ui.listeners;

import com.citrus.sdk.ui.classes.NetbankingOption;
import com.citrus.sdk.ui.classes.PaymentOption;

import java.util.ArrayList;

/**
 * Created by MANGESH KADAM on 2/17/2015.
 */
public interface InitListener {

    void onInitCompleted();

    void onReceiveNetbankingList(ArrayList<NetbankingOption> listNetbanking, ArrayList<NetbankingOption> listTopNetbanking);

    void onFailToReceiveNetbankingList(String errorMessage);

    void onReceiveSavedOptions(ArrayList<PaymentOption> listSavedOption);

    void onFailToReceiveSavedOptions(String errorMessage);
}
