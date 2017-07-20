package com.utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.resource.PropertiesCache;
/**
 * 
 * @author rajendra.beelagi
 *
 */
public class GenericMethods extends DriverMethods {
	static Logger log = Logger.getLogger(GenericMethods.class.getName());
	String currentTestCaseName = "";
	//Map<String, String> dataMap;
	private static  WebDriver driver = null;
	private static WebDriverWait driverWait;
	private static Map<String, Map<String, String>> locatorMap = new HashMap<String, Map<String, String>>();
	private static Map<String, Map<String, String>> locatorValuesMap = new HashMap<String, Map<String, String>>();

	public static HashMap<String, String> locators = null;
	public static HashMap<String, String> locatorsValue = null;
	
	public static Map<String, Map<String, String>> testCaseMap = new HashMap<String, Map<String, String>>();
	
	/**
	 * constructor accepting webdriver
	 * @param webdriver
	 */
	public GenericMethods(WebDriver webdriver ,Map<String, String> dataMap) {
		super(webdriver,dataMap);
		driver = webdriver;
		super.dataMap=dataMap;
		
	}

	/**
	 * Default Constructor
	 */
	public GenericMethods() {

	}
	/**
	 * Description : Sets the value to Textbox fields
	 * @param elementName
	 * @param pageName
	 * @param recordId
	 */
	
	public void setValueInTextBox(String elementName, String pageName,	String recordId) {
		WebElement element = getElement(elementName, pageName);
		String setText= dataMap.get(pageName+recordId+elementName);
		element.sendKeys(setText);
		//takeScreenShot(elementName,pageName);
		log.info("Value (" + setText + ") "+"Entered in Page (" +pageName +") "+ "Element ("+elementName+")");
	}

	/**
	 * Description : Performs click oparation on webelemnt
	 * @param elementName
	 * @param pageName
	 */
	public void clickElement(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		element.click();
		log.info("Clicked Page (" +pageName +") "+ "Element ("+elementName+")");
	}

	/**
	 * Forms WebElemnet based on the locator  
	 * @param elementName
	 * @param pageName
	 * @return
	 */

	public WebElement getElement(String elementName, String pageName) {
		String locator = locatorMap.get(pageName).get(elementName);
		String locatorValue = locatorValuesMap.get(pageName).get(elementName);

		if(waitForPageLoad()){

			if ("ID".equalsIgnoreCase(locator)) {
				return driver.findElement(ById.id(locatorValue));
			} else if ("XPATH".equalsIgnoreCase(locator)) {
				return driver.findElement(ById.xpath(locatorValue));
			} else if ("tagName".equalsIgnoreCase(locator)) {
				return driver.findElement(ById.tagName(locatorValue));
			} else if ("className".equalsIgnoreCase(locator)) {
				return driver.findElement(ByClassName.className(locatorValue));
			} else if ("LinkText".equalsIgnoreCase(locator)) {
				return driver.findElement(ByLinkText.linkText(locatorValue));
			} else if ("cssSelector".equalsIgnoreCase(locator)) {
				return driver.findElement(ByCssSelector.cssSelector(locatorValue));

			}
		}

		return null;
	}

	public WebElement getElementForScreenShot(String elementName, String pageName) {
		String locator = locatorMap.get(pageName).get(elementName);
		String locatorValue = locatorValuesMap.get(pageName).get(elementName);


		if ("ID".equalsIgnoreCase(locator)) {
			return driver.findElement(ById.id(locatorValue));
		} else if ("XPATH".equalsIgnoreCase(locator)) {
			return driver.findElement(ById.xpath(locatorValue));
		} else if ("tagName".equalsIgnoreCase(locator)) {
			return driver.findElement(ById.tagName(locatorValue));
		} else if ("className".equalsIgnoreCase(locator)) {
			return driver.findElement(ByClassName.className(locatorValue));
		} else if ("LinkText".equalsIgnoreCase(locator)) {
			return driver.findElement(ByLinkText.linkText(locatorValue));
		} else if ("cssSelector".equalsIgnoreCase(locator)) {
			return driver.findElement(ByCssSelector.cssSelector(locatorValue));

		}



		return null;
	}

	/**
	 * @Description : Wait for page load.
	 * @return boolean
	 */
	public boolean waitForPageLoad() {

		driverWait = new WebDriverWait(driver,10);

		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long)((JavascriptExecutor)driver).executeScript("return jQuery.active") == 0);
				}
				catch (Exception e) {
					// no jQuery present
					return true;
				}
			}
		};

		// wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor)driver).executeScript("return document.readyState")
						.toString().equals("complete");
			}
		};
		log.info("Loading Complete");
		return driverWait.until(jQueryLoad) && driverWait.until(jsLoad);
	}

	/**
	 * Reads all the Elements from page data and stores in Hashmap
	 * @param currentTestCaseName
	 * @param path
	 */
	public static Map<String, String> loadPageData(String path) {
		Map<String, String> tesDataValuesMap = new HashMap<String, String>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream inputStream = Files.newInputStream(Paths.get(path).toAbsolutePath()); 
			Document document = db.parse(inputStream);
			NodeList nodeList1 = document.getElementsByTagName("page");
			
			for (int i = 0; i < nodeList1.getLength(); i++) {
				Element question = (Element) nodeList1.item(i);
				NodeList optionList = question.getElementsByTagName("record");
				for (int j = 0; j < optionList.getLength(); ++j) {

					for (int k = 0; k <= optionList.item(j).getAttributes().getLength()-1; k++) {
						StringBuffer mapkey = new StringBuffer();
						mapkey = mapkey.append(nodeList1.item(i).getAttributes().getNamedItem("name").getNodeValue());
						mapkey.append(optionList.item(j).getAttributes().getNamedItem("id").getNodeValue());
						mapkey.append(optionList.item(j).getAttributes().item(k).getNodeName());
						tesDataValuesMap.put(mapkey.toString(),optionList.item(j).getAttributes().item(k).getNodeValue());
					}

				}
			}

		}
		catch (Exception e) {
			System.out.println(e);
		}
		return tesDataValuesMap;
	}
	

	/**
	 * Reads Object repository and stores elements  of page as key value pair in map
	 */
	public static void loadObjectRepository() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(Files.newInputStream(Paths.get("src/com/resource/ObjectRepository.xml").toAbsolutePath()));
					
			locators = new HashMap<String, String>();
			locatorsValue = new HashMap<String, String>();

			NodeList nodeList1 = document.getElementsByTagName("page");
			for (int i = 0; i < nodeList1.getLength(); i++) {
				Element question = (Element) nodeList1.item(i);

				NodeList optionList = question.getElementsByTagName("element");
				for (int j = 0; j < optionList.getLength(); ++j) {
					locators.put(optionList.item(j).getAttributes()
							.getNamedItem("name").getNodeValue(), optionList
							.item(j).getAttributes().getNamedItem("Descriptor")
							.getNodeValue());
					locatorsValue.put(optionList.item(j).getAttributes()
							.getNamedItem("name").getNodeValue(), optionList
							.item(j).getAttributes().getNamedItem("value")
							.getNodeValue());

				}
				locatorMap.put(nodeList1.item(i).getAttributes().getNamedItem("name").getNodeValue(), locators);
				locatorValuesMap.put(nodeList1.item(i).getAttributes().getNamedItem("name").getNodeValue(), locatorsValue);
			}

		} catch (Exception e) {
		}
	}

	/**
	 *  Creates instance of webdriver based on the passed browser parameter 
	 * @return WebDriver
	 */
	public static WebDriver getDriver(String browserType) {
		if (browserType.equals("chrome")) {
			driver = initChromeDriver();
		} else if (browserType.equals("ie")) {
			driver = initIEDriver();
		} else {
			driver = initFirefoxDriver();
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;

	}
	private static WebDriver initChromeDriver() {
		System.setProperty("webdriver.chrome.driver", PropertiesCache.getInstance().getProperty("service"));
		WebDriver driver = new ChromeDriver();
		log.info("Chrome Launched");
		return driver;
	}

	private static WebDriver initIEDriver() {
		System.setProperty("webdriver.ie.driver", PropertiesCache.getInstance().getProperty("service"));

		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true); 
		WebDriver driver = new InternetExplorerDriver(capabilities);
		log.info("IE Launched");
		return driver;
	}
	private static WebDriver initFirefoxDriver() {
		System.setProperty("webdriver.gecko.driver", PropertiesCache.getInstance().getProperty("service"));
		WebDriver driver = new FirefoxDriver();
		log.info("FireFox Launched");
		return driver;
	}
	
	/**
	 *  @Description : Focus on newly opened window.
	 *  @return : boolean
	 */
	public boolean focuson_openwindow()
	{
		try
		{
			//String handle = driver.getWindowHandle();
			for (String winHandle : driver.getWindowHandles()) 
			{
				driver.switchTo().window(winHandle); 
			}
			log.info("Focus on Window");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}  
	}

	/**
	 * @Description : Checks if is alert present.
	 * @return boolean
	 */
	public boolean isAlertPresent() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.alertIsPresent());
			log.info("Alert Present");
			return true;
		} // try
		catch (Exception Ex) {
			return false;
		} 
	} 

	/**
	 * @Description : Dismiss alert.
	 */
	public void dismissAlert() {
		try {
			isAlertPresent();
			Alert currentAlert = driver.switchTo().alert();
			currentAlert.dismiss();
		} // try
		catch (Exception e) {

		}
	}
	
	/**
	 *  @Description : Accept alert.
	 */
	public void acceptAlert() {
		try {

			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.accept();

		} 
		catch (Exception e) {

		}

	}

	/**
	 *  @Description : Handling alerts.
	 *  @return : boolean
	 */
	public boolean Alert_handle(int popuptype, String message)
	{ 
		//popuptype = 0 --> cancel
		//popuptype = 1 --> accept	
		//popuptype = 2 --> send_text
		//popuptype = 3 --> get_text
		try
		{
			if(driver.switchTo().alert() != null)
			{
				switch(popuptype)
				{
				case 0:
					driver.switchTo().alert().dismiss(); 
				case 1:
					driver.switchTo().alert().accept();
				case 2:
					driver.switchTo().alert().sendKeys(message);
				case 3:
					driver.switchTo().alert().getText();
				}	
			}
		} 
		catch(Exception e)
		{
			return false; 
		}
		return true;
	}
	
	/**
	 *  @Description : Get system current date.
	 *  @return : String
	 */
     public String getSystemDate(){
		
		DateFormat df = new SimpleDateFormat("M/d/yyyy");
		// Instantiate a Date object
	      Date date = new Date();

	     return df.format(date);   
	}
     

	/**
	 *  @Description : Switch to alert.
	 */
	public void switchToAlert() {
		try {
			driver.switchTo().alert();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Description : Open URL.
	 * @param String: url
	 * @return boolean
	 */
	public boolean openURL(String url) {
		try {

			driver.get(url);
			maxiWindow();

		} catch (Exception ex) {
			return false;
		}

		return true;
	}

	/**
	 * @Description : closeBrowser : close Browser
	 * @return void
	 */
	public void closeBrowser() {
		try {
			driver.close();

		} catch (Exception ex) {

		}

	}


	/**
	 * @Description : Checks if is element enabled.
	 * @param Element : elem
	 * @return boolean
	 */
	public boolean isElementEnabled(String elementName, String pageName) {
		try {
			WebElement element = getElement(elementName, pageName);
			//takeScreenShot(elementName,pageName);
			element.isEnabled();

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * @Description : Checks if is element displayed.
	 * @param Element : elem
	 * @return boolean
	 */
	public boolean isElementDisplayed(String elementName, String pageName) {
		try {
			WebElement element = getElement(elementName, pageName);
			//takeScreenShot(elementName,pageName);
			element.isDisplayed();

		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * @Description : Gets the all from dropdown.
	 * @param Element : elem
	 * @return the all from dropdown
	 */
	public List<WebElement> getAllFromDropdown(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		Select dropdown = new Select(element);
		//takeScreenShot(elementName,pageName);
		log.info("Drop Down values returned");
		return dropdown.getOptions();
	}

	/**
	 * @Description : Action press key.
	 * @param key : key
	 * @return boolean
	 */
	public boolean actionPressKey(Keys key) {
		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(key).perform();
			log.info("Enter Key Pressed");
			// actions.keyDown(key).keyDown(key).build().perform();
			// actions.click(webElement).build().perform();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * @Description :Gets the selected option.
	 * @param Element : elem
	 * @return String
	 */
	public String getSelectedOption(String elementName, String pageName) {
		try {
			WebElement element = getElement(elementName, pageName);
			Select sel = new Select(element);
			String text = sel.getFirstSelectedOption().getText();
		//	takeScreenShot(elementName,pageName);
			log.info("Drop Down Value (" + text + ") "+"Selected in Page (" +pageName +") "+ "Element ("+elementName+")");
			return text;

		} catch (Exception ex) {
			return null;
		}

	}


	/**
	 * @Description :Action mouse over.
	 * @param Element :element
	 * @return boolean
	 */
	public boolean actionMouseOver(String elementName, String pageName) {
		try {
			WebElement element = getElement(elementName, pageName);
			Actions actions = new Actions(driver);
			actions.moveToElement(element).perform();
			//takeScreenShot(elementName,pageName);
		} catch (Exception ex) {
			return false;
		}
		return true;

	}

	/**
	 * @Description :Toggle check box.
	 * @param Element : element
	 * @param String : action
	 * @return boolean
	 */
	public boolean toggleCheckBox(String elementName, String pageName,String recordId) {
		try {
			WebElement element = getElement(elementName, pageName);
		//	takeScreenShot(elementName,pageName);
			//String action = getPageDetailsData(pageName, elementName, recordId);
			//if ((action.equalsIgnoreCase("Check") && !isSelected(elementName, pageName)) || (action.equalsIgnoreCase("Uncheck") && isSelected(elementName, pageName))) {
			//	element.click();
			//}

			element.click();

		} 
		catch (Exception ex) {

			return false;
		}

		return true;
	}

	/**
	 * @Description :Select values from dropdown.
	 * @param Element : element
	 * @param String elementName, String pageName,String recordId
	 * @return boolean
	 */
	public boolean selectValueFromDropdown(String elementName, String pageName,String recordId) {
		try {
			WebElement element = getElement(elementName, pageName);
			//String subStrings = getPageDetailsData(pageName, elementName, recordId);
			String text;
			Select sel = new Select(element);
			List<WebElement> options = sel.getOptions();
			Actions act = new Actions(driver);
			//sel.deselectAll();
			act.keyDown(Keys.CONTROL).build().perform();

			for (WebElement opt : options) {
				text = element.getText().trim();
				//if (text.equalsIgnoreCase(subStrings)) {
					//takeScreenShot(elementName,pageName);
					//opt.click();                       
					//log.info("Drop Down Value (" + text + ") "+"Selected in Page (" +pageName +") "+ "Element ("+elementName+")");
					//break;
				//}
			}

			act.keyUp(Keys.CONTROL).build().perform();


		} catch (Exception ex) {
			return false;
		}
		return true;

	}

	/**
	 * @Description : Gets the text.
	 * @param Element: element
	 * @return String
	 */
	public String getText(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		String text = element.getText().trim();      
		log.info("Value (" + text + ") "+"fetched in Page (" +pageName +") "+ "Element ("+elementName+")");
		return text;
	}
	/**
	 * @Description : Checks if is text displayed.
	 * @param Element: elem
	 * @return boolean
	 */
	public boolean isTextDisplayed(String elementName, String pageName) {
		try {
			String text = getText(elementName, pageName);
			if (text.isEmpty() || text.length() == 0) {
				return false;
			} else {
				return true;
			}

		} catch (Exception ex) {

			return false;
		}

	}

	/**
	 *  @Description :Close all windows.
	 */
	public static void closeAllWindows() {
		try {

			driver.quit();

		} catch (Exception ex) {

		}

	}

	/**
	 * @Description :Gets the title.
	 * @return String
	 */
	public String getTitle() {
		try {
			log.info("Page Title (" + driver.getTitle()+")");
			return (driver.getTitle());

		} catch (Exception ex) {
			return null;
		}

	}

	/**
	 * @Description : Checks if is selected.
	 * @param Element: elem
	 * @return boolean
	 */
	public boolean isSelected(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		if (element.isSelected()){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @Description : Checks if is enabled.
	 * @param Element: elem
	 * @return boolean
	 */
	public boolean isEnabled(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		if (element.isEnabled()){
			return true;
		}
		else {
			return false;
		}
	}



	/**
	 * @Description : Gets the innerHTML of a webelement.
	 * @param Element: element
	 * @return String
	 */
	public String getinnerHTML(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		String text = element.getAttribute("innerHTML").trim();   
		return text;
	}

	/**
	 * @Description : Gets the innerText of a webelement.
	 * @param Element: element
	 * @return String
	 */
	public String getinnerText(String elementName, String pageName) {
		WebElement element = getElement(elementName, pageName);
		//takeScreenShot(elementName,pageName);
		String text = element.getAttribute("innerText").trim();   
		return text;
	}

	/**
	 * @Description :Press enter.
	 * @return boolean
	 */
	public boolean pressEnter() {
		try {
			Actions act = new Actions(driver);
			act.keyDown(Keys.ENTER);
			act.keyUp(Keys.ENTER);
			log.info("Enter Pressed");
		} catch (Exception ex) {

			return false;
		}

		return true;
	}
	  /**
     * @Description :  Method Returns the date before N days from current date in Allstate specific format
     * @param int: n
     * @return the string
     */
    public String getPreviousDate(int n , String format) {
        
        DateFormat dateFormat = new SimpleDateFormat(format);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -n);
        java.util.Date todate1 = cal.getTime();
        String s = dateFormat.format(todate1);

        //String accDate = s.substring(8, 10) + "-" + s.substring(5, 7) + "-" + s.substring(0, 4);

        return (s);
    }

	/**
	 * @Description : Gets the current year.
	 * @return String
	 */
	public String getCurrentYear(){
		try {

		Calendar now = Calendar.getInstance();   
			int year = now.get(Calendar.YEAR);  
			return (Integer.toString(year));

	} catch (Exception ex) {
			return null;
		}

}

	/**
	 * @Description : Gets the future date.
 * @return String
	 */
	public String getFutureDate(int noOfDays) {
	try {

			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DATE, noOfDays);
			 Date date = now.getTime(); 
			return (dateFormat.format(date));

	} catch (Exception ex) {
			return null;
		}

	}
	 
    /**
     * @Description : getModifiedDate
     * @param: String: strDate
     * @param int : Day Count
     * @throws Exception 
     */
    public static String getModifiedDate(String strDate, int dayCount) throws Exception {

    	if(strDate!=null && strDate!="" && strDate.contains("/")) {
    		String date[] = strDate.split("/");
    		if(date[0].length()==1) {
    			date[0]="0"+date[0];
    		}
    		if(date[1].length()==1) {
    			date[1]="0"+date[1];
    		}

    		strDate =date[0] + "/" +date[1] +"/"+date[2] ;
    	}

    	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    	Date date = dateFormat.parse(strDate);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.DATE, dayCount); 
    	
    	Date modifiedDate = cal.getTime();

    	return dateFormat.format(modifiedDate);
    }
    
	/**
	 * @Description : Gets the current date.
	 * @return String
	 */
	public String getCurrentDate() {
		try {

			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			return (dateFormat.format(date));

		} catch (Exception ex) {
			return null;
		}

	}
 

	/**
	 * @Description : Gets the text.
	 * @param Element: element
	 * @return String
	 *//*
	public String getAttribute(String elementName, String pageName,String recordId) {
		WebElement element = getElement(elementName, pageName); // returns webelement
		String Attributename = getPageDetailsData(pageName, elementName, recordId); // return string mentioned in xml

		String retval = null;
		try {
			String Atttext = element.getAttribute(Attributename).trim();
			if(Atttext!=null)
				retval = Atttext;
		}    	
		catch(NullPointerException e ) {

		}
		return retval;
	}*/

	/*public void takeScreenShot(String elementName, String pageName ){
		WebElement element = getElementForScreenShot(elementName, pageName); // returns webelement
		// Get entire page screenshot
		File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = null;

		try {
			fullImg = ImageIO.read(screenshot);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Get the location of element on the page
		Point point = element.getLocation();

		// Get width and height of the element
		int eleWidth = element.getSize().getWidth();
		int eleHeight = element.getSize().getHeight();

		// Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot= fullImg.getSubimage(point.getX(), point.getY(),
				eleWidth, eleHeight);
		try {
			ImageIO.write(eleScreenshot, "png", screenshot);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Copy the element screenshot to disk
		File screenshotLocation = new File("C:/Eclipse/HybridFramework/SeleniumProject/screenshots/"+System.currentTimeMillis()+"_"+pageName+"_"+elementName+".png");
		try {
			FileUtils.copyFile(screenshot, screenshotLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
