package com.divvyapp;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

public class AssignTasksTest extends TestCase {
//Checks if the page redirects to the correct page
	@Test
	public final void testAssignTasks() throws Exception
	{
		WebConversation wc = new WebConversation();
		HttpUnitOptions.setScriptingEnabled(false);
	     WebResponse res = wc.getResponse( "http://localhost:8080/Divvy-App/HomePage.jsp" );
	     assertEquals( "DIVVY-APP", res.getTitle() );
	}

}
