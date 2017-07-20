package com.tests;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.modules.LoginTestModules;
import com.reporting.Reporter;
import com.resource.PropertiesCache;
import com.utils.GenericMethods;
import com.utils.PageReuisits;

public class TestLoginForApplicaion  extends LoginTestModules {
	
	/**
	 *@ Initialize Driver and loading Object Repository 
	 */
	@BeforeMethod
	public void initialise() {
		driver = GenericMethods.getDriver(PropertiesCache.getInstance().getProperty("browser"));
		GenericMethods.loadObjectRepository();
		testCaseName = "TestLoginForApplicaion";
		Map<String, String> tesDataValuesMap = GenericMethods.loadPageData("src/com/resource/loginandverifydata.xml");
		pageReuisits = new PageReuisits(driver, tesDataValuesMap , testCaseName);
		beforeTest();
	}

	/**
	 * 
	 */
	@Test
	public void loginAndVerifyTitle() throws Exception {
		Long startTime = System.currentTimeMillis();
		try {
			
			driver.get(PropertiesCache.getInstance().getProperty("baseUrl1"));
			driver.manage().window().maximize();
			loginAndVerifyTitle(pageReuisits);
			
			Reporter.report("Pass", String.valueOf(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			Reporter.report("Fail", String.valueOf(System.currentTimeMillis()-startTime));
			e.printStackTrace();
		}
		finally{
			Reporter.writeResults();
		}
	}
	
}
