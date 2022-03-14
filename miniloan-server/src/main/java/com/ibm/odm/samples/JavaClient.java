/*
* Copyright IBM Corp. 1987, 2020
* 
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
* 
**/
package com.ibm.odm.samples;

import java.text.MessageFormat;

import javax.json.Json;
import javax.json.JsonObject;

import miniloan.Borrower;
import miniloan.Loan;

public class JavaClient {
	// Java Validation Part
	public String validateWithJava(Loan loan, Borrower borrower) {
		checkMaximumAmount(loan, borrower);
		checkRepaymentAndScore(loan, borrower);
		checkMinimumIncome(loan, borrower);
		checkCreditScore(loan, borrower);
		JsonObject response;
		if (loan.isApproved()) {
			response = Json.createObjectBuilder()
					.add("decision",  "Your loan is approved with a yearly repayment of " + loan.getYearlyRepayment())
					.build();
		} else {
			response = Json.createObjectBuilder()
					.add("decision",  "Your loan is rejected")
					.add("messages",  loan.getMessages().toString())
					.build();
		}

		JsonObject value = Json.createObjectBuilder()
		.add("approved",loan.isApproved())
		.add("response", response)
		.build();

		return value.toString();

	}

	/**
	 * check Repayment And Score
	 */
	private void checkRepaymentAndScore(Loan loan, Borrower borrower) {
		if (borrower.getYearlyIncome() > 0) {
			int val = loan.getYearlyRepayment() * 100
					/ borrower.getYearlyIncome();
			if ((val >= 0) && (val < 30) && (borrower.getCreditScore() >= 0)
					&& (borrower.getCreditScore() < 200)) {
				loan.addToMessages(Messages
						.getString("debttoincometoohighcomparedtocreditscore"));
				loan.reject();
			}
			if ((val >= 30) && (val < 45) && (borrower.getCreditScore() >= 0)
					&& (borrower.getCreditScore() < 400)) {
				loan.addToMessages(Messages
						.getString("debttoincometoohighcomparedtocreditscore"));
				loan.reject();
			}
			if ((val >= 45) && (val < 50) && (borrower.getCreditScore() >= 0)
					&& (borrower.getCreditScore() < 600)) {
				loan.addToMessages(Messages
						.getString("debttoincometoohighcomparedtocreditscore"));
				loan.reject();
			}
			if ((val >= 50) && (borrower.getCreditScore() >= 0)
					&& (borrower.getCreditScore() < 800)) {
				loan.addToMessages(Messages
						.getString("debttoincometoohighcomparedtocreditscore"));
				loan.reject();
			}
		}
	}

	/**
	 * Check Minimum Income
	 */
	private void checkMinimumIncome(Loan loan, Borrower borrower) {
		if (loan.getYearlyRepayment() > (borrower.getYearlyIncome() * 0.3d)) {
			loan.addToMessages(Messages.getString("toobigdebttoincomeratio"));
			loan.reject();
		}
	}

	/**
	 * Check Credit Score
	 */
	private void checkCreditScore(Loan loan, Borrower borrower) {
		if (borrower.getCreditScore() < 200) {
			loan.addToMessages(Messages.getString("creditscorebelow200"));
			loan.reject();
		}
	}

	/**
	 * Check Maximum Amount
	 */
	private void checkMaximumAmount(Loan loan, Borrower borrower) {
		if (loan.getAmount() > 1000000) {
			loan.addToMessages(Messages.getString("theloancannotexceed1000000"));
			loan.reject();
		}
	}


	
	protected static String escapeString(String str) {
		return str.replaceAll("\"", "\\\\\"").replaceAll("\n", "");
	}
	protected static String formatTrace(String ruleName, String taskName) {
		String format = Messages.getString("messagefiredinruletask");
		Object[] arguments = { ruleName, taskName };
		return MessageFormat.format(format, arguments);
	}


}
