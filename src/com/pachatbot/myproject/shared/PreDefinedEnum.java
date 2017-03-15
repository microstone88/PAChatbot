package com.pachatbot.myproject.shared;

import java.io.Serializable;

public abstract class PreDefinedEnum {

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
	
	
}
