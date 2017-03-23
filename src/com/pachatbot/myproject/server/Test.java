package com.pachatbot.myproject.server;

import java.io.IOException;

import edu.mit.jwi.item.POS;

public class Test {

	public static void main(String[] args) throws IOException {
		
		String gloss = WNUtils.getGlossof("hi", POS.NOUN);
		System.out.println(gloss);
		
		
		
		

	}

}
