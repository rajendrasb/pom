package com.tests.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import com.reporting.Reporter;
import com.utils.GenericMethods;
import com.utils.PageReuisits;

public class BaseTestCase {
	protected String testCaseName="";

	protected WebDriver driver = null;
	protected PageReuisits pageReuisits = null;
	protected Reporter reporter = new Reporter();
	
	protected void  beforeTest() {
		Reporter.initialize(testCaseName);
	}

	@AfterMethod
	public void closeInstance() {
		GenericMethods.closeAllWindows();
	}
	



	@DataProvider(name="dataProvidor")
	public Object[][] getData(){
		Object[][] data = new Object[3][2];
		return data;
	}
}