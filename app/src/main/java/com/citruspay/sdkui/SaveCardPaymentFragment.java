package com.citruspay.sdkui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdkui.CardOption;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveCardPaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveCardPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveCardPaymentFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private CardOption mSavedCard = null;

    private TextView mCardNumber = null;
    private TextView mCardHolder = null;
    private TextView mCardExpiry = null;
    private ImageView mImgCardType = null;
    private CheckBox mCheckCVV1 = null;
    private CheckBox mCheckCVV2 = null;
    private CheckBox mCheckCVV3 = null;
    private CheckBox mCheckCVV4 = null;
    private EditText mEditCVVHidden = null;
    private Button mBtnPay = null;
    private String mCVV = ""; // This will store the CVV entered by the user.
    private int mMaxDigitCVV = 3;

    public SaveCardPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SaveCardPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveCardPaymentFragment newInstance(CardOption cardOption) {
        SaveCardPaymentFragment fragment = new SaveCardPaymentFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.PARAM_SAVED_CARD, cardOption);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSavedCard = getArguments().getParcelable(Constants.PARAM_SAVED_CARD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_card_payment, container, false);

        mCardNumber = (TextView) view.findViewById(R.id.txt_card_number);
        mCardHolder = (TextView) view.findViewById(R.id.txt_card_holder);
        mCardExpiry = (TextView) view.findViewById(R.id.txt_card_expiry);
        mImgCardType = (ImageView) view.findViewById(R.id.img_card_logo);
        mCheckCVV1 = (CheckBox) view.findViewById(R.id.check_cvv_1);
        mCheckCVV2 = (CheckBox) view.findViewById(R.id.check_cvv_2);
        mCheckCVV3 = (CheckBox) view.findViewById(R.id.check_cvv_3);
        mCheckCVV4 = (CheckBox) view.findViewById(R.id.check_cvv_4);
        mEditCVVHidden = (EditText) view.findViewById(R.id.edit_cvv_hidden);
        mBtnPay = (Button) view.findViewById(R.id.btn_pay);

        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CVV :: " + mCVV, Toast.LENGTH_SHORT).show();
            }
        });

//        mCheckCVV1.setOnClickListener(onClickListener);
//        mCheckCVV2.setOnClickListener(onClickListener);
//        mCheckCVV3.setOnClickListener(onClickListener);
//        mCheckCVV4.setOnClickListener(onClickListener);

        if (mMaxDigitCVV == 4) {
            mCheckCVV4.setVisibility(View.VISIBLE);
        }

        // Set the text change listener on the CVV field.
        mEditCVVHidden.addTextChangedListener(textWatcher);
        mEditCVVHidden.requestFocus();
        showKeyboard();

        // Set the font
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ocraextended.ttf");
        mCardNumber.setTypeface(tf);
        mCardExpiry.setTypeface(tf);
        mCardHolder.setTypeface(tf);

        String s = mSavedCard.getCardNumber();
        String s1 = s.substring(0, 4);
        String s2 = s.substring(4, 8);
        String s3 = s.substring(8, 12);
        String s4 = s.substring(12, s.length());

        mCardNumber.setText(s1 + " " + s2 + " " + s3 + " " + s4);
        mCardHolder.setText(mSavedCard.getCardHolderName());
        mCardExpiry.setText(mSavedCard.getCardExpiry());

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
                if (start == mMaxDigitCVV - 1) {
                    // Hide the keyboard.
//                    hideKeyboard();
                }
            } else if (start == 3 && before == 0) {
                mCheckCVV4.setChecked(true);
                // 4 digit CVV only for AMEX card.
//                hideKeyboard();
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
            mCVV = s.toString(); // Assign the CVV.
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showKeyboard();

            // Reset all the checkboxes.
            mCheckCVV1.setChecked(false);
            mCheckCVV2.setChecked(false);
            mCheckCVV3.setChecked(false);
            mCheckCVV4.setChecked(false);

            // Reset the CVV.
            mCVV = "";
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
