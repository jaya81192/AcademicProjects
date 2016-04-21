package com.divvyapp;
import junit.framework.TestCase;

import org.junit.Test;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

public class AdvanceDateTest extends TestCase {
//Checks if the page redirects to the correct page
	@Test
	public final void testAdvanceDate() throws Exception {
	  	HttpUnitOptions.setScriptingEnabled(false);
	   	WebConversation wc = new WebConversation();
	    WebResponse res = wc.getResponse( "http://localhost:8080/Divvy-App/HomePage.jsp" );
	    assertEquals( "DIVVY-APP", res.getTitle() );
	}
}
