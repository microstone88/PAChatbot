/**
 * 
 */
package com.pachatbot.myproject.server;

import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.TInfo.Column;
import com.pachatbot.myproject.shared.PreDefined.UCivility;
import com.pachatbot.myproject.shared.PreDefined.ULocale;
import com.pachatbot.myproject.shared.Bean.Account;

/**
 * @author micro
 *
 */
public abstract class AccUtils {
	
	private static int updateAccountFrom(QueryResult qresult, Account account, Column column) {
		
		if (qresult.getColumnNames().contains(column.toString())) {
			String newStr = qresult.getValue(1, column) != null ? 
					qresult.getValue(1, column).toString() : null;
					
//			System.out.println(account.get(column) + "  " + newStr);
			
			switch (column) {
			case PayPal:
			case Alipay:
			case WeChat:
				newStr = Account.cache(newStr);
				break;
			default:
				break;
			}

			if (account.get(column) == null) {
				account.set(column, newStr);
				if (newStr != null) return 1;
			}
			else {
				switch (column) {
				case LOCALE:
					if (!account.get(column).toString().equals(
							ULocale.valueOf(newStr).toString())) {
						account.set(column, newStr);
						return 1;
					}
					break;
					
				case CIVILITY:
					if (!account.get(column).toString().equals(
							UCivility.valueOf(newStr).toString())) {
						account.set(column, newStr);
						return 1;
					}
					break;

				default:
					if (!account.get(column).equals(newStr)) {
						account.set(column, newStr);
						return 1;
					}
					break;
				}
				
				
			}
		}
		return 0;
		
	}
	
	public static int updateAccountInfoFrom(QueryResult qrInfo, Account account) {
		
		int re = 0;
		if (qrInfo.isUniqueRow()) {
			for (Column col : TInfo.Column.values()) {
				re += updateAccountFrom(qrInfo, account, col);
			}
		}
		return re;
		
	}
	

}
