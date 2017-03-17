package com.pachatbot.myproject.shared;

import java.io.Serializable;

public abstract class PreDefined {

	/**
	 * Due to some already known version issues, java.util.Locale object 
	 * cannot be successfully serialized. Therefore, we use instead this 
	 * customized serializable enum class.
	 * @author micro
	 *
	 */
	public enum ULocale implements Serializable {
		fr_FR, zh_CN, en_US, en_GB;
	}
	
	public enum UStatus implements Serializable {
		active, offline, expired;
		
	}
	
	public enum UGroup implements Serializable {
		admin, user, guest;
		
	}
	
	public enum UCivility implements Serializable {
		M, Mme, Mlle, Mr, Mrs, Ms, Dr; 
	}
	
	public abstract static class TInfo {
		public enum Column {
			
			UNDEFINED ("undefined"),
			
			UID ("uid"), LOCALE ("locale"), CIVILITY ("civility"), FIRSTNAME ("first_name"), LASTNAME ("last_name"),
			// Contact information
			CELLPHONE ("cell"), EMAIL ("email"),
			// Payment information
			WeChat ("wechat"), PayPal ("paypal"), Alipay ("alipay");
			
			private String colname = "";
			Column(String colname){this.colname = colname;}
			@Override
			public String toString(){return colname;}
		}
	}
	
	
}
