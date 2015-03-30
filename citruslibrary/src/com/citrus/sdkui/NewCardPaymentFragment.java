package com.citrus.sdkui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.citrus.asynch.Savecard;
import com.citrus.card.Card;
import com.citrus.mobile.Callback;
import com.citrus.mobile.User;
import com.citrus.payment.Bill;
import com.citrus.payment.PG;
import com.citrus.payment.UserDetails;
import com.citruspay.citruslibrary.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPaymentOptionSelectedListener} interface
 * to handle interaction events.
 * Use the {@link NewCardPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public final class NewCardPaymentFragment extends Fragment implements View.OnClickListener {
    private ProcessPaymentListener mListener = null;
    private CitrusPaymentParams mPaymentParams = null;

    private RadioGroup mRadioGroup = null;
    private Button mButtonPay = null;
    private EditText mEditNameOnCard = null;
    private EditText mEditCardNo = null;
    private EditText mEditCVV = null;
    private Spinner mSpinnerMonth = null;
    private Spinner mSpinnerYear = null;
    private ProgressDialog mProgressDialog = null;

    public NewCardPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CardPaymentFragment.
     */
    public static NewCardPaymentFragment newInstance(CitrusPaymentParams paymentParams) {

        NewCardPaymentFragment fragment = new NewCardPaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS, paymentParams);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPaymentParams = getArguments().getParcelable(Constants.INTENT_EXTRA_PAYMENT_PARAMS);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_new_card_payment, container, false);
        mSpinnerMonth = (Spinner) rootView.findViewById(R.id.month);
        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<CharSequence>(
                getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.months_array));

        // Specify the layout to use when the list of choices appears
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerMonth.setAdapter(monthAdapter);
        mSpinnerYear = (Spinner) rootView.findViewById(R.id.year);
        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter<CharSequence>(
                getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.years_array));
        // Specify the layout to use when the list of choices appears
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerYear.setAdapter(yearAdapter);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_card_type);
        mEditCardNo = (EditText) rootView.findViewById(R.id.edit_card_no);
        mEditNameOnCard = (EditText) rootView.findViewById(R.id.edit_name_on_card);
        mEditCVV = (EditText) rootView.findViewById(R.id.edit_cvv);
        mButtonPay = (Button) rootView.findViewById(R.id.button_pay);

        // TODO Setting color tint for views.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mPaymentParams != null && mPaymentParams.accentColor != null) {
//            mEditCardNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mPaymentParams.accentColor)));
//            mEditNameOnCard.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mPaymentParams.accentColor)));
//            mEditCVV.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mPaymentParams.accentColor)));
//            mRadioGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mPaymentParams.accentColor)));
//        }

        mButtonPay.setOnClickListener(this);
        if (mPaymentParams != null) {
            mButtonPay.setBackgroundColor(Color.parseColor(mPaymentParams.colorPrimary));
        }

        mProgressDialog = new ProgressDialog(getActivity());

        // Inflate the layout for this fragment
        return rootView;
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

        // Check whether the activity has implemented the OnActivityTitleChangeListener.
        // Call the onActivityTitleChanged to change the title of the activity
        if (activity instanceof OnActivityTitleChangeListener) {
            Log.d("NewCardPaymentFragment", "onAttach (line 131): OnActivityTitleChangeListener");
            ((OnActivityTitleChangeListener) activity).onActivityTitleChanged("Add Card");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int selectedCardTypeId = mRadioGroup.getCheckedRadioButtonId();
        String cardName = mEditNameOnCard.getText().toString();
        String cardNumber = mEditCardNo.getText().toString();
        String cardCVV = mEditCVV.getText().toString();
        String cardExpiryYear = mSpinnerYear.getSelectedItem().toString();
        String cardExpiryMonth;

        int expiryMonth = mSpinnerMonth.getSelectedItemPosition() + 1;
        if (expiryMonth < 10) {
            cardExpiryMonth = String.format("%02d", expiryMonth);
        } else {
            cardExpiryMonth = String.valueOf(expiryMonth);
        }

        // Validate the enter card details and if valid then proceed.
        if (!validate(cardName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear, selectedCardTypeId)) {
            return;
        }

        CardOption cardOption = null;

        if (selectedCardTypeId == R.id.radio_credit_card) {
            cardOption = new CreditCardOption(cardName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
        } else if (selectedCardTypeId == R.id.radio_debit_card) {
            cardOption = new DebitCardOption(cardName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
        }

        processPayment(cardOption);
    }

    private void processPayment(CardOption cardOption) {

        if (cardOption != null) {
            final Card card = new Card(cardOption.getCardNumber(), cardOption.getCardExpiryMonth(), cardOption.getCardExpiryYear(), cardOption.getCardCVV(), cardOption.getCardHolderName(), cardOption.getCardType());

            showDialog("Processing your payment", false);

            // Process card payment only if the card is valid.
            if (card.validateCard()) {

                // Get bill json
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
                                    dismissDialog();

                                    if (!TextUtils.isEmpty(success)) {
                                        mListener.processPayment(success, error);
                                    } else {
                                        Utils.showToast(getActivity(), error);
                                    }
                                }
                            });
                        } else {
                            Log.e(Utils.TAG, error);
                            Utils.showToast(getActivity(), error);
                        }
                    }
                }).execute();

                // Save the card if the user has opted to save the card.
                // Currently we will be saving card every time, so this will always return true.
                if (cardOption.isSavePaymentOption()) {
                    saveCard(card);
                }
            } else {
                dismissDialog();
                Utils.showToast(getActivity(), "Invalid Card. Please check card details.");
            }
        }
    }

    private void saveCard(Card card) {
        if (User.isUserLoggedIn(getActivity())) {
            new Savecard(getActivity(), new Callback() {
                @Override
                public void onTaskexecuted(String success, String error) {
                    if (!TextUtils.isEmpty(success)) {
                        Utils.showToast(getActivity(), "Card Saved Successfully.");
                    } else {
                        Utils.showToast(getActivity(), "Error saving the card.");
                    }
                }
            }).execute(card);
        }
    }

    private boolean validate(String cardName, String cardNumber, String cardCVV, String cardExpiryMonth, String cardExpiryYear, int selectedCardTypeId) {
        boolean valid = true;

        // TODO: Validation of the expiryMonth and expiryYear
        if (TextUtils.isEmpty(cardName)) {
            valid = false;
            mEditNameOnCard.setError("Please Enter Name On The Card.");
        }

        if (TextUtils.isEmpty(cardNumber)) {
            valid = false;
            mEditCardNo.setError("Please Enter Valid Card Number");
        }

        if (TextUtils.isEmpty(cardCVV)) {
            valid = false;
            mEditCVV.setError("Please Enter CVV.");
        }

        if (selectedCardTypeId == -1) {
            valid = false;

            for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
                ((RadioButton) (mRadioGroup.getChildAt(i))).setError("Please Select The Type of Card.");
            }
        }

        if (!valid) {
            Utils.showToast(getActivity(), "Please enter valid card details!");
        }

        return valid;
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
