package com.divvyapp;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

public class CompleteTaskTest {
//Checks if the page redirects to the correct page
	@Test
	public void test() throws SAXException {
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
	     WebResponse res = null;
		try {
			res = wc.getResponse( "http://localhost:8080/Divvy-App/HomePage.jsp" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     assertEquals( "DIVVY-APP", res.getTitle() );
	}

}
