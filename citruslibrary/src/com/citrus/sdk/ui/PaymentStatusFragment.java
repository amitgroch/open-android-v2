package com.citrus.sdk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.Constants;
import com.citrus.sdk.ui.listeners.FragmentEventsListeners;
import com.citruspay.citruslibrary.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentStatusFragment.OnTransactionResponseListener} interface
 * to handle interaction events.
 * Use the {@link PaymentStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentStatusFragment extends Fragment implements View.OnClickListener {

    private OnTransactionResponseListener mListener;
    private TransactionResponse mTransactionResponse = null;
    private PaymentParams mPaymentParams = null;
    private ImageView imgTransactionStatus = null;

    public PaymentStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param transactionResponse Object of {@link TransactionResponse}
     * @return A new instance of fragment PaymentStatusFragment.
     */
    public static PaymentStatusFragment newInstance(TransactionResponse transactionResponse, PaymentParams paymentParams) {
        PaymentStatusFragment fragment = new PaymentStatusFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_TRANSACTION_RESPONSE, transactionResponse);
        args.putParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTransactionResponse = getArguments().getParcelable(Constants.PARAM_TRANSACTION_RESPONSE);
            mPaymentParams = getArguments().getParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_status, container, false);

        imgTransactionStatus = (ImageView) view.findViewById(R.id.img_transaction_status);
        TextView txtTransactionMessage = (TextView) view.findViewById(R.id.txt_transaction_message);
        TextView txtTitleTransactionId = (TextView) view.findViewById(R.id.txt_title_transaction_id);
        TextView txtTransactionId = (TextView) view.findViewById(R.id.txt_transaction_id);
        TextView txtTitleText2 = (TextView) view.findViewById(R.id.txt_title_text2);
        TextView txtText2 = (TextView) view.findViewById(R.id.txt_text2);
        TextView txtMessageGratitude = (TextView) view.findViewById(R.id.txt_message_gratitude);
        Button btnRetryTransaction = (Button) view.findViewById(R.id.btn_retry_transaction);
        Button btnDismiss = (Button) view.findViewById(R.id.btn_dismiss);

        btnRetryTransaction.setOnClickListener(this);
        btnDismiss.setOnClickListener(this);

        if (mTransactionResponse != null && mTransactionResponse.getTransactionDetails() != null) {

            Log.i("Citrus", "Transaction Response :: " + mTransactionResponse);

            if (mTransactionResponse.getTransactionStatus() == TransactionResponse.TransactionStatus.SUCCESS) {
                // Set the icon for transaction status.
                imgTransactionStatus.setBackgroundResource(R.drawable.checkmark_green);

                txtTransactionMessage.setText(getString(R.string.message_payment_successful));
                txtTitleTransactionId.setText(getString(R.string.title_transaction_id_success));
                txtTransactionId.setText(mTransactionResponse.getTransactionDetails().getTransactionId());
                txtTitleText2.setText(getString(R.string.title_text2_success));
                txtText2.setText(mTransactionResponse.getAmount());
                txtMessageGratitude.setVisibility(View.VISIBLE);

                setTitle(getString(R.string.fragment_title_transaction_success));

                // Hide the retry and dismiss buttons.
                btnRetryTransaction.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.GONE);
            } else {
                // Set the icon for transaction status
                imgTransactionStatus.setBackgroundResource(R.drawable.cross_red);

                txtTransactionMessage.setText(getString(R.string.message_payment_error));
                txtTitleTransactionId.setText(getString(R.string.title_transaction_id_error));
                txtTransactionId.setText(mTransactionResponse.getTransactionDetails().getTransactionId());
                txtTitleText2.setText(getString(R.string.title_text2_error));
                txtText2.setText(mTransactionResponse.getMessage());

                if (mPaymentParams != null) {
                    btnRetryTransaction.setTextColor(Color.parseColor(mPaymentParams.colorPrimary));
                }

                setTitle(getString(R.string.fragment_title_transaction_error));
            }
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTransactionResponseListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTransactionResponseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mTransactionResponse = null;
        mPaymentParams = null;
    }

    private void setTitle(String title) {
        // Check whether the activity has implemented the OnActivityTitleChangeListener.
        // Call the onActivityTitleChanged to change the title of the activity
        if (getActivity() instanceof FragmentEventsListeners) {
            Log.d("NewCardPaymentFragment", "onAttach (line 131): OnActivityTitleChangeListener");
            ((FragmentEventsListeners) getActivity()).onActivityTitleChanged(title);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_retry_transaction) {
            mListener.onRetryTransaction();
        } else if (id == R.id.btn_dismiss) {
            mListener.onDismiss();
        }
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
    public interface OnTransactionResponseListener {
        public void onRetryTransaction();

        public void onDismiss();
    }

}
