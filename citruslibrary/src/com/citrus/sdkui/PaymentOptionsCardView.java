package com.citrus.sdkui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrus.sdkui.classes.CardOption;
import com.citrus.sdkui.classes.CitrusCash;
import com.citrus.sdkui.classes.CreditCardOption;
import com.citrus.sdkui.classes.DebitCardOption;
import com.citrus.sdkui.classes.NetbankingOption;
import com.citrus.sdkui.classes.PaymentOption;
import com.citruspay.citruslibrary.R;

import java.util.List;

/**
 * Created by salil on 2/3/15.
 */
public final class PaymentOptionsCardView extends CardView implements View.OnClickListener {

    private Context mContext = null;
    private OnPaymentOptionSelectedListener mListener = null;

    public PaymentOptionsCardView(Context context) {
        super(context);

        mContext = context;
    }

    public PaymentOptionsCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
    }

    public PaymentOptionsCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
    }

    public void init(OnPaymentOptionSelectedListener listener, PaymentOptionsType paymentOptionsCardType, CitrusPaymentParams paymentParams) {

        mListener = listener;

        String colorPrimary = null;
        if (paymentParams != null) {
            colorPrimary = paymentParams.colorPrimary;
        }

        View.inflate(mContext, R.layout.payment_option_cards_layout, this);  //correct way to inflate..
        TextView txtHeader = (TextView) findViewById(R.id.txt_card_header);
        Button btnFooter = (Button) findViewById(R.id.btn_footer);
        btnFooter.setOnClickListener(this);
        LinearLayout layoutPaymentOptions = (LinearLayout) findViewById(R.id.layout_payment_options);

        // Initialize the card view for the particular payment option.
        if (paymentOptionsCardType != null) {
            List<? extends PaymentOption> paymentOptionsList;

            txtHeader.setText(paymentOptionsCardType.getHeaderText());
            btnFooter.setText(paymentOptionsCardType.getFooterText());

            // Set the app theme color.
            if (colorPrimary != null) {
                btnFooter.setTextColor(Color.parseColor(colorPrimary));
            }

            // Initializing the list of payment options
            switch (paymentOptionsCardType) {

                case CITRUS_CASH:
                    paymentOptionsList = paymentOptionsCardType.getPaymentOptionsList();
                    if (paymentOptionsList != null && !paymentOptionsList.isEmpty()) {
                        CitrusCash citrusCash = (CitrusCash) paymentOptionsList.get(0);

                        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.payment_options_layout, null, false);
                        ((TextView) relativeLayout.findViewById(R.id.txt_payment_name)).setText(citrusCash.getAmount());
                        ((TextView) relativeLayout.findViewById(R.id.txt_payment_name)).setTextColor(Color.parseColor("#77C83E"));

                        ((TextView) relativeLayout.findViewById(R.id.txt_bank_name)).setText("Your Balance");
                        relativeLayout.findViewById(R.id.img_payment_logo).setBackgroundColor(Color.WHITE);
                        // Set the icon image
                        setBackgroundImage(relativeLayout.findViewById(R.id.img_payment_logo), citrusCash.getOptionIcon(mContext));
                        layoutPaymentOptions.addView(relativeLayout);

                        // Set the listener and add tag for pay now button.
                        btnFooter.setTag(citrusCash);
                        btnFooter.setOnClickListener(this);
                    } else {
                        TextView textView = new TextView(mContext);
                        textView.setPadding(16, 8, 0, 0);
                        // TODO: Need to change the message.
                        textView.setText("Seems you do not have Citrus Cash account!");
                        layoutPaymentOptions.addView(textView);
                    }
                    break;

                case SAVED_CARDS:
                    paymentOptionsList = paymentOptionsCardType.getPaymentOptionsList();
                    if (paymentOptionsList != null && !paymentOptionsList.isEmpty()) {
                        for (int i = 0; i < paymentOptionsList.size(); i++) {

                            PaymentOption paymentOption = paymentOptionsList.get(i);

                            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.payment_options_layout, null, false);

                            if (paymentOption instanceof CreditCardOption || paymentOption instanceof DebitCardOption) {
                                ((TextView) relativeLayout.findViewById(R.id.txt_payment_name)).setText(((CardOption) paymentOption).getCardNumber());
                                ((TextView) relativeLayout.findViewById(R.id.txt_bank_name)).setText(paymentOption.getName());
                            } else if (paymentOption instanceof NetbankingOption) {
                                ((TextView) relativeLayout.findViewById(R.id.txt_payment_name)).setText(paymentOption.getName());
                                ((TextView) relativeLayout.findViewById(R.id.txt_bank_name)).setText(((NetbankingOption) paymentOption).getBankName());
                            }

                            // Set the icon image
                            setBackgroundImage(relativeLayout.findViewById(R.id.img_payment_logo), paymentOption.getOptionIcon(mContext));
                            // Set click listener
                            relativeLayout.setOnClickListener(this);
                            // Set the tag
                            relativeLayout.setTag(paymentOption);
                            layoutPaymentOptions.addView(relativeLayout);
                        }
                    } else {
                        TextView textView = new TextView(mContext);
                        textView.setPadding(16, 8, 0, 0);
                        // TODO: Need to change the message.
                        textView.setText("You have not saved any card for faster checkout!");
                        layoutPaymentOptions.addView(textView);
                    }

                    // Set the listener for default and add card.
                    btnFooter.setTag(CardOption.DEFAULT_CARD);
                    btnFooter.setOnClickListener(this);
                    break;

                case DEBIT_CREDIT_CARDS:
                    // In case of credit and debit card, there will be no elements in the list.
                    // Hide the layout
                    layoutPaymentOptions.setVisibility(View.GONE);

                    // Set the listener for default and add card.
                    btnFooter.setTag(CardOption.DEFAULT_CARD);
                    btnFooter.setOnClickListener(this);
                    break;

                case NETBANKING:
                    paymentOptionsList = paymentOptionsCardType.getPaymentOptionsList();
                    if (paymentOptionsList != null && !paymentOptionsList.isEmpty()) {
                        for (int i = 0; i < paymentOptionsList.size(); i++) {
                            NetbankingOption netbankingOption = (NetbankingOption) paymentOptionsList.get(i);

                            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.payment_options_layout, null, false);

                            ((TextView) relativeLayout.findViewById(R.id.txt_payment_name)).setText(netbankingOption.getBankName());
                            // Hide the secondary field in case of banks
                            relativeLayout.findViewById(R.id.txt_bank_name).setVisibility(View.GONE);
                            // Set the icon image
                            setBackgroundImage(relativeLayout.findViewById(R.id.img_payment_logo), netbankingOption.getOptionIcon(mContext));
                            // Set click listener
                            relativeLayout.setOnClickListener(this);
                            // Set the tag
                            relativeLayout.setTag(netbankingOption);
                            layoutPaymentOptions.addView(relativeLayout);
                        }
                    } else {
                        TextView textView = new TextView(mContext);
                        textView.setPadding(16, 8, 0, 0);
                        // TODO: Need to change the message.
                        textView.setText("Merchant does not support netbanking payment.");
                        layoutPaymentOptions.addView(textView);
                    }

                    // Set the listener for default and add card.
                    btnFooter.setTag(NetbankingOption.DEFAULT_BANK);
                    btnFooter.setOnClickListener(this);
                    break;
            }
        }
    }

    private void setBackgroundImage(View view, Drawable drawable) {
        if (view != null && drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v != null && v.getTag() != null && v.getTag() instanceof PaymentOption && mListener != null) {
            PaymentOption paymentOption = (PaymentOption) v.getTag();
            mListener.onOptionSelected(paymentOption);
        }
    }

    /**
     * PaymentOptionType such as Citrus Cash, Debit Card, Credit Card, Netbanking etc.
     * EMI may be in future.
     * <p/>
     * Set specific header and footer text and payment options list for every type.
     */
    public static enum PaymentOptionsType {

        CITRUS_CASH {
            public String getHeaderText() {
                return headerText;
            }

            public String getFooterText() {
                return footerText;
            }

            public List<? extends PaymentOption> getPaymentOptionsList() {
                return paymentOptionList;
            }

            public void setHeaderText(String headerText) {
                this.headerText = headerText;
            }

            public void setFooterText(String footerText) {
                this.footerText = footerText;
            }

            public void setPaymentOptionList(List<? extends PaymentOption> paymentOptionList) {
                this.paymentOptionList = paymentOptionList;
            }
        },
        SAVED_CARDS {
            public String getHeaderText() {
                return headerText;
            }

            public String getFooterText() {
                return footerText;
            }

            public List<? extends PaymentOption> getPaymentOptionsList() {
                return paymentOptionList;
            }

            public void setHeaderText(String headerText) {
                this.headerText = headerText;
            }

            public void setFooterText(String footerText) {
                this.footerText = footerText;
            }

            public void setPaymentOptionList(List<? extends PaymentOption> paymentOptionList) {
                this.paymentOptionList = paymentOptionList;
            }
        },
        DEBIT_CREDIT_CARDS {
            public String getHeaderText() {
                return headerText;
            }

            public String getFooterText() {
                return footerText;
            }

            public List<? extends PaymentOption> getPaymentOptionsList() {
                return paymentOptionList;
            }

            public void setHeaderText(String headerText) {
                this.headerText = headerText;
            }

            public void setFooterText(String footerText) {
                this.footerText = footerText;
            }

            public void setPaymentOptionList(List<? extends PaymentOption> paymentOptionList) {
                this.paymentOptionList = paymentOptionList;
            }
        },
        NETBANKING {
            public String getHeaderText() {
                return headerText;
            }

            public String getFooterText() {
                return footerText;
            }

            public List<? extends PaymentOption> getPaymentOptionsList() {
                return paymentOptionList;
            }

            public void setHeaderText(String headerText) {
                this.headerText = headerText;
            }

            public void setFooterText(String footerText) {
                this.footerText = footerText;
            }

            public void setPaymentOptionList(List<? extends PaymentOption> paymentOptionList) {
                this.paymentOptionList = paymentOptionList;
            }
        };

        String headerText = null;
        String footerText = null;
        List<? extends PaymentOption> paymentOptionList = null;

        public abstract String getHeaderText();

        public abstract void setHeaderText(String headerText);

        public abstract String getFooterText();

        public abstract void setFooterText(String footerText);

        public abstract List<? extends PaymentOption> getPaymentOptionsList();

        public abstract void setPaymentOptionList(List<? extends PaymentOption> paymentOptionList);
    }
}
