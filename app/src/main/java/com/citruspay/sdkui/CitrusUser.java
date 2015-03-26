package com.citruspay.sdkui;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by salil on 16/2/15.
 */
public class CitrusUser implements Parcelable {

    private String firstName = null;
    private String lastName = null;
    private String emailId = null;
    private String mobileNo = null;
    private Address address = null;

    public CitrusUser(String emailId, String mobileNo) {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
    }

    public CitrusUser(String emailId, String mobileNo, String firstName, String lastName, Address address) {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public static CitrusUser fromJSONObject(JSONObject response) {
        CitrusUser user = null;

        if (response != null) {
            String email = response.optString("email");
            String mobileNo = response.optString("mobileNo");
            String firstName = response.optString("firstName");
            String lastName = response.optString("lastName");
            Address address = Address.fromJSONObject(response);

            user = new CitrusUser(email, mobileNo, firstName, lastName, address);
        }

        return user;
    }

    public static JSONObject toJSONObject(CitrusUser user) {
        JSONObject customer = null;
        // TODO: Need to put valid values from the user object passed
        /*
         * All the below mentioned parameters are mandatory - missing anyone of them may create errors Do not change the
		 * key in the json below - only change the values
		 */

        try {
            customer = new JSONObject();
            customer.put("firstName", "Tester");
            customer.put("lastName", "Citrus");
            customer.put("email", "tester@gmail.com");
            customer.put("mobileNo", "9170164284");
            customer.put("street1", "streetone");
            customer.put("street2", "streettwo");
            customer.put("city", "Mumbai");
            customer.put("state", "Maharashtra");
            customer.put("country", "India");
            customer.put("zip", "400052");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customer;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "CitrusUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", address=" + address +
                '}';
    }

    public static class Address implements Parcelable {

        private String street1 = null;
        private String street2 = null;
        private String city = null;
        private String state = null;
        private String country = null;
        private String zip = null;

        public Address(String street1, String street2, String city, String state, String country, String zip) {
            this.street1 = street1;
            this.street2 = street2;
            this.city = city;
            this.state = state;
            this.country = country;
            this.zip = zip;
        }

        public static Address fromJSONObject(JSONObject response) {
            Address address = null;

            if (response != null) {
                String addressCountry = response.optString("addressCountry");
                String addressState = response.optString("addressState");
                String addressCity = response.optString("addressCity");
                String addressStreet1 = response.optString("addressStreet1");
                String addressStreet2 = response.optString("addressStreet2");
                String addressZip = response.optString("addressZip");

                address = new Address(addressStreet1, addressStreet2, addressCity, addressState, addressCountry, addressZip);
            }

            return address;
        }

        public String getStreet1() {
            return street1;
        }

        public String getStreet2() {
            return street2;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public String getZip() {
            return zip;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street1='" + street1 + '\'' +
                    ", street2='" + street2 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", country='" + country + '\'' +
                    ", zip='" + zip + '\'' +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.street1);
            dest.writeString(this.street2);
            dest.writeString(this.city);
            dest.writeString(this.state);
            dest.writeString(this.country);
            dest.writeString(this.zip);
        }

        private Address(Parcel in) {
            this.street1 = in.readString();
            this.street2 = in.readString();
            this.city = in.readString();
            this.state = in.readString();
            this.country = in.readString();
            this.zip = in.readString();
        }

        public static final Creator<Address> CREATOR = new Creator<Address>() {
            public Address createFromParcel(Parcel source) {
                return new Address(source);
            }

            public Address[] newArray(int size) {
                return new Address[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.emailId);
        dest.writeString(this.mobileNo);
        dest.writeParcelable(this.address, flags);
    }

    private CitrusUser(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.emailId = in.readString();
        this.mobileNo = in.readString();
        this.address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Parcelable.Creator<CitrusUser> CREATOR = new Parcelable.Creator<CitrusUser>() {
        public CitrusUser createFromParcel(Parcel source) {
            return new CitrusUser(source);
        }

        public CitrusUser[] newArray(int size) {
            return new CitrusUser[size];
        }
    };
}