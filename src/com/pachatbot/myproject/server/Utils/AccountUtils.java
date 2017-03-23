/**
 * 
 */
package com.pachatbot.myproject.server.Utils;

import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.TInfo.Column;
import com.pachatbot.myproject.shared.PreDefined.UCivility;
import com.pachatbot.myproject.shared.Bean.Account;
import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public abstract class AccountUtils {
	
	private static int updateAccountFrom(QueryResult qresult, Account account, TInfo.Column column) {
		
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
			case CIVILITY:
				if (newStr == null) newStr = UCivility.UNKNOWN.name();
				break;
			default:
				break;
			}

			if (account.get(column) == null) {
				account.set(column, newStr);
				if (newStr != null) return 1;
			}
			else if (!account.get(column).equals(newStr)) {
					account.set(column, newStr);
					return 1;
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
