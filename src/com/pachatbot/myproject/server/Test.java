package com.pachatbot.myproject.server;

import com.pachatbot.myproject.shared.Bean.QueryResult;

public class Test {

	public static void main(String[] args) {
		
//        InetAddress netAddress = getInetAddress();  
//        System.out.println("host ip:" + getHostIp(netAddress));  
//        System.out.println("host name:" + getHostName(netAddress));  
//        Properties properties = System.getProperties();  
//        Set<String> set = properties.stringPropertyNames(); //获取java虚拟机和系统的信息。  
//        for(String name : set){  
//            System.out.println(name + ":" + properties.getProperty(name));  
//        } 
		
//		String result = MySqlUtils.connect(DB.root);
		
//		MySqlUtils.testQuery();
		
//		QueryResult qr = SqlQueryUtils.testQuery();
//		System.out.println(qr);
		
//		QueryResult qr = SqlQueryUtils.queryForStdAnswer(Locale.FRENCH, "something");
		QueryResult qr = SqlQueryUtils.queryForClientInfoByPrimaryID(10);
		
//		QueryResult qr = new QueryResult();
		
		System.out.println("isUniqueValue = " + qr.isUniqueValue());
		System.out.println("isUniqueRow = " + qr.isUniqueRow());
		System.out.println("isUniqueColumn = " + qr.isUniqueColumn());
		System.out.println("isEmpty = " + qr.isEmpty());
		System.out.println("isNull = " + qr.isNull());
		
		System.out.println(qr);
		
//		QueryResult qr2 = new QueryResult();
//		QueryResult qr3 = new QueryResult(qr2);
//		
//		System.out.println(qr2.isNull());
//		System.out.println(qr3.isNull());
		
//		QueryResult new_qr = new QueryResult(qr);
//		System.out.println(new_qr);

	}
	
//    public static InetAddress getInetAddress(){  
//    	  
//        try{  
//            return InetAddress.getLocalHost();  
//        }catch(UnknownHostException e){  
//            System.out.println("unknown host!");  
//        }  
//        return null;  
//  
//    }  
//  
//    public static String getHostIp(InetAddress netAddress){  
//        if(null == netAddress){  
//            return null;  
//        }  
//        String ip = netAddress.getHostAddress(); //get the ip address  
//        return ip;  
//    }  
//  
//    public static String getHostName(InetAddress netAddress){  
//        if(null == netAddress){  
//            return null;  
//        }  
//        String name = netAddress.getHostName(); //get the host address  
//        return name;  
//    } 

}
