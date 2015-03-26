package com.citruspay.sdkui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.citruspay.sdkui.PaymentProcessingFragment.OnTransactionCompleteListener} interface
 * to handle interaction events.
 * Use the {@link PaymentProcessingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentProcessingFragment extends Fragment {

    private OnTransactionCompleteListener mListener;
    private WebView mWebviewPayment = null;
    private String mUrl = null;
    private ProgressDialog mProgressDialog = null;
    private Context mContext = null;

    public PaymentProcessingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param redirectUrl - Url of the bank or Card acquirer.
     * @return A new instance of fragment PaymentProcessingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentProcessingFragment newInstance(String redirectUrl) {
        PaymentProcessingFragment fragment = new PaymentProcessingFragment();
        Bundle args = new Bundle();
        args.putString(Utils.INTENT_EXTRA_PAYMENT_URL, redirectUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(Utils.INTENT_EXTRA_PAYMENT_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_processing, container, false);

        mContext = getActivity();
        mProgressDialog = new ProgressDialog(mContext);

        mWebviewPayment = (WebView) rootView.findViewById(R.id.webViewPayment);
        mWebviewPayment.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
            This setting is required to enable redirection of urls from https to http or vice-versa.
            This redirection is blocked by default from Lollipop (Android 21).
             */
            mWebviewPayment.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebviewPayment.addJavascriptInterface(new JsInterface(), "CitrusResponse");
        mWebviewPayment.setWebViewClient(new CitrusWebClient());
        // Load the bank's or card payment url
        mWebviewPayment.loadUrl(mUrl);

        return rootView;
    }

    private void showDialog(String message, boolean cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    private void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTransactionCompleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // Check whether the activity has implemented the OnActivityTitleChangeListener.
        // Call the onActivityTitleChanged to change the title of the activity
        if (activity instanceof OnActivityTitleChangeListener) {
            Log.d("NewCardPaymentFragment", "onAttach (line 131): OnActivityTitleChangeListener");
            ((OnActivityTitleChangeListener) activity).onActivityTitleChanged("Processing...");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        dismissDialog();

        mListener = null;
        mWebviewPayment = null;
        mUrl = null;
        mContext = null;
        mProgressDialog = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTransactionCompleteListener {
        public void onTransactionComplete(CitrusTransactionResponse transactionResponse);
    }

    /**
     * Handle all the Webview loading in custom webview client.
     */
    private class CitrusWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Let this webview handle all the urls loaded inside. Return false to denote that.
            view.loadUrl(url);

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // Display the message.
            showDialog("Processing your payment. Please do not refresh the page.", true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // Dismiss the progress/message dialog.
            dismissDialog();
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    }

    /**
     * This class will be loaded as JSInterface and the methods of this class will be called from
     * the javascript loaded inside webview.
     * <p/>
     * Handle the payment response and take actions accordingly.
     */
    private class JsInterface {

        @JavascriptInterface
        public void pgResponse(String response) {
            CitrusTransactionResponse transactionResponse = CitrusTransactionResponse.fromJSON(response);
            mListener.onTransactionComplete(transactionResponse);

        }
    }

}
