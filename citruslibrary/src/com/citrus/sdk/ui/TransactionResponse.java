package com.citrus.sdk.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.citrus.sdk.ui.classes.CitrusUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by salil on 16/2/15.
 */
public final class TransactionResponse implements Parcelable {

    public static final Parcelable.Creator<TransactionResponse> CREATOR = new Parcelable.Creator<TransactionResponse>() {
        public TransactionResponse createFromParcel(Parcel source) {
            return new TransactionResponse(source);
        }

        public TransactionResponse[] newArray(int size) {
            return new TransactionResponse[size];
        }
    };

    private String amount = null;
    private String currency = null;
    private String message = null;
    private String responseCode = null;
    private TransactionStatus transactionStatus = null;
    private TransactionDetails transactionDetails = null;
    private CitrusUser citrusUser = null;
    private PaymentMode paymentMode = null;
    private String issuerCode = null;
    private String impsMobileNumber = null;
    private String impsMmid = null;
    private String authIdCode = null;
    private String signature = null;
    private boolean COD = false; // Cash On Delivery
    private Map<String, String> customParamsMap = null;
    private String jsonResponse = null;

    private TransactionResponse() {

    }

    /**
     * This constructor will be used internally.
     *
     * @param transactionStatus
     * @param message
     * @param transactionId
     */
    TransactionResponse(TransactionStatus transactionStatus, String message, String transactionId) {
        this.transactionStatus = transactionStatus;
        this.message = message;
        this.transactionDetails = new TransactionDetails(transactionId);
    }

    /**
     * @param amount
     * @param currency
     * @param message
     * @param responseCode
     * @param transactionStatus
     * @param transactionDetails
     * @param citrusUser
     * @param paymentMode
     * @param issuerCode
     * @param impsMobileNumber
     * @param impsMmid
     * @param authIdCode
     * @param signature
     * @param COD                - cash on delivery.
     * @param customParamsMap    - custom parameters sent with request.
     */
    private TransactionResponse(String amount, String currency, String message, String responseCode, TransactionStatus transactionStatus, TransactionDetails transactionDetails, CitrusUser citrusUser, PaymentMode paymentMode, String issuerCode, String impsMobileNumber, String impsMmid, String authIdCode, String signature, boolean COD, Map<String, String> customParamsMap) {
        this.amount = amount;
        this.currency = currency;
        this.message = message;
        this.responseCode = responseCode;
        this.transactionStatus = transactionStatus;
        this.transactionDetails = transactionDetails;
        this.citrusUser = citrusUser;
        this.paymentMode = paymentMode;
        this.issuerCode = issuerCode;
        this.impsMobileNumber = impsMobileNumber;
        this.impsMmid = impsMmid;
        this.authIdCode = authIdCode;
        this.signature = signature;
        this.COD = COD;
        this.customParamsMap = customParamsMap;
    }

    private TransactionResponse(Parcel in) {
        this.amount = in.readString();
        this.currency = in.readString();
        this.message = in.readString();
        this.responseCode = in.readString();
        int tmpTransactionStatus = in.readInt();
        this.transactionStatus = tmpTransactionStatus == -1 ? null : TransactionStatus.values()[tmpTransactionStatus];
        this.transactionDetails = in.readParcelable(TransactionDetails.class.getClassLoader());
        this.citrusUser = in.readParcelable(CitrusUser.class.getClassLoader());
        int tmpPaymentMode = in.readInt();
        this.paymentMode = tmpPaymentMode == -1 ? null : PaymentMode.values()[tmpPaymentMode];
        this.issuerCode = in.readString();
        this.impsMobileNumber = in.readString();
        this.impsMmid = in.readString();
        this.authIdCode = in.readString();
        this.signature = in.readString();
        this.COD = in.readByte() != 0;
//        this.customParamsMap = in.readParcelable(HashMap<String, String>.class.getClassLoader());
        this.jsonResponse = in.readString();
    }

    public static TransactionResponse fromJSON(String response) {
        TransactionResponse transactionResponse = null;

        try {
            if (response != null) {
                JSONObject jsonObject = new JSONObject(response);

                PaymentMode paymentMode = PaymentMode.getPaymentMode(jsonObject.optString("paymentMode"));
                TransactionStatus transactionStatus = TransactionStatus.getTransactionStatus(jsonObject.optString("TxStatus"));
                String currency = jsonObject.optString("currency");
                String amount = jsonObject.optString("amount");
                String responseCode = jsonObject.optString("pgRespCode");
                String message = jsonObject.optString("TxMsg");
                String isCOD = jsonObject.optString("isCOD");
                String signature = jsonObject.optString("signature");
                String issuerCode = jsonObject.optString("issuerCode");
                String impsMmid = jsonObject.optString("impsMmid");
                String impsMobileNumber = jsonObject.optString("impsMobileNumber");
                String authIdCode = jsonObject.optString("authIdCode");
                // TODO Need to parse custom parameters
                Map<String, String> customParamsMap = null;

                TransactionDetails transactionDetails = TransactionDetails.fromJSONObject(jsonObject);
                CitrusUser citrusUser = CitrusUser.fromJSONObject(jsonObject);
                boolean cod = "true".equalsIgnoreCase(isCOD) ? true : false;

                transactionResponse = new TransactionResponse(amount, currency, message, responseCode, transactionStatus, transactionDetails, citrusUser, paymentMode, issuerCode, impsMobileNumber, impsMmid, authIdCode, signature, cod, customParamsMap);
                transactionResponse.setJsonResponse(jsonObject.toString());

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            transactionResponse = new TransactionResponse();
            transactionResponse.setJsonResponse(response);
        }

        return transactionResponse;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public CitrusUser getCitrusUser() {
        return citrusUser;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMessage() {
        return message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public boolean isCOD() {
        return COD;
    }

    public String getImpsMobileNumber() {
        return impsMobileNumber;
    }

    public String getIssuerCode() {
        return issuerCode;
    }

    public String getImpsMmid() {
        return impsMmid;
    }

    public String getAuthIdCode() {
        return authIdCode;
    }

    public String getSignature() {
        return signature;
    }

    public Map<String, String> getCustomParamsMap() {
        return customParamsMap;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    @Override
    public String toString() {
        return "CitrusTransactionResponse{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", message='" + message + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", transactionStatus=" + transactionStatus +
                ", transactionDetails=" + transactionDetails +
                ", citrusUser=" + citrusUser +
                ", paymentMode=" + paymentMode +
                ", issuerCode='" + issuerCode + '\'' +
                ", impsMobileNumber='" + impsMobileNumber + '\'' +
                ", impsMmid='" + impsMmid + '\'' +
                ", authIdCode='" + authIdCode + '\'' +
                ", signature='" + signature + '\'' +
                ", COD=" + COD +
                ", customParamsMap=" + customParamsMap +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.amount);
        dest.writeString(this.currency);
        dest.writeString(this.message);
        dest.writeString(this.responseCode);
        dest.writeInt(this.transactionStatus == null ? -1 : this.transactionStatus.ordinal());
        dest.writeParcelable(this.transactionDetails, flags);
        dest.writeParcelable(this.citrusUser, 0);
        dest.writeInt(this.paymentMode == null ? -1 : this.paymentMode.ordinal());
        dest.writeString(this.issuerCode);
        dest.writeString(this.impsMobileNumber);
        dest.writeString(this.impsMmid);
        dest.writeString(this.authIdCode);
        dest.writeString(this.signature);
        dest.writeByte(COD ? (byte) 1 : (byte) 0);
//        dest.writeParcelable(this.customParamsMap, flags);
        dest.writeString(this.jsonResponse);
    }

    public static enum PaymentMode {
        NET_BANKING, CREDIT_CARD, DEBIT_CARD;

        public static PaymentMode getPaymentMode(String paymentMode) {
            PaymentMode mode = null;
            if (TextUtils.equals(paymentMode, "NET_BANKING")) {
                mode = NET_BANKING;
            } else if (TextUtils.equals(paymentMode, "CREDIT_CARD")) {
                mode = CREDIT_CARD;
            } else if (TextUtils.equals(paymentMode, "DEBIT_CARD")) {
                mode = DEBIT_CARD;
            }

            return mode;
        }
    }

    public static enum TransactionStatus {
        SUCCESS, FAIL;

        public static TransactionStatus getTransactionStatus(String transactionStatus) {
            TransactionStatus status = null;
            if (TextUtils.equals(transactionStatus, "SUCCESS")) {
                status = SUCCESS;
            } else if (TextUtils.equals(transactionStatus, "FAIL")) {
                status = FAIL;
            }

            return status;
        }
    }

    public static class TransactionDetails implements Parcelable {
        public static final Creator<TransactionDetails> CREATOR = new Creator<TransactionDetails>() {
            public TransactionDetails createFromParcel(Parcel source) {
                return new TransactionDetails(source);
            }

            public TransactionDetails[] newArray(int size) {
                return new TransactionDetails[size];
            }
        };
        private String transactionId = null;
        private String txRefNo = null;
        private String pgTxnNo = null;
        private String issuerRefNo = null;
        private String transactionGateway = null;
        private String transactionDateTime = null;

        /**
         * @param transactionId
         */
        TransactionDetails(String transactionId) {
            this.transactionId = transactionId;
        }

        public TransactionDetails(String transactionId, String txRefNo, String pgTxnNo, String issuerRefNo, String transactionGateway, String transactionDateTime) {
            this.transactionId = transactionId;
            this.txRefNo = txRefNo;
            this.pgTxnNo = pgTxnNo;
            this.issuerRefNo = issuerRefNo;
            this.transactionGateway = transactionGateway;
            this.transactionDateTime = transactionDateTime;
        }

        private TransactionDetails(Parcel in) {
            this.transactionId = in.readString();
            this.txRefNo = in.readString();
            this.pgTxnNo = in.readString();
            this.issuerRefNo = in.readString();
            this.transactionGateway = in.readString();
            this.transactionDateTime = in.readString();
        }

        public static TransactionDetails fromJSONObject(JSONObject response) {
            TransactionDetails transactionDetails = null;

            if (response != null) {
                String txRefNo = response.optString("TxRefNo");
                String pgTxnNo = response.optString("pgTxnNo");
                String issuerRefNo = response.optString("issuerRefNo");
                String txGateway = response.optString("TxGateway");
                String txnDateTime = response.optString("txnDateTime");
                String txId = response.optString("TxId");

                transactionDetails = new TransactionDetails(txId, txRefNo, pgTxnNo, issuerRefNo, txGateway, txnDateTime);
            }

            return transactionDetails;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getTxRefNo() {
            return txRefNo;
        }

        public String getPgTxnNo() {
            return pgTxnNo;
        }

        public String getIssuerRefNo() {
            return issuerRefNo;
        }

        public String getTransactionGateway() {
            return transactionGateway;
        }

        public String getTransactionDateTime() {
            return transactionDateTime;
        }

        @Override
        public String toString() {
            return "TransactionDetails{" +
                    "transactionId='" + transactionId + '\'' +
                    ", txRefNo='" + txRefNo + '\'' +
                    ", pgTxnNo='" + pgTxnNo + '\'' +
                    ", issuerRefNo='" + issuerRefNo + '\'' +
                    ", transactionGateway='" + transactionGateway + '\'' +
                    ", transactionDateTime='" + transactionDateTime + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.transactionId);
            dest.writeString(this.txRefNo);
            dest.writeString(this.pgTxnNo);
            dest.writeString(this.issuerRefNo);
            dest.writeString(this.transactionGateway);
            dest.writeString(this.transactionDateTime);
        }
    }
}
