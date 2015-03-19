package com.citruspay.sdkui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
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
import android.widget.Toast;

import com.citrus.sdkui.CardOption;
import com.citrus.sdkui.CreditCardOption;
import com.citrus.sdkui.DebitCardOption;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.citruspay.sdkui.OnPaymentOptionSelectedListener} interface
 * to handle interaction events.
 * Use the {@link CardPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardPaymentFragment extends Fragment implements View.OnClickListener {
    private OnCardPaymentListener mListener = null;
    private CitrusPaymentParams mPaymentParams = null;

    private RadioGroup mRadioGroup = null;
    private Button mButtonPay = null;
    private EditText mEditNameOnCard = null;
    private EditText mEditCardNo = null;
    private EditText mEditCVV = null;
    private Spinner mSpinnerMonth = null;
    private Spinner mSpinnerYear = null;
    private SwitchCompat mSwitchToggleSaveCard = null;

    public CardPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CardPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardPaymentFragment newInstance(CitrusPaymentParams paymentParams) {

        CardPaymentFragment fragment = new CardPaymentFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_card_payment, container, false);
        mSpinnerMonth = (Spinner) rootView.findViewById(R.id.month);
        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<CharSequence>(
                getActivity(), R.layout.customtextview, android.R.id.text1, getResources().getStringArray(R.array.months_array));

        // Specify the layout to use when the list of choices appears
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerMonth.setAdapter(monthAdapter);
        mSpinnerYear = (Spinner) rootView.findViewById(R.id.year);
        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter<CharSequence>(
                getActivity(), R.layout.customtextview, android.R.id.text1, getResources().getStringArray(R.array.years_array));
        // Specify the layout to use when the list of choices appears
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerYear.setAdapter(yearAdapter);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group_card_type);
        mEditCardNo = (EditText) rootView.findViewById(R.id.edit_card_no);
        mEditNameOnCard = (EditText) rootView.findViewById(R.id.edit_name_on_card);
        mEditCVV = (EditText) rootView.findViewById(R.id.edit_cvv);
        mButtonPay = (Button) rootView.findViewById(R.id.button_pay);

        mButtonPay.setOnClickListener(this);
        if (mPaymentParams != null) {
            mButtonPay.setBackgroundColor(Color.parseColor(mPaymentParams.colorPrimary));
        }

        mSwitchToggleSaveCard = (SwitchCompat) rootView.findViewById(R.id.toggle_save_card);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCardPaymentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCardPaymentListener");
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
        String cardExpiryMonth = null;

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

        switch (selectedCardTypeId) {
            case R.id.radio_credit_card:
                cardOption = new CreditCardOption(cardName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
                break;

            case R.id.radio_debit_card:
                cardOption = new DebitCardOption(cardName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
                break;
        }

        // TODO: Take the proper card type somehow.
        // TODO: Also add the validations for every field, and validate the card here itself.

        cardOption.setSavePaymentOption(mSwitchToggleSaveCard.isChecked());
        mListener.onCardPaymentSelected(cardOption);
    }

    private boolean validate(String cardName, String cardNumber, String cardCVV, String cardExpiryMonth, String cardExpiryYear, int selectedCardTypeId) {
        boolean valid = true;

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
            Toast.makeText(getActivity(), "Please enter valid card details!", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    public static interface OnCardPaymentListener {
        public void onCardPaymentSelected(CardOption cardOption);
    }
}
