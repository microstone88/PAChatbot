package com.pachatbot.myproject.shared;

/**
 * 
 */
public class FieldVerifier {

	public FieldVerifier(){}
	
	public static boolean isValidUserForSignIn(String name) {
		return Username.isValid(name) || Email.isValid(name) || Cellphone.isValid(name);
	}
	
	
	public static boolean isEmpty(String str) {
		return str.trim().length() < 1;
	}
	
	
	public static class Username extends FieldVerifier {
		public Username(){}
		public static boolean isValid(String username) {
//			return username.matches("^[a-zA-Z0-9_-]{5,20}$");
//			return username.matches("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+){5,20}$");
			return username.matches("^(?=.*[a-zA-Z])[a-zA-Z0-9_-]{5,20}$");
		}
	}
	
	public static class Password extends FieldVerifier {
		public Password(){}
		public static boolean isValid(String password) {
			return password.matches("^[a-zA-Z0-9_-]{8,30}$");
		}
	}
	
	public static class Cellphone extends FieldVerifier {
		public Cellphone(){}
		public static boolean isValid(String cellphone) {
			return cellphone.matches("^[0-9]{4,12}$");
		}
	}

	public static class Email extends FieldVerifier {
		public Email(){}
		public static boolean isValid(String email) {
	        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
		}
	}
	
	public static class Firstname extends FieldVerifier {
		public Firstname(){}
		public static boolean isValid(String firstname) {
			/**
			 * \\p{L} is a Unicode Character Property that 
			 * matches any kind of letter from any language.
			 */
			return firstname.matches("^[a-zA-Z\u00C0-\u017F ’.'-]{1,30}$"); 
		}
	}
	
	public static class Lastname extends FieldVerifier {
		public Lastname(){}
		public static boolean isValid(String lastname) {
			/**
			 * \\p{L} is a Unicode Character Property that 
			 * matches any kind of letter from any language.
			 */
			return lastname.matches("^[a-zA-Z\u00C0-\u017F ’.'-]{1,60}$"); 
		}
	}
	
	public static class PayPalAcc extends FieldVerifier {
		public PayPalAcc(){}
		public static boolean isValid(String payPalAcc) {
			return payPalAcc.matches("^[a-zA-Z0-9._@-]{3,}$");
		}
	}

	public static class AlipayAcc extends FieldVerifier {
		public AlipayAcc(){}
		public static boolean isValid(String payPalAcc) {
			return payPalAcc.matches("^[a-zA-Z0-9._@-]{3,}$");
		}
	}
	
	public static class WeChatAcc extends FieldVerifier {
		public WeChatAcc(){}
		public static boolean isValid(String payPalAcc) {
			return payPalAcc.matches("^[a-zA-Z0-9._@-]{3,}$");
		}
	}

	
	
}
