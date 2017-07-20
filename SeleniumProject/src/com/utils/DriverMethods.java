package com.utils;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * @Class Name: DriverMethods
 * @Description: Class is useful for reusable driver methods
 */
public class DriverMethods  {
	static Logger log = Logger.getLogger(GenericMethods.class.getName());
	Map<String, String> dataMap;
	protected  WebDriver driver = null;
	public DriverMethods(WebDriver webdriver ,Map<String, String> dataMap) {
		driver = webdriver;
		this.dataMap = dataMap;
	}

	public DriverMethods()
	{

	}

	/**
	 * @Description: Delete the cookies
	 */
	public void deleteCookies() {

		if (driver instanceof RemoteWebDriver)
			((RemoteWebDriver) driver).manage().deleteAllCookies();	
	}

	/**
	 * @Description: Maximizes the window 
	 */
	public  void maxiWindow() {
		driver.manage().window().maximize();
		log.info("WebPage Maximized");
	}


	/**
	 * @Description :Sets the implicit time out.
	 * @param Stirng : seconds, web driver 
	 */
	public void setImplicitTimeOut() {
		driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
		log.info("Set implicit wait of 5000 ms");
	}

	/**
	 * @Description : Method for switching to the child window
	 */
	public void switchToChildWindow() {
		try {

			if(waitForPageLoad()){

				String parentWinID = driver.getWindowHandle();
				Set<String> allWinIDs = driver.getWindowHandles();

				for (String win : allWinIDs) {
					if (!win.equalsIgnoreCase(parentWinID)) {
						driver.switchTo().window(win);
						break;
					}
				}
				log.info("Switched to child window");

			}} catch (Exception e) {
				e.printStackTrace();
			}

	}

	/**
	 * @Description : Method for switching window
	 */
	public void switchWindow() {

		log.info("***** In method 'switchWindow()' ");
		Set<String> handles = driver.getWindowHandles();
		System.out.println("No. of windows is: " + handles.size());

		for (String winHan : handles) {
			System.out.println(winHan);
			System.out.println(driver.getTitle());
			driver.switchTo().window(winHan);
		}
	} 

	/*
	 *//**
	 * @Description :Sets the implicit time out.
	 * @param Stirng : seconds 
	 */
	public void setImplicitTimeOut(int seconds) {

		driver.manage().timeouts().implicitlyWait(seconds * 1000, TimeUnit.MILLISECONDS);
	}

	/*   *//**
	 * @Description :Sets the page load time out.
	 * @param String :seconds 
	 */
	public void setPageLoadTimeOut(int seconds) {

		driver.manage().timeouts().pageLoadTimeout(seconds * 1000, TimeUnit.MILLISECONDS);
	}

	/*    *//**
	 * @Description :Sets the script load time out.
	 * @param String:seconds
	 */
	public void setScriptLoadTimeOut(int seconds) {

		driver.manage().timeouts().setScriptTimeout(seconds * 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 * @Description : Switch to child window.
	 * @param String :parentWinID
	 */
	public void switchToChildWindow(String parentWinID) {
		try {
			waitForPageLoad();
			System.out.println("parent Id "+parentWinID);	
			Set<String> allWinIDs = driver.getWindowHandles();

			for (String win : allWinIDs) {
				if (!win.equalsIgnoreCase(parentWinID)) {
					driver.switchTo().window(win);
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * @Description : Gets the current window ID.
	 * @return the current window ID
	 */
	public String getCurrentWindowID() {
		try {

			String winID = driver.getWindowHandle();
			log.info("Window ID ("+winID+")");
			return winID;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @Description : getAllWindowIDs : Get All Window IDs
	 * @return : Set<String>
	 */
	public Set<String> getAllWindowIDs() {
		try {

			log.info("Returned All Window IDs");
			return driver.getWindowHandles();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @Description : Window Switch Type.
	 * @return enum
	 */
	private enum WindowSwitchType {

		/** The by wintitle. */
		BY_WINTITLE,
		/** The by winurl. */
		BY_WINURL,
		/** The by frame. */
		BY_FRAME,
		/** The by parentframe. */
		BY_PARENTFRAME,
		/** The by default. */
		BY_DEFAULT,
		/** The by winclose. */
		BY_WINCLOSE,
		/** The by alert. */
		BY_ALERT,
		/** The by windowdialog */
		BY_WINDOWDIALOG_TITLE,
		/** The by frame index */
		BY_FRAME_INDEX,
		/** The by winID. */
		BY_WINID;
	}

	/**
	 * @Description : Switch to.
	 * @param String:switchType , switchExpValue 
	 * @return  boolean
	 */
	public boolean switchTo(String switchType, String switchExpValue) {
		try {
			switch (WindowSwitchType.valueOf(switchType)) {
			case BY_WINTITLE:
				return switchWindow(switchType, switchExpValue);
			case BY_WINURL:
				return switchWindow(switchType, switchExpValue);
			case BY_WINDOWDIALOG_TITLE:
				return switchWindowDialog(switchType, switchExpValue);
			case BY_FRAME:
				driver.switchTo().defaultContent();
				driver.switchTo().frame(switchExpValue);
				break;
			case BY_FRAME_INDEX:
				driver.switchTo().defaultContent();
				driver.switchTo().frame(Integer.parseInt(switchExpValue));
				break;
			case BY_PARENTFRAME:
				driver.switchTo().parentFrame();
				break;
			case BY_DEFAULT:
				driver.switchTo().defaultContent();
				break;
			case BY_WINCLOSE:
				driver.close();
				break;
			case BY_ALERT:
				WebDriverWait alertWait = new WebDriverWait(driver, 5);
				alertWait.until(ExpectedConditions.alertIsPresent());
				Alert alert = driver.switchTo().alert();
				alert.accept();
				break;
			case BY_WINID:
				driver.switchTo().window(switchExpValue);
				break;
			default:
				throw new IllegalArgumentException("Parameter switchtype should be BY_WINTITLE| BY_WINURL|BY_FRAME|BY_PARENTFRAME|BY_DEFAULT|BY_ALERT");
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * @Description : Switch window.
	 * @param String : switchType ,winExpValue
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean switchWindow(String switchType, String winExpValue) throws Exception {

		log.info("***** In method 'switchWindow()' for: " + switchType + " Window Name:" + winExpValue + " *****");

		waitForPageLoad();
		TimeUnit.SECONDS.sleep(2);

		boolean bSwitchWindow = false;
		String winActValue = "";
		Set<String> availableWindows = driver.getWindowHandles();
		if (!availableWindows.isEmpty()) {
			for (String windowId : availableWindows) {
				if (switchType.equalsIgnoreCase("BY_WINTITLE")) {
					winActValue = driver.switchTo().window(windowId).getTitle().trim().toLowerCase();
				} else {
					winActValue = driver.switchTo().window(windowId).getCurrentUrl().trim().toLowerCase();
				}

				winExpValue = winExpValue.trim().toLowerCase();
				if (winActValue.contains(winExpValue)) {
					bSwitchWindow = true;
					log.info("Window '" + winExpValue + "' switched successfully!!");
					driver.manage().window().maximize();
					break;
				}
			}

			log.info("***** Exit method 'switchWindow()' for: " + switchType + " Window Name:" + winExpValue + " *****");
		}

		waitForPageLoad();

		return bSwitchWindow;
	}

	/**
	 * @Description : Switch window with no title.
	 * @return boolean
	 * @throws InterruptedException
	 */
	public boolean switchWindowWithNoTitle() throws InterruptedException {

		log.info("***** In method 'switchWindowWithNoTitle()' *****");

		waitForPageLoad();
		TimeUnit.SECONDS.sleep(2);

		Set<String> availableWindows = driver.getWindowHandles();
		availableWindows = driver.getWindowHandles();
		String winTitle;
		Boolean bSwitchWindow = false;

		for (String windowId : availableWindows) {
			winTitle = driver.switchTo().window(windowId).getTitle().trim();
			if (winTitle.length() == 0) {
				log.info("Window '" + winTitle + "' switched successfully!!");
				driver.switchTo().window(windowId);
				bSwitchWindow = true;
				break;
			}
		}	
		waitForPageLoad();

		log.info("***** Exit method 'switchWindowWithNoTitle()' *****");
		return bSwitchWindow;
	}

	/**
	 * @Description : Switch window dialog.
	 * @param String :switchType ,winExpValue
	 * @return boolean
	 */
	public boolean switchWindowDialog(String switchType, String winExpValue) {

		try {

			log.info("***** In method 'switchWindowDialog()' for: " + switchType + " Window Name:" + winExpValue + " *****");

			waitForPageLoad();
			TimeUnit.SECONDS.sleep(2);

			String currentWindowId = driver.getWindowHandle();

			for (String winHandle : driver.getWindowHandles()) {
				String actualWindowTitle = driver.switchTo().window(winHandle).getTitle();
				if (actualWindowTitle.contains(winExpValue) && !currentWindowId.equals(winHandle)) {
					driver.switchTo().window(winHandle);
					break;
				}
			}
			// Thread.sleep(1000);
			waitForPageLoad();
			log.info("***** EXIT method 'switchWindowDialog()' for: " + switchType + " Window Name:" + winExpValue + " *****");

		} catch (Exception ex) {

			return false;
		}
		return true;
	}

	/**
	 * @Description : Maximize window.
	 */
	public void maximizeWindow() {

		try {
			driver.manage().window().maximize();
		} catch (Exception e) {

		}

	}

	/**
	 * @Description : Gets the windows count.
	 * @return int
	 */
	public int getWindowsCount() {

		try {
			log.info("Return Window Count");
			return (driver.getWindowHandles().size());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * @Description :Checks if is data present.
	 * @param String: data
	 * @return boolean
	 */
	public boolean isDataPresent(String data) {
		try {
			if (data.isEmpty() || data.length() == 0 || data == null || data.equals("null")) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @Description : Gets the page source.
	 * @return String
	 */
	public String getPageSource() {
		try {

			return driver.getPageSource();

		} catch (Exception ex) {

			return null;
		}

	}

	/**
	 * @Description :Action click.
	 * @param Element: element
	 * @return boolean
	 */
	public boolean moveToElementAndClick(WebElement element) throws Exception {
		try {

			Actions actions = new Actions(driver);
			actions.moveToElement(element).click().build().perform();
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex.fillInStackTrace());

		}
		return true;
	} 

	/**
	 * @Description : No ofwindows.
	 * @return int
	 */
	public int noOfwindows() {

		Set<String> listValues = null;

		listValues = driver.getWindowHandles();
		return listValues.size();

	}

	/**
	 * @Description : Cancel alert.
	 */
	public void cancelAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
		} // try
		catch (Exception e) {

		}

	}

	/**
	 * @Description :Split values.
	 * @param String :strValues , delimiter
	 * @return the string[]
	 */
	public String[] splitValues(String strValues, String delimiter) {
		String values[] = strValues.split(delimiter);

		String formattedArray[] = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			formattedArray[i] = values[i].trim();
		}

		return formattedArray;

	}

	/**
	 * @Description :Click quick launch.
	 * @param int x , y
	 * @return boolean
	 */
	public boolean clickQuickLaunch(int x, int y) {

		try {
			log.info("Method 'clickQuickLaunch' starts here");
			// There is  a mouse event which take place in this method. Hence need of hard wait. WaitForPageLoad will not suffice the need as the page would be loaded.
			Thread.sleep(200);
			// while (System.currentTimeMillis() < System.currentTimeMillis() +
			// (2 * 1000)) {}

			Robot r = new Robot();
			r.mouseMove(x, y);
			// There is  a mouse event which take place in this method. Hence need of hard wait. WaitForPageLoad will not suffice the need as the page would be loaded.
			Thread.sleep(1000);
			// while (System.currentTimeMillis() < System.currentTimeMillis() +
			// (1 * 1000)) {}

			r.mousePress(InputEvent.BUTTON1_MASK);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			// There is  a mouse event which take place in this method. Hence need of hard wait. WaitForPageLoad will not suffice the need as the page would be loaded.
			Thread.sleep(2000);
			// while (System.currentTimeMillis() < System.currentTimeMillis() +
			// (2 * 1000)) {}

			log.info("Method 'clickQuickLaunch' ends here");

		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description : Close Word Document 
	 * @return boolean
	 */
	public boolean closeWordDocument() {

		try {
			Thread.sleep(15000);
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_ALT);
			r.keyPress(KeyEvent.VK_F4);
			r.keyRelease(KeyEvent.VK_ALT);
			r.keyRelease(KeyEvent.VK_F4);
			Thread.sleep(3000);

		} catch (Exception ex) {

			return false;
		}

		return true;
	}



	/**
	 * @Description :Gets the todays date.
	 * @return String
	 */
	public String getTodaysDate() {

		String todayAsString = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
		return todayAsString;
	}

	/**
	 * @Description :Wait for alert.
	 * @return boolean
	 */
	public boolean WaitForAlert() {

		try {
			log.info("Method 'clickQuickLaunch' starts here");
			WebDriverWait wait = new WebDriverWait(driver, 50);
			wait.until(ExpectedConditions.alertIsPresent());
		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description : Wait for frameto switch by index.
	 * @param int : Index
	 * @return boolean
	 */
	public boolean WaitForFrametoSwitchByIndex(int Index) {

		try {
			log.info("Method 'clickQuickLaunch' starts here");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(Index));
		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description : Wait for frameto switch by name.
	 * @param String : frameName
	 * @return boolean
	 */
	public boolean WaitForFrametoSwitchByName(String frameName) {

		try {
			log.info("Method 'clickQuickLaunch' starts here");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description :Press enter.
	 * @return boolean
	 */
	public boolean pressEnter() {
		try {
			log.info("Method 'clickQuickLaunch' starts here");
			Actions act = new Actions(driver);
			act.keyDown(Keys.ENTER);
			act.keyUp(Keys.ENTER);
		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description :Checks if is image present.
	 * @param Element : imgElem
	 * @return boolean
	 */
	public boolean isImagePresent(WebElement imgElem) {
		try {
			log.info("Method 'isImagePresent' starts here");
			String src = imgElem.getAttribute("src");
			if (!src.equalsIgnoreCase("null") && src.length() > 1) {
				return true;
			} else {
				return false;
			}

		} catch (Exception ex) {

			return false;
		}

	}

	/**
	 * @Description : Gets the connection.
	 * @return Connection
	 */
	public Connection getConnection() {

		Connection connection = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@ldap://oid:389/ngsot101,cn=OracleContext,dc=allstate,dc=com", "NGTESTI7USR", "NGTESTI7USR");

		} catch (ClassNotFoundException | SQLException e) {
			return null;

		}
		return connection;
	}

	/**
	 * @Description : Checks if is window closed after operation.
	 * @param int :expectedNoOfWindowsAfterCloser
	 * @throws InterruptedException
	 */
	public void IsWindowClosedAfterOperation(int expectedNoOfWindowsAfterCloser) throws InterruptedException {
		int b = 0;
		while (b <= 6) {
			int a = getWindowsCount();
			if (a == expectedNoOfWindowsAfterCloser) {
				break;
			}
			waitForPageLoad();

			b++;

		}
	}

	/**
	 * @Description : Checks if is element present.
	 * @param Element : elem
	 * @return boolean
	 */
	public boolean isElementPresent(WebElement elem) {
		try {

			elem.isDisplayed();

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * @Description : Close all browsers.
	 * @param : String id
	 */
	public void closeAllBrowsers(String id) {
		try {

			Set<String> windowHandles = driver.getWindowHandles();

			for (String s : windowHandles) {
				if (!id.equalsIgnoreCase(s)) {
					driver.switchTo().window(s);
					driver.close();
				}

			}

			driver.switchTo().window(id);

		} catch (Exception ex) {

		}
	}

	/**
	 * @Description : Wait for page load.
	 * @return boolean
	 */
	public boolean waitForPageLoad() {
		int waitTime = new Double(20).intValue();

		ExpectedCondition<Boolean> pageLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, waitTime);

		try {
			wait.until(pageLoad);
		} catch (Throwable pageLoadWaitError) {

			return false;

		}
		return true;
	}

	/**
	 * @Description : Close the Attachment Window 
	 */
	public void closeAttachmentWindow() {

		try {
			// thread needed to perform following action by robot
			Thread.sleep(15000);
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_ALT);
			r.keyPress(KeyEvent.VK_F4);
			r.keyRelease(KeyEvent.VK_ALT);
			r.keyRelease(KeyEvent.VK_F4);
			Thread.sleep(3000);

		} catch (Exception ex){ 
		} 
	}

	/**
	 * @Description : JS click.
	 * @param Element:element
	 * @return boolean
	 */
	public boolean JSScrollHorizontillay(WebElement ele) {
		try {

			JavascriptExecutor jsexecutor = (JavascriptExecutor) driver;
			//WebElement element = (WebElement) ele.getNative();
			//jsexecutor.executeScript("arguments[0].scrollIntoView(true);",element);
			jsexecutor.executeScript("scrollTo(3000,0);");


		} catch (Exception ex) {
			log.error(ex);
			return false;
		}
		return true;
	}


	public void closeCurrentBrowser(String id) {
		try {

			Set<String> windowHandles = driver.getWindowHandles();

			for (String s : windowHandles) {
				if (id.equalsIgnoreCase(s)) {
					driver.switchTo().window(s);
					driver.close();
				}

			}
			driver.switchTo().window(id);

		} catch (Exception ex) {

		}
	}

	/*
	 * @Description : Switch to window using specific window handle.
	 */
	public void switchToWindow(String parentWinID) {
		try {
			waitForPageLoad();
			TimeUnit.SECONDS.sleep(2);

			driver.switchTo().window(parentWinID);

			waitForPageLoad();
		} catch (Exception e) {

		}

	}

	/**
	 * @Description : get alert text.
	 */
	public String getAlertText() {
		String alertText = null;
		try {
			Alert currentAlert = driver.switchTo().alert();
			alertText= currentAlert.getText();

		} // try
		catch (Exception e) {

		}
		return alertText;

	}


	/**
	 * @Description : JS scroll into view.
	 * @param Element:element
	 * @return boolean
	 */

	public boolean scrollIntoViewClick(WebElement element) throws Exception {
		try {
			Actions actions = new Actions(driver);

			actions.moveToElement(element, 0, 0).click().build().perform();
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex.fillInStackTrace());

		}
		return true;
	} 

	/**
	 * @Description :Select from dropdown by index.
	 * @param Element : element
	 * @param int : index
	 * 
	 */
	public void selectByIndexFromDropdown(WebElement element, int index) {
		log.debug("Begin of  DriverMethod.actionMouseOver()");
		try {

			Select sel = new Select(element);
			sel.selectByIndex(index);


		} catch (Exception ex) {
			log.error(ex.getStackTrace());

		}

	}

	/**
	 * @Description : Gets the current US timeZone date.
	 * @return String
	 */
	public String getCurrentTimeZoneDate() {
		try {

			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("US/Central"));
			Date date = c.getTime();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			String strDate = df.format(date);
			return strDate;

		} catch (Exception ex) {
			return null;
		}

	}

	/**
	 * @Description : compare Dates.
	 * @return String
	 */
	public static boolean compareDates(String expectedDate, String actualDate) {

		boolean status = false;
		try {
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Date expected = df.parse(expectedDate);
			Date actual = df.parse(actualDate);

			Assert.assertEquals(expected,actual);
			status= true;

		} catch (Exception ex) {
			System.out.println(ex);
			return status;
		}

		return status;
	}

	/**
	 * @Description :send data.
	 * @param Element: element
	 * @return boolean
	 */
	public void setData(WebElement element,String data) throws Exception {
		try {

			element.sendKeys(data);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex.fillInStackTrace());

		}
	} 

	/**
	 * @Description :Press F12.
	 * @return boolean
	 */
	public boolean pressF12(WebElement element) {
		try {
			log.info("Method 'pressF12' starts here");
			Actions actions = new Actions(driver);

			actions.moveToElement(element).click().build().perform();
			actions.sendKeys(Keys.F12);
			actions.perform();			


		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description :Press Tab
	 * @return boolean
	 */
	public boolean pressTab() {
		try {
			log.info("Method 'pressTab' starts here");
			Actions actions = new Actions(driver);

			actions.sendKeys(Keys.TAB);
			actions.build().perform();			


		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description :Press Down Key
	 * @return boolean
	 */
	public boolean pressDownKey() {
		try {
			log.info("Method 'presDown' starts here");
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.DOWN);
			actions.build().perform();			
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	/**
	 * @Description : Scroll  the page down
	 * @return boolean
	 */
	public boolean scrollPageDown(int height) {
		try {
			log.info("Method 'scrollPageDown' starts here");
			JavascriptExecutor jse = (JavascriptExecutor)driver;
			String arg = "window.scrollBy(0,"+height+")";

			jse.executeScript(arg, "");

		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description : getCurrentURL : get current URL
	 * @return : void
	 * @param : String :userId
	 */
	public String getCurrentURL() {
		String url ="";
		try {
			url = driver.getCurrentUrl();


			log.info("Current URL: " + url);
		} catch (Exception ex) {
			log.error(ex.getStackTrace());
		}
		return url;

	}


	/**
	 * @Description : navigateToURL : navigate to given URL
	 * @return : void
	 * @param : String :userId
	 */
	public void navigateToURL(String url) {
		try {

			driver.get(url);
			log.info("Navigated to URL: " + url);
		} catch (Exception ex) {
			log.error(ex.getStackTrace());
		}

	}

	/**
	 * @Description : Gets the random number of 7 digit.
	 * @param Element: element
	 * @return String
	 */
	public String getRandomNumber() {

		Random rnd = new Random(); 
		int n = 1000000 + rnd.nextInt(9000000);
		String newNumber = Integer.toString(n);
		return newNumber;

	}

}

