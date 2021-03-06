package com.pachatbot.myproject.shared;

import java.io.Serializable;

public abstract class PreDefined {

	public enum UStatus implements Serializable {
		active, offline, expired;
		
	}
	
	public enum UGroup implements Serializable {
		admin, user, guest;
		
	}
	
	
	/**
	 * Due to some already known version issues, java.util.Locale object 
	 * cannot be successfully serialized. Therefore, we use instead this 
	 * customized serializable enum class.
	 * @author micro
	 *
	 */
	public enum ULocale implements Serializable {

		en_US ("English(US)"), en_GB ("English(UK)"), fr_FR ("French"), zh_CN ("Chinese");
		
		private String locale = "";
		ULocale(String locale){this.locale = locale;}
		@Override
		public String toString(){return locale;}
	}
	
	public enum UCivility implements Serializable {
		
		UNKNOWN (""), DR ("Dr."),
		M ("M."), MME ("Mme."), MLLE ("Mlle."), 
		MR ("Mr."), MRS ("Mrs."), MS ("Ms."), MISS ("Miss"); 
		
		private String civility = "";
		UCivility(String civility){this.civility = civility;}
		@Override
		public String toString(){return civility;}
	}
	
	public abstract static class TInfo {
		
		public enum Column {
			UNDEFINED ("undefined"),
			UID ("uid"), LOCALE ("locale"), CIVILITY ("civility"), FIRSTNAME ("first_name"), LASTNAME ("last_name"),
			CODE ("phonecode"), CELLPHONE ("cell"), EMAIL ("email"),
			WeChat ("wechat"), PayPal ("paypal"), Alipay ("alipay");
			
			private String colname = "";
			Column(String colname){this.colname = colname;}
			@Override
			public String toString(){return colname;}
		}
		
	}
	
}
