/*
   Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.citrus.card;


public class Card {
	private String cardnumber;
	private String cardCVV;
	private String nameOnCard;
	private String cardType;
	private String expMonth;
	private String expYear;
	private String crdr;
	private String token;

	public Card(String cardNum, String month, String year, String cvv, String name, String crdr) {
		this.cardnumber = normalizeCardNumber(cardNum);
		this.cardCVV = cvv;
		this.nameOnCard = name;
		this.expMonth = month;
		this.expYear = year;
		this.crdr = crdr;
	}

	public Card(String token, String cvv) {
		this.token = token;
		this.cardCVV = cvv;
		this.cardnumber = null;
	}



	public CardType getCardType() {
		return CardType.typeOf(cardnumber);
	}

	public String getCardNumber() {
		return this.cardnumber;
	}

	public String getCardHolderName() {
		return this.nameOnCard;
	}

	public String getCrdr() {
		return this.crdr;
	}

	public String getExpiryYear() {
		return ("20" + String.valueOf(expYear));
	}

	public String getExpiryMonth() {

		if (Integer.valueOf(expMonth) < 10) {
			return "0" + String.valueOf(Integer.valueOf(expMonth));
		}

		return String.valueOf(expMonth);
	}

	public String getCvvNumber() {
		return this.cardCVV;
	}

	public String getcardToken() {
		return this.token;
	}

	private String normalizeCardNumber(String number) {
		if (number == null) {
			return null;
		}
		return number.trim().replaceAll("\\s+|-", "");
	}

	public boolean validateCard() {
		if (cardCVV == null) {
			return validateNumber() && validateExpiryDate();
		} else {
			return validateNumber() && validateExpiryDate() && validateCVC();
		}
	}

	public boolean validateNumber() {
		
		CardType type = getCardType();
		
		if (type == null)
			return false;
			
		cardType = type.toString();
		
		if (TextUtils.isBlank(cardnumber)) {
			return false;
		}

		String rawNumber = cardnumber.trim().replaceAll("\\s+|-", "");
		if (TextUtils.isBlank(rawNumber) || !TextUtils.isWholePositiveNumber(rawNumber)
				|| !isValidLuhnNumber(rawNumber)) {
			return false;
		}

		if (!"AMEX".equals(cardType) && rawNumber.length() != 16) {
			return false;
		}

		if ("AMEX".equals(cardType) && rawNumber.length() != 15) {
			return false;
		}

		return true;
	}

	public boolean validateExpiryDate() {
		if (!validateExpMonth()) {
			return false;
		}
		if (!validateExpYear()) {
			return false;
		}
		return !DateUtils.hasMonthPassed(Integer.valueOf(expYear), Integer.valueOf(expMonth));
	}

	public boolean validateExpMonth() {
		if (expMonth == null) {
			return false;
		}
		return (Integer.valueOf(expMonth) >= 1 && Integer.valueOf(expMonth) <= 12);
	}

	public boolean validateExpYear() {
		if (expYear == null) {
			return false;
		}
		return !DateUtils.hasYearPassed(Integer.valueOf(expYear));
	}

	public boolean validateCVC() {
		if (TextUtils.isBlank(cardCVV)) {
			return false;
		}
		String cvcValue = cardCVV.trim();
		cardType = getCardType().toString();
		boolean validLength = ((cardType == null && cvcValue.length() >= 3 && cvcValue.length() <= 4)
				|| ("AMEX".equals(cardType) && cvcValue.length() == 4) || (!"AMEX".equals(cardType) && cvcValue
				.length() == 3));

		if (!TextUtils.isWholePositiveNumber(cvcValue) || !validLength) {
			return false;
		}
		return true;
	}

	private boolean isValidLuhnNumber(String number) {
		boolean isOdd = true;
		int sum = 0;

		for (int index = number.length() - 1; index >= 0; index--) {
			char c = number.charAt(index);
			if (!Character.isDigit(c)) {
				return false;
			}
			int digitInteger = Integer.parseInt("" + c);
			isOdd = !isOdd;

			if (isOdd) {
				digitInteger *= 2;
			}

			if (digitInteger > 9) {
				digitInteger -= 9;
			}

			sum += digitInteger;
		}

		return sum % 10 == 0;
	}
}
