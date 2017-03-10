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
	public enum LOCALE implements Serializable {
		fr_FR, zh_CN, en_US, en_GB;
	}
	
	public enum STATUS implements Serializable {
		active, offline;
	}
	
	public enum USERGROUP implements Serializable {
		admin, user, guest;
	}
	
	
}
