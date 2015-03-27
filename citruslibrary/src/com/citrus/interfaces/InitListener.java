package com.citrus.interfaces;

import com.citrus.sdkui.NetbankingOption;
import com.citrus.sdkui.PaymentOption;

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
