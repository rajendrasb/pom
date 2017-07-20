package com.page;

import com.page.base.BasePage;

public interface HomePage extends BasePage {
	public String title = "title";
	public String signin = "signin";

	public void verifyTitle();

	public void clickSignIn();
}
