package com.citrus.sdkui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.card.Card;
import com.citrus.mobile.Callback;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citruspay.citruslibrary.R;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProcessPaymentListener} interface
 * to handle interaction events.
 * Use the {@link SavedCardPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedCardPaymentFragment extends Fragment {
    private ProcessPaymentListener mListener;
    private CardOption mSavedCard = null;
    private CitrusPaymentParams mPaymentParams;

    private ProgressDialog mProgressDialog = null;
    private CheckBox mCheckCVV1 = null;
    private CheckBox mCheckCVV2 = null;
    private CheckBox mCheckCVV3 = null;
    private CheckBox mCheckCVV4 = null;
    private EditText mEditCVVHidden = null;
    private boolean mProcessPayment = false;
    private int mCVVLength = Constants.CVV_LENGTH;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (start == 0 && before == 0) {
                mCheckCVV1.setChecked(true);
            } else if (start == 1 && before == 0) {
                mCheckCVV2.setChecked(true);
            } else if (start == 2 && before == 0) {
                mCheckCVV3.setChecked(true);

                // Check the maximum digits for most of the cards.
                if (start == mCVVLength - 1) {
                    // Hide the keyboard.
                    hideKeyboard();

                    // Denote that payment can be processed now.
                    mProcessPayment = true;
                }
            } else if (start == 3 && before == 0) {
                mCheckCVV4.setChecked(true);
                // 4 digit CVV only for AMEX card.
                hideKeyboard();

                // Denote that payment can be processed now.
                mProcessPayment = true;
            }

            if (start == 0 && before == 1) {
                mCheckCVV1.setChecked(false);
            } else if (start == 1 && before == 1) {
                mCheckCVV2.setChecked(false);
            } else if (start == 2 && before == 1) {
                mCheckCVV3.setChecked(false);
            } else if (start == 3 && before == 1) {
                mCheckCVV4.setChecked(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String mCVV = s.toString();

            if (mProcessPayment) {
                mSavedCard.setCardCVV(mCVV);
                processPayment(mSavedCard);
            }
        }
    };

    public SavedCardPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SaveCardPaymentFragment.
     */
    public static SavedCardPaymentFragment newInstance(CardOption cardOption, CitrusPaymentParams paymentParams) {
        SavedCardPaymentFragment fragment = new SavedCardPaymentFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_SAVED_CARD, cardOption);
        args.putParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPaymentParams = getArguments().getParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS);
            mSavedCard = getArguments().getParcelable(Constants.PARAM_SAVED_CARD);
            if (mSavedCard != null) {
                mCVVLength = mSavedCard.getCVVLength();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_card_payment, container, false);


        mProgressDialog = new ProgressDialog(getActivity());

        ImageView imgCardType = (ImageView) view.findViewById(R.id.img_card_logo);
        TextView cardNumber = (TextView) view.findViewById(R.id.txt_card_number);
        TextView cardHolder = (TextView) view.findViewById(R.id.txt_card_holder);
        TextView cardExpiry = (TextView) view.findViewById(R.id.txt_card_expiry);

        mCheckCVV1 = (CheckBox) view.findViewById(R.id.check_cvv_1);
        mCheckCVV2 = (CheckBox) view.findViewById(R.id.check_cvv_2);
        mCheckCVV3 = (CheckBox) view.findViewById(R.id.check_cvv_3);
        mCheckCVV4 = (CheckBox) view.findViewById(R.id.check_cvv_4);
        mEditCVVHidden = (EditText) view.findViewById(R.id.edit_cvv_hidden);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgCardType.setBackground(mSavedCard.getOptionIcon(getActivity()));
        } else {
            imgCardType.setBackgroundDrawable(mSavedCard.getOptionIcon(getActivity()));
        }

        if (mCVVLength == 4) {
            mCheckCVV4.setVisibility(View.VISIBLE);
        }

        // Set the text change listener on the CVV field.
        mEditCVVHidden.addTextChangedListener(textWatcher);
        mEditCVVHidden.requestFocus();
        showKeyboard();

        // Set the font
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ocraextended.ttf");
        cardNumber.setTypeface(tf);
        cardExpiry.setTypeface(tf);
        cardHolder.setTypeface(tf);

        String s = mSavedCard.getCardNumber();
        String s1 = s.substring(0, 4);
        String s2 = s.substring(4, 8);
        String s3 = s.substring(8, 12);
        String s4 = s.substring(12, s.length());

        cardNumber.setText(s1 + " " + s2 + " " + s3 + " " + s4);
        if (!TextUtils.isEmpty(mSavedCard.getCardHolderName())) {
            cardHolder.setText(mSavedCard.getCardHolderName().toUpperCase(Locale.getDefault()));
        }
        cardExpiry.setText(mSavedCard.getCardExpiryMonth() + "/" + mSavedCard.getCardExpiryYear());

        return view;
    }

    private void showKeyboard() {
        mEditCVVHidden.setFocusableInTouchMode(true);
        mEditCVVHidden.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ProcessPaymentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCardPaymentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
        mProgressDialog = null;
        mCheckCVV1 = null;
        mCheckCVV2 = null;
        mCheckCVV3 = null;
        mCheckCVV4 = null;
        mEditCVVHidden = null;
    }

    private void processPayment(CardOption cardOption) {

        if (cardOption != null) {
            final Card card = new Card(cardOption.getToken(), cardOption.getCardCVV());

            new GetBill(mPaymentParams.billUrl, mPaymentParams.transactionAmount, new Callback() {
                @Override
                public void onTaskexecuted(String billString, String error) {
                    Bill bill;
                    if (TextUtils.isEmpty(error)) {
                        bill = new Bill(billString);
                        UserDetails userDetails = new UserDetails(CitrusUser.toJSONObject(mPaymentParams.user));

                        PG paymentGateway = new PG(card, bill, userDetails);

                        paymentGateway.charge(new Callback() {
                            @Override
                            public void onTaskexecuted(String success, String error) {
                                mListener.processPayment(success, error);

                                dismissDialog();
                            }
                        });
                    } else {
                        Utils.showToast(getActivity(), error);
                    }
                }
            }).execute();

            showDialog("Processing Payment. Please wait...", true);
        }
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
}