package com.citrus.sdkui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.citruspay.citruslibrary.R;

/**
 * Created by salil on 13/2/15.
 */
public abstract class CardOption extends PaymentOption {

    protected String cardHolderName = null;
    protected String cardNumber = null;
    protected String cardCVV = null;
    protected String cardExpiry = null;
    protected String cardExpiryMonth = null;
    protected String cardExpiryYear = null;
    protected String cardScheme = null;

    /**
     * @param cardHolderName - Name of the card holder.
     * @param cardNumber     - Card number
     * @param cardCVV        - CVV of the card. We do not store CVV at our end.
     * @param cardExpiry     - Expiry date in MM/YY format.
     */
    public CardOption(String cardHolderName, String cardNumber, String cardCVV, String cardExpiry) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardCVV = cardCVV;
        this.cardExpiry = cardExpiry;

        if (!TextUtils.isEmpty(cardExpiry)) {
            String[] strArr = cardExpiry.split("/");
            if (strArr != null && strArr.length == 2) {
                cardExpiryMonth = strArr[0];
                cardExpiryYear = strArr[1];
            }
        }
    }

    /**
     * This constructor will be used internally, mostly for the cases of tokenized payments.
     *
     * @param name           - User friendly name of the card. e.g. Debit Card (4242) or Credit Card (1234)
     * @param token          - Stored token for Card payment.
     * @param cardHolderName - Name of the card holder.
     * @param cardNumber     - Card number
     * @param cardScheme     - Card scheme e.g. VISA, MASTER etc.
     * @param cardExpiry     - Card expiry date. In MMYYYY format.
     */
    public CardOption(String name, String token, String cardHolderName, String cardNumber, String cardScheme, String cardExpiry) {
        super(name, token);
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardScheme = cardScheme;

        // The received expiry date is in MMYYYY format so take out expiry month and year which will be required for display purpose.
        if (!TextUtils.isEmpty(cardExpiry)) {
            this.cardExpiryMonth = TextUtils.substring(cardExpiry, 0, 2);
            this.cardExpiryYear = TextUtils.substring(cardExpiry, 2, cardExpiry.length());
        }
    }

    public abstract String getCardType();

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardExpiryYear() {
        return cardExpiryYear;
    }

    public String getCardExpiryMonth() {

        return cardExpiryMonth;
    }

    public String getCardCVV() {

        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public Drawable getOptionIcon(Context context) {
        // Return the icon depending upon the scheme of the card.
        Drawable drawable = null;

        int resourceId = 0;
        if ("visa".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("visa", "drawable", context.getPackageName());
        } else if ("mcrd".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("mcrd", "drawable", context.getPackageName());
        } else if ("maestro".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("maestro", "drawable", context.getPackageName());
        } else if ("DINERCLUB".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("dinerclub", "drawable", context.getPackageName());
        } else if ("jcb".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("jcb", "drawable", context.getPackageName());
        } else if ("amex".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("amex", "drawable", context.getPackageName());
        } else if ("DISCOVER".equalsIgnoreCase(cardScheme)) {
            resourceId = context.getResources().getIdentifier("discover", "drawable", context.getPackageName());
        }

        if (resourceId == 0) {
            if ((resourceId = context.getResources().getIdentifier("default_card", "drawable", context.getPackageName())) != 0) {
                drawable = context.getResources().getDrawable(resourceId);
            }
        }  else {
            drawable = context.getResources().getDrawable(resourceId);
        }

        return drawable;
    }

    /**
     * Denotes the type of the card. i.e. Credit or Debit.
     */
    public static enum CardType {
        DEBIT {
            public String getCardType() {
                return "debit";
            }
        }, CREDIT {
            public String getCardType() {
                return "credit";
            }
        };

        /**
         * Get the type of the card in string, i.e. debit or credit.
         *
         * @return
         */
        public abstract String getCardType();
    }

}