package com.modules;

import MakeMyTripPage.MakeMyTripPageImpl;

import com.page.MakeMyTripPage;
import com.tests.base.BaseTestCase;
import com.utils.PageReuisits;

public class LoginTestModules  extends BaseTestCase{
	
	
	public void loginAndVerifyTitle(PageReuisits pageReuisits) throws Exception {
		// declaration
		/*HomePage homePage = new HomePageImpl(pageReuisits);
		LoginPage loginPage = new LoginPageImpl(pageReuisits);

		homePage.clickSignIn();
		loginPage.loginToApplication("TC001");
		homePage.verifyTitle();
	}*/
		MakeMyTripPage  makeMyTripPage = new MakeMyTripPageImpl(pageReuisits);
		makeMyTripPage.searchTriptDetails("TC001");
	}
}
