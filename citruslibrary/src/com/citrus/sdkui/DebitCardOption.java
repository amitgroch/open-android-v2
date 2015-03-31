package com.citrus.sdkui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by salil on 13/2/15.
 */
public final class DebitCardOption extends CardOption implements Parcelable {

    public static final Creator<DebitCardOption> CREATOR = new Creator<DebitCardOption>() {
        public DebitCardOption createFromParcel(Parcel source) {
            return new DebitCardOption(source);
        }

        public DebitCardOption[] newArray(int size) {
            return new DebitCardOption[size];
        }
    };

    public DebitCardOption() {
    }

    /**
     * @param cardHolderName - Name of the card holder.
     * @param cardNumber     - Card number.
     * @param cardCVV        - CVV of the card. We do not store CVV at our end.
     * @param cardExpiry     - Expiry date in MM/YY format.
     */
    public DebitCardOption(String cardHolderName, String cardNumber, String cardCVV, String cardExpiry) {
        super(cardHolderName, cardNumber, cardCVV, cardExpiry);
    }

    /**
     * @param cardHolderName  - Name of the card holder.
     * @param cardNumber      - Card number.
     * @param cardCVV         - CVV of the card. We do not store CVV at our end.
     * @param cardExpiryMonth - Card Expiry Month 01 to 12 e.g. 01 for January.
     * @param cardExpiryYear  - Card Expiry Year in the form of YYYY e.g. 2015.
     */
    public DebitCardOption(String cardHolderName, String cardNumber, String cardCVV, String cardExpiryMonth, String cardExpiryYear) {
        super(cardHolderName, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear);
    }

    /**
     * This constructor will be used internally, mostly to display the saved card details.
     *
     * @param name           - User friendly name of the card. e.g. Debit Card (4242) or Credit Card (1234)
     * @param token          - Stored token for Card payment.
     * @param cardHolderName - Name of the card holder.
     * @param cardNumber     - Card number
     * @param cardScheme     - Card scheme e.g. VISA, MASTER etc.
     * @param cardExpiry     - Card expiry date. In MMYYYY format.
     */
    public DebitCardOption(String name, String token, String cardHolderName, String cardNumber, String cardScheme, String cardExpiry) {
        super(name, token, cardHolderName, cardNumber, cardScheme, cardExpiry);
    }


    private DebitCardOption(Parcel in) {
        this.cardHolderName = in.readString();
        this.cardNumber = in.readString();
        this.cardCVV = in.readString();
        this.cardExpiry = in.readString();
        this.cardExpiryMonth = in.readString();
        this.cardExpiryYear = in.readString();
        this.cardScheme = in.readString();
        this.name = in.readString();
        this.token = in.readString();
        this.savePaymentOption = in.readByte() != 0;
    }

    @Override
    public String getCardType() {
        return CardType.DEBIT.getCardType();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardHolderName);
        dest.writeString(this.cardNumber);
        dest.writeString(this.cardCVV);
        dest.writeString(this.cardExpiry);
        dest.writeString(this.cardExpiryMonth);
        dest.writeString(this.cardExpiryYear);
        dest.writeString(this.cardScheme);
        dest.writeString(this.name);
        dest.writeString(this.token);
        dest.writeByte(savePaymentOption ? (byte) 1 : (byte) 0);
    }
}