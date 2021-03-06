package functions;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Level;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.interactions.Actions;


/* Purpose		: Selenium related util class.
 * Developed By	: Brij
 * Modified By	:
 * Modified Date:
 * Reviewed By	:
 * Reviewed Date:
 */


public class Utility {

    public final static int IMPLICITWAIT = 20;
    public final static int EXPLICITWAIT = 30;
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Actions UserAction;
    private static String CurrentScreenshot = null;
    protected String mainWindowHandle;
    String browser = "chrome";
    //  String setDatapath = null;
    //  String setLanguage = null;
    String[] locatorInfo = new String[2];

    public static String GetScreenshot(String FileName) {
        FileOutputStream FOS = null;
        File ScreenshotFile = null;
        WebDriver screenshotdriver = driver;
        try {
            FileName = FileName.replaceAll("[^a-zA-Z0-9_]", "");//While providing XPATH needs to remove all special characters.
            FileName = Reporter.ScreenshotPath + "\\" + FileName + "-" + Reporter.GetTimeStamp("hhmmssSSS") + ".png";
            ScreenshotFile = new File(FileName);
            FileName = ScreenshotFile.getName();
            FOS = new FileOutputStream(ScreenshotFile);
            if (driver.getClass().toString().toLowerCase().endsWith("remotewebdriver")) {
                screenshotdriver = new Augmenter().augment(driver);
            }
            FOS.write(((TakesScreenshot) screenshotdriver).getScreenshotAs(OutputType.BYTES));
            FOS.flush();
            FOS.close();
        } catch (WebDriverException e) {
            Environment.loger.log(Level.ERROR, "WebDriverException While taking screenshot for - " + FileName, e);
        } catch (IOException e) {
            Environment.loger.log(Level.ERROR, "While taking screenshot for - " + FileName, e);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Other exception While taking screenshot for - " + FileName, e);
        } finally {
            try {
                FOS = null;
                ScreenshotFile = null;
                screenshotdriver = null;
            } catch (Exception e) {
                Environment.loger.log(Level.ERROR, "", e);
            }
        }
        return FileName;
    }

    private static void ApplicationExceptionHandler() {//TODO
    }

    public static void CloseBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
            }
            driver = null;
            Environment.BrowserObj = null;
        }
    }

    public WebDriver LaunchBrowser(String URL) {
        try {
            if (Environment.BrowserObj == null) {
                Environment.BrowserObj = GetBrowser(Environment.defaultBrowser);
                driver = Environment.BrowserObj;
                mainWindowHandle = driver.getWindowHandle();
                driver.manage().timeouts().implicitlyWait(IMPLICITWAIT, TimeUnit.SECONDS);
                wait = new WebDriverWait(driver, EXPLICITWAIT);
                UserAction = new Actions(driver);
            } else {
                driver.manage().deleteAllCookies();
            }
            driver.manage().window().maximize();
            driver.get(URL);
            Reporter.WriteLog(Level.INFO, "URL-" + URL);
        } catch (UnreachableBrowserException e) {
            Environment.loger.log(Level.ERROR, e);
            if (Environment.BrowserObj != null) {
                Environment.BrowserObj = null;
                Wait(30);
            }
        } catch (NullPointerException e) {
            FailCurrentTest("Intialize the appplication URL in the '" + Environment.getRunEnvironment() + "'");
            Environment.loger.log(Level.FATAL, "Intialize the appplication URL in the '" + Environment.getRunEnvironment() + "' block.");
            Utility.CloseBrowser();
        } catch (WebDriverException e) {
            Environment.loger.log(Level.ERROR, "WebDriverException occured", e);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
        return driver;
    }

    protected WebDriver GetBrowser(String browsername) {
        SupportedBrowser supportedBrowser = null;
        try {
            supportedBrowser = SupportedBrowser.valueOf(browsername.toUpperCase());
        } catch (IllegalArgumentException e) {
            Environment.loger.log(Level.ERROR, "Check for browser name".toUpperCase());
            System.exit(1);
        }
        switch (supportedBrowser) {
            case FF:
                //		File FirefoxProfilePath = new File(Environment.LibraryPath+"\\SeleniumFirefoxProfile");

                System.setProperty("webdriver.gec.driver", Environment.LibraryPath + "\\geckodriver.exe");
                driver = new FirefoxDriver();
                //		profile.setPreference("capability.policy.strict.Window.alert","noAccess");
                //		driver = new FirefoxDriver(profile);
                break;
            case GC:
//                System.setProperty("webdriver.chrome.driver", Environment.LibraryPath + "\\ChromeDriver.exe");
//                ChromeOptions options = new ChromeOptions();
//                options.addArguments("start-maximized"); // open Browser in maximized mode
//                options.addArguments("disable-infobars"); // disabling infobars
//                options.addArguments("--disable-extensions"); // disabling extensions
//                options.addArguments("--disable-gpu"); // applicable to windows os only
//                options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
//                options.addArguments("--no-sandbox"); // Bypass OS security model
//                options.setExperimentalOption("useAutomationExtension", false);
//                options.addArguments("--headless");
//
//                driver = new ChromeDriver(options);
//                break;

                System.setProperty("webdriver.chrome.driver", Environment.LibraryPath + "\\ChromeDriver.exe");
                driver = new ChromeDriver();
                break;
            case IE:
                System.setProperty("webdriver.ie.driver", Environment.LibraryPath + "\\IEDriverServer.exe");
                DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
                //			cap.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
                cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                //			cap.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
                //			cap.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
                driver = new InternetExplorerDriver(cap);
                break;
            //		case IE64:
            //			System.setProperty("webdriver.ie.driver",projPath+"/Data/Library/IEDriverServer64.exe");
            //			driver = new InternetExplorerDriver();
            //			break;
            //		default:
            //			LoggerIntialization.LOG.log(Level.ERROR, "Browser type unsupported");

        }
        return driver;
    }

    public WebDriver LaunchAppiumBrowser(String URL) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "18334D80E8"); // device product:CT60
        //   capabilities.setCapability("deviceName", "19060D84C1"); // device product:CN80
      //    capabilities.setCapability("deviceName", "18264523022268"); // device product:MC3300
      //  capabilities.setCapability("deviceName", "15350521400566");  // device product:TC8000
        capabilities.setCapability("platformVersion", "7.1.1");
        //   capabilities.setCapability("automationName","uiautomator2");
        capabilities.setCapability("appPackage", "com.manh.scale");
        capabilities.setCapability("appActivity", ".MainActivity");
        capabilities.setCapability("autoGrantPermissions", true);
        capabilities.setCapability("clearSystemFiles", true);

        try {

            driver = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return driver;

    }

    public boolean checkToastMessage(String expected) throws InterruptedException, URISyntaxException {
        String imgPath = captureScreenshot();

        return OcrApiUtil.checkText(imgPath, expected);
    }

    public String captureScreenshot() {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        //   String dir = System.getProperty("user.dir") + File.pathSeparator + "toastmessages";
        String dir = System.getProperty("user.dir") + "\\toastmessages\\";
        File file = new File(dir);
        file.delete();
        file.mkdir();

        //  String filePath = dir + File.pathSeparator + "toastMessage_" + (System.nanoTime() / 1000000000) + ".png";
        String filePath = dir + "toastMessage_" + (System.nanoTime() / 1000000000) + ".png";
        try {
            FileUtils.copyFile(scrFile, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public void NavigateTo(String URL) {
        driver.get(URL);
        WaitForPageload();
        //Override link for Internet Explorer
        if (GetBrowserInfo().startsWith("IE") && GetTitle().startsWith("Certificate Error")) {
            driver.get("javascript:document.getElementById('overridelink').click();");
        }
    }

    public List<WebElement> GetAllVisibleElements(String ObjectName) {
        List<WebElement> ElementCollection = null;
        List<WebElement> HiddenElements = new ArrayList<WebElement>();
        try {
            ElementCollection = GetAllElements(ObjectName);
            for (WebElement ElementItem : ElementCollection) {
                if (!ElementItem.isDisplayed()) {
                    HiddenElements.add(ElementItem);
                }
            }
            ElementCollection.removeAll(HiddenElements);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured:", e);
        }
        return ElementCollection;
    }

    public List<WebElement> GetAllElements(String ObjectName) {
        List<WebElement> ElementCollection = null;
        String[] locatorInfo = new String[2];
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            ElementCollection = driver.findElements(GetBy(locatorInfo[0], locatorInfo[1]));
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured:", e);
        }
        return ElementCollection;
    }

    public List<String> GetAllText(String ObjectName) {
        List<String> ReturnText = new ArrayList<String>();
        List<WebElement> ElementCollection = null;
        try {
            ElementCollection = GetAllVisibleElements(ObjectName);
            for (WebElement CurrentRow : ElementCollection) {
                ReturnText.add(GetText(CurrentRow));
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured:", e);
        }
        return ReturnText;
    }

    public String GetOnlyMyText(String ObjectName) {
        String ReturnValue = "";
        String ChildText = "";
        try {
            WebElement ParentElement = GetObject(ObjectName);
            ReturnValue = ParentElement.getText();
            List<WebElement> ChildColl = ParentElement.findElements(By.xpath("descendant::*"));
            for (WebElement ChildElement : ChildColl) {
                //				ChildText = ReplaceSplCharacters(ChildElement.getText());
                if (!GetText(ChildElement).isEmpty()) {
                    ReturnValue = ReturnValue.replaceFirst(ChildText, "");
                }
            }
        } catch (NullPointerException e) {
            ReturnValue = "OBJECT NOT FOUND";
        } catch (StaleElementReferenceException e) {
            ReturnValue = "STALE ELEMENT EXCEPTION";
        } catch (Exception e) {
            ReturnValue = "ERROR OCCURED";
        }

        return ReturnValue.trim().replaceAll("^", "");
    }

    public boolean ObjectExists(String ObjectName) {
        boolean returnValue = false;
        try {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);//Not to wait till Implicit wait.
            List<WebElement> AllElements = GetAllElements(ObjectName);
            driver.manage().timeouts().implicitlyWait(IMPLICITWAIT, TimeUnit.SECONDS);
            returnValue = AllElements.size() > 0;
            if (returnValue) {
                for (WebElement w : AllElements) {//TODO for multiple elements.
                    if (!w.isDisplayed()) {
                        returnValue = false;
                    }
                    break;//Just validating for single value,Because for multiple values it will check for another element(Child element) which is not displayed.
                    //Ex://table[@id='resSearchResultsTBL']//tbody/tr[1]/td[1]/* here we are locating all child elements, but child elements may be as hidden
                }
            } else {
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return returnValue;
        }
        return returnValue;
    }

    public boolean IsEnabled(String ObjectName, String EnabledORDisabled) {
        WebElement ActionObject = GetObject(ObjectName);
        try {
            if (!ActionObject.isDisplayed()) {
                Environment.loger.log(Level.ERROR, ObjectName + " should be displayed but it is not displayed");
                return false;
            }
            EnabledORDisabled = EnabledORDisabled.toUpperCase();
            if (EnabledORDisabled.toUpperCase().startsWith("ENABLED")) {
                // Verify whether the Object is enabled
                if (ActionObject.isEnabled()) {
                    Environment.loger.log(Level.INFO, ObjectName + " should be enabled '" + ObjectName + "' is enabled");
                    return true;
                } else {
                    Environment.loger.log(Level.ERROR, ObjectName + " should be enabled '" + ObjectName + "' is not enabled");
                    return false;
                }
            } else {
                // Verify whether the Object is disabled
                if (!ActionObject.isEnabled()) {
                    Environment.loger.log(Level.INFO, ObjectName + " should be disabled '" + ObjectName + "' is disabled");
                    return true;
                } else {
                    Environment.loger.log(Level.ERROR, ObjectName + " should be disabled '" + ObjectName + "' is not disabled");
                    return false;
                }
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return false;
        }
    }

    private String[] GetLocatorInfo(String ObjectName) {
        String[] locatorInfo = new String[2];
        if (ObjectName.startsWith("(") || ObjectName.startsWith("/")) {//If its an XPATH, Object Name should start with '/','('.
            locatorInfo[0] = "xpath";
            locatorInfo[1] = ObjectName;
        } else {
            locatorInfo = GetLocator(ObjectName).split("\\|");
        }
        return locatorInfo;
    }

    protected WebElement GetObject(String ObjectName) {
        WebElement ActionObject = null;
        boolean WritePageSource = false;
        WaitForObject(ObjectName);
        //    System.out.println(driver);
        try {
            //    System.out.println("action 350  " + driver.findElement(GetBy(locatorInfo[0], locatorInfo[1])));
            ActionObject = driver.findElement(GetBy(locatorInfo[0], locatorInfo[1]));

        } catch (InvalidSelectorException e) {
            Environment.loger.log(Level.ERROR, "Check for XPATH Syntax:'" + ObjectName + "'->" + GetXPath(ObjectName));
        } catch (ArrayIndexOutOfBoundsException e) {
            Environment.loger.log(Level.ERROR, "Locator Info doesnot have identifier value: " + ObjectName);
        } catch (NoSuchElementException e) {
            Environment.loger.log(Level.ERROR, "Object not found: '" + ObjectName + "'");
            //Write the current page source into a text file
            if (WritePageSource) {
                File ExpectionFile = new File(Reporter.ResultFolder + "\\Exception" + GetTimeStamp("hhmmssSSSa") + ".txt");
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ExpectionFile), Charset.forName("UTF-8")));
                    writer.write(driver.getPageSource());
                    writer.flush();
                    writer = null;
                } catch (Exception Ignore) {
                }
            }
            //			driver.navigate().back();
            //			if(driver.findElements(By.tagName("iframe")).size()>0 || driver.findElements(By.tagName("frame")).size()>0){
            //				Environment.loger.log(Level.INFO,"FRAME(s) ARE AVAILABLE!!!..Switch to corrrespoding frame and try it!!!");
            //			}
        } catch (Exception e) {
            throw e;
        } finally {
            locatorInfo = null;
            //TODO
        }
        return ActionObject;
    }

    private By GetBy(String locatorType, String locator) {
        GetBy GB = null;
        try {
            GB = GetBy.valueOf(locatorType.toUpperCase());
        } catch (IllegalArgumentException e) {
            Environment.loger.log(Level.ERROR, "Check for identifier Locator Type in ORLocator");
        }
        try {
            switch (GB) {
                case ID:
                    return By.id(locator);
                case NAME:
                    return By.name(locator);
                case XPATH:
                    return By.xpath(locator);
                case CLASSNAME:
                    return By.className(locator);
                case LINKTEXT:
                    return By.linkText(locator);
                case PARTIALLINKTEXT:
                    return By.partialLinkText(locator);
                case TAGNAME:
                    return By.tagName(locator);
                case CSS:
                    return By.cssSelector(locator);
                default:
                    Environment.loger.log(Level.ERROR, "INVLAID LOCATOR TYPE");
                    //TODO
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
        return null;
    }

    public void MoveToObject(String ObjectName) {
        WebElement ActionObject = GetObject(ObjectName);
        MoveToObject(ActionObject);
    }

    private void MoveToObject(WebElement element) {
        UserAction.moveToElement(element).build().perform();
    }

    private void ActionClick(WebElement element) {
        UserAction.click(element).build().perform();
    }

    private void ActionSendKeys(String Identifier, String Text) {
        UserAction.sendKeys(GetObject(Identifier), Text).build().perform();
    }

    public String GetAttributeValue(String ObjectName, String AttributeName) {
        try {
            return GetObject(ObjectName).getAttribute(AttributeName);
        } catch (Exception e) {
            return null;
        }
    }

    public String GetCSSValue(String ObjectName, String propertyName) {
        try {
            return GetObject(ObjectName).getCssValue(propertyName);
        } catch (Exception e) {
            return null;
        }
    }

    public void ClickByJavascript(String Identifier) {
        try {
            RunJavaScript(Identifier, "arguments[0].click()");
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured");
        }
    }

    public boolean DoubleClick(String ObjectName) {
        try {
            WebElement Element = GetObject(ObjectName);
            if (GetBrowserInfo().startsWith("IE"))
                MoveToObject(Element);
            UserAction.doubleClick(Element).build().perform();
            WaitForPageload();
            return true;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception is captured check screenshot: " + GetScreenshot("Exception"), e);
            return false;
        }
    }



    public void Click(String ObjectName) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            if (ActionObject.isEnabled() && ActionObject.isDisplayed()) {
                if ((ActionObject.getTagName().compareToIgnoreCase("a") == 0 || ActionObject.getTagName().compareToIgnoreCase("input") == 0 || ActionObject.getTagName().compareToIgnoreCase("img") == 0)) {
                    ActionObject.sendKeys(Keys.ENTER);
                } else {
                    if (GetBrowserInfo().startsWith("IE"))
                        MoveToObject(ActionObject);
                    try {
                        ActionObject.click();
                    } catch (TimeoutException e) {
                    }
                }
                WaitForPageload();
            } else {
                Environment.loger.log(Level.ERROR, ObjectName + " button is disabled");
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception is captured check screenshot: " + GetScreenshot("Exception"), e);
        }
    }

    public void ClickAndProceed(String ObjectName) {//NO WaitForPageLoad
        try {
            WebElement ActionObject = GetObject(ObjectName);
            if (ActionObject.isEnabled() && ActionObject.isDisplayed()) {
                if ((ActionObject.getTagName().compareToIgnoreCase("a") == 0 || ActionObject.getTagName().compareToIgnoreCase("input") == 0 || ActionObject.getTagName().compareToIgnoreCase("img") == 0)) {
                    ActionObject.sendKeys(Keys.ENTER);
                } else {
                    if (GetBrowserInfo().startsWith("IE"))
                        MoveToObject(ActionObject);
                    try {
                        ActionObject.click();
                    } catch (TimeoutException e) {
                    }
                }
            } else {
                Environment.loger.log(Level.ERROR, ObjectName + " button is disabled");
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception is captured check screenshot: " + GetScreenshot("Exception"), e);
        }
    }

    public void NormalClick(String ObjectName) {//Not using any keyboard actions
        try {
            WebElement ActionObject = GetObject(ObjectName);
            if (ActionObject.isEnabled() && ActionObject.isDisplayed()) {
                if (GetBrowserInfo().startsWith("IE"))
                    MoveToObject(ActionObject);
                try {
                    ActionObject.click();
                } catch (TimeoutException e) {
                }
                WaitForPageload();
            } else {
                Environment.loger.log(Level.ERROR, ObjectName + " button is disabled");
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception is captured check screenshot: " + GetScreenshot("Exception"), e);
        }
    }

    public boolean SwitchToFrame(String FrameObject) {
        try {
            if (FrameObject.trim().length() != 0) {
                String[] locatorInfo = GetLocatorInfo(FrameObject);
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(GetBy(locatorInfo[0], locatorInfo[1])));
                return true;
            } else {
                driver.switchTo().defaultContent();
                return true;
            }

        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return false;
        }
    }

    public void TakeScreenshot(String FileName) {
        CurrentScreenshot = GetScreenshot(FileName);
    }

    public void ClearScreehshot() {
        CurrentScreenshot = null;
    }

    public void CloseAllChildBroswer() {//TODO
        Set<String> AllWindow = driver.getWindowHandles();
        for (int i = 1; i < AllWindow.size(); i++) {
            driver.switchTo().window((String) AllWindow.toArray()[i]);
            driver.close();
        }
        driver.switchTo().window((String) AllWindow.toArray()[0]);
    }

    public boolean HandleAlert(boolean AcceptOrDecline) {
        if (IsAlertPresent()) {
            Alert alert = driver.switchTo().alert();
            //			Environment.loger.log(Level.WARN,"Alert: " + alert.getText() + " is displayed"); // Get the text from the alert
            if (AcceptOrDecline) {
                alert.accept();
                Environment.loger.log(Level.INFO, "Alert accepted");
            } else {
                alert.dismiss();
                Environment.loger.log(Level.INFO, "Alert dismissed");
            }
            WaitForPageload();
            //				driver.switchTo().window(mainWindowHandle); 		//Switch back to the application
            return true;
        } else {
            Environment.loger.log(Level.INFO, "Alert not present");
            return false;
        }
    }

    public boolean IsAlertPresent() {
        boolean ReturnValue = false;
        try {
            if (wait.until(ExpectedConditions.alertIsPresent()) != null)
                ReturnValue = true;
        } catch (TimeoutException e) {
            Environment.loger.log(Level.INFO, "Alert not present");
            ReturnValue = false;
        }
        return ReturnValue;
    }

    public String GetAlertText() {
        if (IsAlertPresent()) {
            return driver.switchTo().alert().getText();
        } else {
            Environment.loger.log(Level.ERROR, "Alert not present to get the text");
        }
        return null;
    }

    public void WaitForWindowCount(final int WindowCount) {
        try {
            // brij check
            wait.until(ExpectedConditions.numberOfWindowsToBe(WindowCount));
        } catch (TimeoutException e) {
            Environment.loger.log(Level.ERROR, "Another window doesnt open!TimeOut Exception.", e);
        } catch (Exception e) {
            throw e;
        }
    }

    public int GetCurrentWindowsCount() {
        try {
            return driver.getWindowHandles().size();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
        return -1;
    }

    public String GetPageSource() {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
        return null;
    }

    public boolean SwitchToWindow(int WhichWindow) {
        boolean flag = false;
        Set<String> windowHandles = null;
        try {
            windowHandles = driver.getWindowHandles();
            ArrayList<String> winHandle = new ArrayList<String>(windowHandles);
            String CurrentWinHandle = null;
            CurrentWinHandle = winHandle.get(WhichWindow - 1);
            driver.switchTo().window(CurrentWinHandle);
            driver.manage().window().maximize();
            if (GetBrowserInfo().startsWith("IE") && GetTitle().startsWith("Certificate Error")) {
                driver.get("javascript:document.getElementById('overridelink').click();");
            }
            WaitForPageload();
            flag = true;
        } catch (IndexOutOfBoundsException e) {
            Environment.loger.log(Level.ERROR, "Total window count is:" + windowHandles.size() + ". But you are trying to switch to " + WhichWindow, e);
            flag = false;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            flag = false;
        }
        return flag;

        //		Set<String> handles = driver.getWindowHandles();
        //		Iterator<String> winHandle = handles.iterator();
        //		//		handles.remove(mainWindowHandle);
        //		//		while(driver.getWindowHandles().iterator().hasNext())
        //		while(winHandle.hasNext()){
        //			//			String fwindow = winHandle.next();
        //			//			String swindow = winHandle.next();
        //			String childWindow = winHandle.next();
        //			if (childWindow != mainWindowHandle){
        //				//Switch control to new window
        //				driver.switchTo().window(childWindow);
        //			}
        //		}
    }

    public void EnterValue(String Identifier, CharSequence Text) {
        try {
            WebElement ActionObject = GetObject(Identifier);
            ActionObject.clear();
            ActionObject.sendKeys(Text);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Unable to enter the value in " + Identifier);
        }
    }

    public void EnterValue(String identifier, int Text) {
        try {
            WebElement ActionObject = GetObject(identifier);
            ActionObject.clear();
            ActionObject.sendKeys(String.valueOf(Text));
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
    }

    public void EnterValueByjavascript(String ObjectName) {//TODO
        WebElement ActionObject = GetObject(ObjectName);
        String IdXpath = "@id";


    }

    public String GetText(String Identifier) {
        String ReturnValue = "";
        try {
            WebElement ActionObject = GetObject(Identifier);
            String sTagName = ActionObject.getTagName().toUpperCase();
            if (CompareText(sTagName, "INPUT")) {
                sTagName = ActionObject.getAttribute("type").toUpperCase();
                //				if (CompareText(sTagName,"TEXT")){//EditBox
                //					ReturnValue = ActionObject.getAttribute("value");//"placeholder"
                if (CompareText(sTagName, "SUBMIT") || CompareText(sTagName, "BUTTON") || CompareText(sTagName, "TEXT")) {
                    ReturnValue = ActionObject.getAttribute("value");    //Button
                } else {
                    Environment.loger.log(Level.ERROR, "Check for this control - " + sTagName);
                    ReturnValue = ActionObject.getText();    //Unknown type with TagName as INPUT
                }
            } else {
                ReturnValue = ActionObject.getText();
                //Text - DIV,P,H1,H2,H3,UL,LI,LABEL
                //Link - A
                //BrowserTitle - TITLE
            }
        } catch (NullPointerException e) {
            ReturnValue = "OBJECT NOT FOUND";
        } catch (StaleElementReferenceException e) {
            ReturnValue = "STALE ELEMENT EXCEPTION";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, Identifier, e);
            ReturnValue = "ERROR OCCURED";
        }
        return ReturnValue;
    }

    public String GetText(WebElement Element) {
        String ReturnValue = "";
        try {
            ReturnValue = Element.getText();
        } catch (Exception e) {
            ReturnValue = "OBJECT NOT FOUND";
        }
        return ReturnValue;
    }

    public void ClearValue(String identifier) {
        try {
            WebElement ActionObject = GetObject(identifier);
            ActionObject.clear();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
    }

    public boolean CompareTextContained(String StepName, String Expected, String Actual) {
        boolean Return = false;
        if (CompareTextContained(Expected, Actual)) {
            Return = true;
            Reporter.Write(StepName, Expected, Actual, "PASS");
        } else {
            Return = false;
            Reporter.Write(StepName, Expected, Actual, "FAIL");
        }
        return Return;
    }

    public boolean CompareTextContained(String Expected, String Actual) {
        boolean Return = false;
        Expected = Expected.trim();
        Actual = Actual.trim();
        String BiggerText = " " + Actual + " ";
        String SmallerText = Expected;

        if (Actual.isEmpty() && !Expected.isEmpty()) {
            Return = false;
        } else if (Expected.isEmpty() && !Actual.isEmpty()) {
            Return = false;
        } else if (Actual.length() >= Expected.length()) {
            Return = BiggerText.contains(SmallerText);
        } else if (Expected.length() > Actual.length()) {
            BiggerText = " " + Expected + " ";
            SmallerText = Actual;
            Return = BiggerText.contains(SmallerText);
        }
        return Return;
    }

    public boolean CompareText(String StepName, String Expected, String Actual) {
        boolean Return = false;
        if (CompareText(Expected, Actual)) {
            Return = true;
            Reporter.Write(StepName, Expected, Actual, "PASS");
        } else {
            Reporter.Write(StepName, Expected, Actual, "FAIL");
        }
        return Return;
    }

    public boolean CompareText(String Expected, String Actual) {
        return Expected.trim().compareTo(Actual.trim()) == 0;
    }

    public void WaitForObject(String ObjectName) {
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            //   			wait.until(ExpectedConditions.elementToBeClickable(GetBy(locatorInfo[0], locatorInfo[1])));
                wait.until(ExpectedConditions.visibilityOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));  // WEBUI
            //    wait.until(ExpectedConditions.presenceOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));

            //    System.out.println("Wait completed");
        } catch (TimeoutException e) {
            //			Environment.loger.log(Level.ERROR, "Timeout Exception - OBJECT NAME:'"+ObjectName+"' "+GetScreenshot("ElementNoVisible-"+ObjectName),e);
            ApplicationExceptionHandler();
        } catch (Exception e) {
            throw e;
        }
    }

    public void WaitTillInvisibilityOfElement(String ObjectName) {
        String[] locatorInfo = new String[2];
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));
        } catch (TimeoutException e) {
            Environment.loger.log(Level.ERROR, "Timeout Exception", e);
        } catch (Exception e) {
            throw e;
        }
    }

    public void WaitTillElementToBeClickable(String ObjectName) {
        String[] locatorInfo = new String[2];
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            wait.until(ExpectedConditions.elementToBeClickable(GetBy(locatorInfo[0], locatorInfo[1])));
        } catch (TimeoutException e) {
            Environment.loger.log(Level.ERROR, "Timeout Exception:" + e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    public void WaitTillElementToBeVisible(String ObjectName) {
        String[] locatorInfo = new String[2];
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            wait.until(ExpectedConditions.visibilityOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));
        } catch (TimeoutException e) {
            Environment.loger.log(Level.ERROR, "Timeout Exception:" + e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    public void WaitTillPresenceOfElementLocated(String ObjectName) {
        String[] locatorInfo = new String[2];
        try {
            locatorInfo = GetLocatorInfo(ObjectName);
            wait.until(ExpectedConditions.presenceOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));
        } catch (TimeoutException e) {
            Environment.loger.log(Level.ERROR, "Timeout Exception", e);
        } catch (Exception e) {
            throw e;
        }
    }

    public void WaitForPageload() {
        long startTime, endTime, diffTime;
        //Get the starting Time
        startTime = System.currentTimeMillis();
        String BrowserStatus;
        //		Environment.loger.log(Level.INFO, "Browser loading Started!!!");
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Environment.loger.log(Level.INFO, "Exception in wait!", e);
            }
            endTime = System.currentTimeMillis();
            diffTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
            if (diffTime > 5) {
                Environment.loger.log(Level.INFO, "Browser is loading after 5 minutes!!!so proceeding without waiting.");
                break;
            }
            try {
                BrowserStatus = (String) RunJavaScript("return document.readyState;");
            } catch (Exception e) {
                BrowserStatus = "complete";//If exception occurred to avoid indefinite loop making the status as 'complete'
            }
        } while (!(BrowserStatus.compareToIgnoreCase("complete") == 0));
        BrowserStatus = null;
        //		Environment.loger.log(Level.INFO, "Browser loading Stopped!!!");
    }

	/*public void WaitTillInvisibilityOfElement(String invisibilityOfElement){
		long startTime, endTime, diffTime;
		//Get the starting Time
		startTime = System.currentTimeMillis();

		String BrowserStatus = "";
		do{
			//check for max timeout of 10minutes for the busy image
			endTime = System.currentTimeMillis();
			diffTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
			if (diffTime > 1){
				Environment.loger.log(Level.DEBUG, invisibilityOfElement+" object was displaying for 1 minutes, so proceeding without waiting.");
				break;
			}
			try{
				String[] locatorInfo = GetLocator(invisibilityOfElement).split("\\|");
				wait.until(ExpectedConditions.invisibilityOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));
				BrowserStatus = "complete";
			}catch(TimeoutException e){
				Environment.loger.log(Level.DEBUG, "Still Loading...");
				BrowserStatus = "Still loading..";
			}catch(Exception e){
				throw e;
			}
		}while (!(BrowserStatus.equalsIgnoreCase("complete")));
	}

	public void WaitTillElementToBeClickable(String ObjectName){//In this method alone using 'if' condition
		long startTime, endTime, diffTime;
		//Get the starting Time
		startTime = System.currentTimeMillis();
		String BrowserStatus = "";
		do{
			//check for max timeout of 10minutes for the busy image
			endTime = System.currentTimeMillis();
			diffTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
			if (diffTime > 1){
				Environment.loger.log(Level.DEBUG, ObjectName+" object wasn't displaying for 1 minutes, so proceeding without waiting.");
				break;
			}
			try{
				String[] locatorInfo = GetLocator(ObjectName).split("\\|");
				wait.until(ExpectedConditions.elementToBeClickable(GetBy(locatorInfo[0], locatorInfo[1])));
				BrowserStatus = "complete";
			}catch(TimeoutException e){
				Environment.loger.log(Level.DEBUG, "Still Loading...");
				BrowserStatus = "Still loading..";
			}catch(Exception e){
				throw e;
			}
		}while (!(BrowserStatus.equalsIgnoreCase("complete")));
	}

	public void WaitTillPresenceOfElementLocated(String invisibilityOfElement){
		long startTime, endTime, diffTime;
		//Get the starting Time
		startTime = System.currentTimeMillis();

		String BrowserStatus = "";
		do{
			//check for max timeout of 10minutes for the busy image
			endTime = System.currentTimeMillis();
			diffTime = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
			if (diffTime > 1){
				Environment.loger.log(Level.DEBUG, invisibilityOfElement+" object wasn't displaying for 1 minutes, so proceeding without waiting.");
				break;
			}
			try{
				String[] locatorInfo = GetLocator(invisibilityOfElement).split("\\|");
				wait.until(ExpectedConditions.presenceOfElementLocated(GetBy(locatorInfo[0], locatorInfo[1])));
				BrowserStatus = "complete";
			}catch(TimeoutException e){
				Environment.loger.log(Level.DEBUG, "Still Loading...");
				BrowserStatus = "Still loading..";
			}catch(Exception e){
				throw e;
			}
		}while (!(BrowserStatus.equalsIgnoreCase("complete")));
	}*/

    public boolean DropDown_SelectByText(String ObjectName, String TextToSlect) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            if (DropDownObject.isMultiple())
                DropDownObject.deselectAll();
            DropDownObject.selectByVisibleText(TextToSlect);
            return true;
        } catch (NoSuchElementException e) {
            Environment.loger.log(Level.ERROR, "There is no option with text: '" + TextToSlect + "' for the object :" + ObjectName);
            return false;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return false;
        }
    }

    public boolean DropDown_SelectByValue(String ObjectName, String Value) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            if (DropDownObject.isMultiple())
                DropDownObject.deselectAll();
            DropDownObject.selectByValue(Value);
            return true;
        } catch (NoSuchElementException e) {
            Environment.loger.log(Level.ERROR, "There is no option with value: '" + Value + "' for the object :" + ObjectName);
            return false;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return false;
        }
    }

    public boolean DropDown_SelectByIndex(String ObjectName, int index) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            if (DropDownObject.isMultiple())
                DropDownObject.deselectAll();
            DropDownObject.selectByIndex(index);
            return true;
        } catch (NoSuchElementException e) {
            Environment.loger.log(Level.ERROR, "There is no option with index: '" + index + "' for the object :" + ObjectName);
            return false;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return false;
        }
    }

    public int DropDown_GetSize(String ObjectName) {
        try {
            Select SelectObject = new Select(GetObject(ObjectName));
            return SelectObject.getOptions().size();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return -1;
        }
    }

    public String DropDown_GetSelectedText(String ObjectName) {
        String returnText = "";
        try {
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            returnText = DropDownObject.getFirstSelectedOption().getText();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return returnText.trim();
        }
        return returnText;
    }

    @Deprecated
    private int DropDown_GetTextByIndex(String ObjectName, int index) {//TODO Debug and cross check it
        int returnText = -1;
        try {
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            returnText = DropDownObject.getOptions().indexOf(index);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return returnText;
        }
        return returnText;
    }

    public List<String> DropDown_GetText(String ObjectName) {
        try {
            List<String> ReturnText = new ArrayList<String>();
            WebElement ActionObject = GetObject(ObjectName);
            Select DropDownObject = new Select(ActionObject);
            List<WebElement> listoptions = DropDownObject.getOptions();
            if (listoptions.size() > 0) {
                for (WebElement option : listoptions) {
                    ReturnText.add(GetText(option));
                }
                return ReturnText;
            } else {
                return null;
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            //			return "OBJECT NOT FOUND";
        }
        return null;
    }

    public Object RunJavaScript(String JScript) {//TODO
        try {
            return ((JavascriptExecutor) driver).executeScript(JScript);
        } catch (Exception e) {
            //			Environment.loger.log(Level.ERROR, "JavaScriptExecution failed. "+e.getMessage());
            return "complete";//Since using this method in WaitForPageload()
        }
    }

    public Object RunJavaScript(String ObjectName, String JScript) {//TODO
        try {
            WebElement we = GetObject(ObjectName);
            return ((JavascriptExecutor) driver).executeScript(JScript, we);
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return "Error";
        }
    }

    public void CloseOnlyThisBrowser() {
        try {
            if (driver != null) {
                driver.close();
            }
        } catch (Exception e) {
        }
    }

    public String GetTitle() {
        return driver.getTitle();
    }

    public String GetCurrentURL() {
        return driver.getCurrentUrl();
    }

    public String GetBrowserInfo() {

        int version;
        String returValue = "";
        String browswerName = "";
        String browserAgent = (String) RunJavaScript("return navigator.userAgent;");
        //		String browserAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");

        if ((version = browserAgent.indexOf("MSIE 9.0")) != -1) {
            returValue = "9";    //browserAgent.substring(version+5,version+6);
            browswerName = "IE";
        } else if ((version = browserAgent.indexOf("MSIE 7.0")) != -1) {
            returValue = "7";    //browserAgent.substring(version+5,version+6);
            browswerName = "IE";
        } else if ((version = browserAgent.indexOf("MSIE 8.0")) != -1) {
            returValue = "8";    //browserAgent.substring(version+5,version+6);
            browswerName = "IE";
        } else if ((version = browserAgent.indexOf("rv:11")) != -1) {
            returValue = "11";    //browserAgent.substring(version+3,version+5);
            browswerName = "IE";
        } else if ((version = browserAgent.indexOf("Chrome")) != -1) {
            returValue = browserAgent.substring(version + 7, version + 9);
            browswerName = "Chrome";
        } else if ((version = browserAgent.indexOf("Firefox")) != -1) {
            returValue = browserAgent.substring(version + 8);
            browswerName = "Firefox";
        }
        return browswerName + returValue;
    }

    public String GetTimeStamp(String DateFormat) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
        String formattedDate = sdf.format(date);
        date = null;
        sdf = null;
        return formattedDate;
    }

    public String DateAddDays(String InputDateString, String InputDateFormat, int NoOfDays, int IntervalType) {
        try {
            DateFormat formatter = new SimpleDateFormat(InputDateFormat);
            Date InputDate = formatter.parse(InputDateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(InputDate);
            cal.add(IntervalType, NoOfDays);
            return formatter.format(cal.getTime());
        } catch (Exception e) {
            Environment.loger.log(Level.INFO, "Exception occured:", e);
            return null;
        }
    }

    public String ChangeDateFormat(String InputDateString, String InputDateFormat, String FormatToChange) {
        String formatedString = null;
        try {
            SimpleDateFormat sdfSource = new SimpleDateFormat(InputDateFormat);
            Date date = sdfSource.parse(InputDateString);
            SimpleDateFormat sdfDestination = new SimpleDateFormat(FormatToChange);
            formatedString = sdfDestination.format(date);
        } catch (ParseException e) {
            Environment.loger.log(Level.ERROR, "Check whether Inpudate and InputDateFormats are same");
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
        return formatedString;
    }

    public int RandomNumber(int min, int max) {
        max = max + 1;
        if (max > min) {
            Random r = new Random();
            return (r.nextInt(max - min) + min);
        } else {
            return min;
        }
    }

    public String RandomString(int length) {
        //		RandomStringUtils.random(length,true,true);
        StringBuffer buffer = new StringBuffer();
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * letters.length());
            buffer.append(letters.charAt(index));
        }
        return buffer.toString();
    }

    public int RandomInteger(int length) {
        return Integer.parseInt(RandomStringUtils.random(length, false, true));

    }

    public void CheckBox(String Identifier, String ONorOFF) {//TODO Try to implement for IE with 'Space bar' keyboard button
        try {
            WebElement ActionObject = GetObject(Identifier);
            if (ONorOFF.equalsIgnoreCase("ON")) {
                if (!ActionObject.isSelected()) {
                    ActionObject.click();
                }
                try {
                    if (!ActionObject.isSelected()) {
                        ActionObject.click();
                    }
                } catch (StaleElementReferenceException e) {
                    Environment.loger.log(Level.INFO, "Page refreshed");
                }
            } else {
                if (ActionObject.isSelected()) {
                    ActionObject.click();
                }
                try {
                    if (ActionObject.isSelected()) {//TO avoid the StaleElementReferenceException exception
                        ActionObject.click();
                    }
                } catch (StaleElementReferenceException e) {
                    Environment.loger.log(Level.INFO, "Page refreshed");
                }
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured is-", e);
        }
    }

    public boolean CheckBoxIsSelected(String ObjectName) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            return ActionObject.isSelected();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return false;
        }
    }

    public boolean RadiobuttonIsSelected(String ObjectName) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            return ActionObject.isSelected();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return false;
        }
    }

    public boolean CheckBoxSetOptionForAll(String ObjectName, String ONorOFF) {
        try {
            List<WebElement> ListOfObjects = GetAllVisibleElements(ObjectName);
            for (WebElement ActionObject : ListOfObjects) {
                try {
                    if (ONorOFF.toUpperCase().trim().compareTo("ON") == 0) {
                        if (!ActionObject.isSelected())
                            ActionObject.click();
                    } else {
                        if (ActionObject.isSelected())
                            ActionObject.click();
                    }
                } catch (Exception ignore) {
                }
            }
            return true;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured - ", e);
            return false;
        }
    }

    public void RefreshPage() {
        driver.navigate().refresh();
    }

    public void SelectRadioButton(String Identifier) {
        try {
            WebElement ActionObject = GetObject(Identifier);
            if (!ActionObject.isSelected()) {
                ActionObject.click();
                try {
                    if (!ActionObject.isSelected()) {
                        ActionObject.click();
                    }
                    if (!ActionObject.isSelected()) {
                        RunJavaScript("document.getElementById('" + ActionObject.getAttribute("id") + "').checked=true;");
                    }
                } catch (StaleElementReferenceException ex) {
                    Environment.loger.log(Level.INFO, "Link has been clicked on previous action, So WebPage got refreshed and navigated to another page.");//TO avoid StaleElementReferenceException exception
                }
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured - ", e);
        }
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*DATABASE-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    public void FileUpload(String ObjectName, String FileName) {
        String Path;
        try {
            Path = Environment.DataPath + "\\Documents\\" + Environment.Tower + "\\" + FileName;
            File UploadFile = new File(Path);
            WebElement ActionObject = GetObject(ObjectName);
            ActionObject.sendKeys(UploadFile.getAbsolutePath());
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured - ", e);
        }
    }

    public void Wait(int TimeOutInSecs) {
        try {
            Thread.sleep(TimeOutInSecs * 1000);
        } catch (InterruptedException e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
        }
    }

    public String WebTbl_GetText(String TableId, int rowIndex, int colIndex) {
        try {
            WebElement ActionObject = GetObject(TableId);
            return GetText(ActionObject.findElement(By.xpath("tbody/tr[" + rowIndex + "]/td[" + colIndex + "]")));
            //			String XPath = GetXPath(TableId)+"/tbody/tr["+rowIndex+"]/td["+colIndex+"]";
            //			return GetText(GetObject(XPath)).trim();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return "Error Occured"; //Element does not exists
        }
    }

    public List<String> WebTbl_GetText(String TableId, int colIndex) {
        try {
            WebElement ActionObject = GetObject(TableId);
            String CellText = "";
            List<String> TableText = new ArrayList<String>();
            List<WebElement> Cells = ActionObject.findElements(By.xpath("tbody/tr/td[" + colIndex + "]"));
            for (int CurrentRow = 0; CurrentRow < Cells.size(); CurrentRow++) {
                CellText = GetText(Cells.get(CurrentRow));
                TableText.add(CellText);
            }
            return TableText;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, e);
            return null;
        }
    }
    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    //WebTable

    public int WebTbl_GetRowCount(String TableId) {
        try {
            WebElement ActionObject = GetObject(TableId);
            return ActionObject.findElements(By.xpath("tbody/tr")).size();
            //			String XPath = GetXPath(TableId)+"/tbody/tr[1]/td";
            //			return driver.findElements(By.xpath(XPath)).size();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return -1;
        }
    }

    public int WebTbl_GetColumnCount(String TableId) {
        try {
            WebElement ActionObject = GetObject(TableId);
            return ActionObject.findElements(By.xpath("tbody/tr[1]/td")).size();
            //			String XPath = GetXPath(TableId)+"/tbody/tr[1]/td";
            //			return driver.findElements(By.xpath(XPath)).size();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return -1;
        }
    }

    public List<String> WebTbl_GetHeader(String Table) {
        try {
            String CellText = "";
            WebElement ActionObject = GetObject(Table);

            List<String> TableText = new ArrayList<String>();
            List<WebElement> Cells = ActionObject.findElements(By.xpath("thead/tr/th"));
            for (WebElement CurrentRow : Cells) {
                if (CurrentRow.isDisplayed()) {
                    CellText = GetText(CurrentRow);
                    TableText.add(CellText);
                }
            }
            return TableText;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Exception occured", e);
            return null;
        }

    }

    public int WebTbl_GetRowIndex(String ObjectName, int ColIndex, String SearchString) {
        try {
            WebElement ActionObject = GetObject(ObjectName);
            String actualStr = null;
            List<WebElement> Cells = ActionObject.findElements(By.xpath("tbody/tr/td[" + ColIndex + "]"));
            for (int CurrentRow = 0; CurrentRow < Cells.size(); CurrentRow++) {
                actualStr = GetText(Cells.get(CurrentRow));
                if (CompareText(SearchString, actualStr)) {
                    return CurrentRow + 1;
                }
            }
            return -1;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return -1; //Element does not exists
        }
    }

    public int WebTbln_GetColumnIndex(String ObjectName, int RowIndex, String SearchString) {//TODO debug once
        try {
            WebElement ActionObject = GetObject(ObjectName);
            String actualStr = null;
            List<WebElement> Cells = ActionObject.findElements(By.xpath("tbody/tr[" + RowIndex + "]/td"));
            for (int CurrentRow = 0; CurrentRow < Cells.size(); CurrentRow++) {
                actualStr = GetText(Cells.get(CurrentRow));
                if (CompareText(SearchString, actualStr)) {
                    return CurrentRow + 1;
                }
            }
            return -1;
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
            return -1; //Element does not exists
        }
    }

    public void WebTbl_Click(String ObjectName, int RowIndex, int ColIndex) {//Pass identifier as XPATH only.
        String TempXpath;
        try {
            String Xpath = GetXPath(ObjectName);
            TempXpath = Xpath + "//tbody/tr[" + RowIndex + "]/td[" + ColIndex + "]/*";
            if (ObjectExists(TempXpath)) {
                ClickAndProceed(TempXpath);//Avoiding to use 'Click' because it will close the Pop-up.
            } else {
                TempXpath = Xpath + "/tbody/tr[" + RowIndex + "]/td[" + ColIndex + "]";
                ClickAndProceed(TempXpath);
            }
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, ObjectName, e);
        }
    }

    @Deprecated
    public void FailCurrentTest(String ErroMessage) {
        //		StackTraceElement[] TestList = Thread.currentThread().getStackTrace();
        //		Environment.loger.log(Level.ERROR, "Screenshot for failed @"+TestList[2].getMethodName()+" '"+GetScreenshot(ErroMessage)+"'");
        Reporter.Write("NA_FailCurrentTest", "", "FAILED-" + ErroMessage, "FAIL");
    }

    public String ReadXMLAsString(String FileName) {
        String FilePath = Environment.DataPath + "\\Documents\\" + Environment.Tower + "\\" + FileName;
        String ReturnValue = null;
        BufferedReader bufReader;
        try {
            Reader fileReader = new FileReader(FilePath);
            bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            ReturnValue = sb.toString();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, e);
        }
        return ReturnValue;
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    //Excel Related
    public String GetLocator(String ObjectName) {
        return ExcelUtil.ORLocator.GetLocator(ObjectName);
    }

    //Get Locator Type & Locator from ORLocator
    public String GetXPath(String ObjectName) {
        return ExcelUtil.ORLocator.GetXPath(ObjectName);
    }

    //To change the sheet in ORLocator.xlsx
    public void ORLocatorSetSheet(String SheetName) {
        ExcelUtil.ORLocator.SetSheet(SheetName);
    }

    //Retrieve TextID from TestData.xls
    public String TestData(String TextID) {
        return ExcelUtil.TestData.GetTestData(TextID);
    }

    //Write to TestData.xls
    public void WriteToTestData(String TextID, String TextToWrite) {
        ExcelUtil.TestData.WriteToTestData(TextID, TextToWrite);
    }

    //To change the sheet in TestDat.xls
    public void TestDataSetSheet(String SheetName) {
        ExcelUtil.TestData.SetSheet(SheetName);
    }

    public ExcelUtil LoadExcel(String FileName, String SheetName) {
        return new ExcelUtil(Environment.DataPath + "\\Documents\\" + Environment.Tower + "\\" + FileName, SheetName);
    }

    public enum SupportedBrowser {
        FF, GC, IE
    }

    public enum GetBy {
        ID, NAME, XPATH, CLASSNAME, LINKTEXT, PARTIALLINKTEXT, TAGNAME, CSS
    }

    public Actions keyDown(CharSequence key) {

        Actions ac = new Actions(driver);
        return ac.keyDown(key);
    }
}
