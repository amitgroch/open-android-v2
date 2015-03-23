package com.citruspay.sdkui;

public interface ProcessPaymentListener {
    public void processPayment(String response, String error);
}