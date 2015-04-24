package com.citrus.mobile;

/**
 * Created by MANGESH KADAM on 4/23/2015.
 */
public enum Month {
    JAN("01"),
    FEB("02"),
    MAR("03"),
    APR("04"),
    MAY("05"),
    JUN("06"),
    JUL("07"),
    AUG("08"),
    SEP("09"),
    OCT("10"),
    NOV("11"),
    DEC("12");

    private final String month;

     Month(String month) {
         this.month = month;
    }

    @Override
    public String toString() {
        return month;
    }

}
