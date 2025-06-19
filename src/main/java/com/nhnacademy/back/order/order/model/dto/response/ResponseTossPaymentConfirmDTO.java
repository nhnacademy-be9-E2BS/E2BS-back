package com.nhnacademy.back.order.order.model.dto.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 토스의 결제 승인 응답을 받기 위한 DTO
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 속성을 무시
public class ResponseTossPaymentConfirmDTO {
	private String mId;
	private String lastTransactionKey;
	private String paymentKey;
	private String orderId;
	private String orderName;
	private int taxExemptionAmount;
	private String status;
	private ZonedDateTime requestedAt;
	private ZonedDateTime approvedAt;
	private boolean useEscrow;
	private boolean cultureExpense;
	private Card card; // null in 간편결제
	private VirtualAccount virtualAccount;
	private Transfer transfer;
	private MobilePhone mobilePhone;
	private GiftCertificate giftCertificate;
	private CashReceipt cashReceipt;
	private Object cashReceipts; // null
	private Object discount; // null
	private Object cancels; // null
	private String secret;
	private String type;
	private EasyPay easyPay;
	private String country;
	private Object failure;
	private boolean isPartialCancelable;
	private Receipt receipt;
	private Checkout checkout;
	private String currency;
	private int totalAmount;
	private int balanceAmount;
	private int suppliedAmount;
	private int vat;
	private int taxFreeAmount;
	private String method;
	private String version;
	private Object metadata;

	@Getter
	@Setter
	public static class EasyPay {
		private String provider;
		private int amount;
		private int discountAmount;
	}

	@Getter
	@Setter
	public static class Receipt {
		private String url;
	}

	@Getter
	@Setter
	public static class Checkout {
		private String url;
	}

	// Optional nested DTOs in case 다른 결제수단 쓰게 될 경우 대비
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Card {
	}

	@Getter
	@Setter
	public static class VirtualAccount {
		private String accountType;
		private String accountNumber;
		private String bankCode;
		private String customerName;
		private String dueDate;
		private String refundStatus;
		private boolean expired;
		private String settlementStatus;
		private Object refundReceiveAccount;
	}

	@Getter
	@Setter
	public static class Transfer {
	}

	@Getter
	@Setter
	public static class MobilePhone {
		private String customerMobilePhone;
		private String settlementStatus;
		private String receiptUrl;
	}

	@Getter
	@Setter
	public static class GiftCertificate {
	}

	@Getter
	@Setter
	public static class CashReceipt {
	}
}
