/* Purpose		:All environmental related stuffs.
 * Developed By	:Brij
 * Modified By	:
 * Modified Date:20-May-2018
 * Reviewed By	:
 * Reviewed Date:
 */

package functions;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * @author Brij
 **/
public class Environment extends org.apache.log4j.HTMLLayout {

    public static boolean IsRunningFromMaven = false;
    //Logger object
    public static Logger loger = null;
    //Application URL Initialization
    public static String Scale, ScaleRF, WarehouseMobile, SeleniumWM;
    //*-*-*-*-*-*-*-*-*-*-*-*-*Default Browser*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static String defaultBrowser = "IE";
    //*-*-*-*-*-*-*-*-*-*-*-*-*Application Tower*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static String Tower = null;
    //Global Path Configuration
    protected static String frameworkPath = System.getProperty("user.dir");
    public static String DataPath = frameworkPath + "\\Data";
    public static String Documents = DataPath + "\\Documents";
    public static String ResultPath = frameworkPath + "\\test-output";
    protected static String LibraryPath = DataPath + "\\Library";
    //Browser Object
    protected static WebDriver BrowserObj = null;
    protected static String ORDataSheet = null;
    protected static String TestDataSheet = null;
    //Environment Configuration
    static String runEnvironment = "QA3";//QA2 or QA3 0r QA4 or STAGE

    public static String getRunEnvironment() {
        return runEnvironment;
    }

    static void setRunEnvironment(String runEnvironment) {
        Environment.runEnvironment = runEnvironment;
    }

    public static void SetBrowserToUse(String BrowserName) {
        defaultBrowser = BrowserName.toUpperCase();
    }

    public static String GetBrowserUsed() {
        return defaultBrowser;
    }

    public static void LoadSheetName() {
        if (Tower.equalsIgnoreCase("CRM")) {
            ORDataSheet = "CRM";
            TestDataSheet = "CRM";
        } else if (Tower.equalsIgnoreCase("CRS")) {
            ORDataSheet = "CRS";
            TestDataSheet = "CRS";
        } else if (Tower.equalsIgnoreCase("ScaleRF")) {
            ORDataSheet = "ScaleRF";
            TestDataSheet = "Scale";
        } else if (Tower.equalsIgnoreCase("Scale")) {
            ORDataSheet = "Scale";
            TestDataSheet = "Scale";
        }
        else if (Tower.equalsIgnoreCase("WarehouseMobile")) {
            ORDataSheet = "WarehouseMobile";
            TestDataSheet = "WarehouseMobile";
        }
        else if (Tower.equalsIgnoreCase("SeleniumWM")) {
            ORDataSheet = "SeleniumWM";
            TestDataSheet = "SeleniumWM";
        }
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*Applications URL declaration*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    //IF ANY NEW URL TO BE INCLUDED CONTACT AUTOMATION TEAM
    public static void LoadURL() {
        if (runEnvironment.equalsIgnoreCase("QA3")) {

            Scale = "https://scaleqa03.manhdevscale.com/scale/";
            WarehouseMobile = "https://scaleqa03.manhdevscale.com/WarehouseMobile/";
            SeleniumWM = "https://scaleqa03.manhdevscale.com/scale/";
            ScaleRF = "https://selenium-test.ilsnetdev.pri/RF/logon.aspx";

        } else if (runEnvironment.equalsIgnoreCase("QA2")) {

            Scale = "https://autoenv.manhdevscale.com/scale/";
            //	Scale ="https://awpdscrdp2016.ilsnetdev.pri/scale";
            ScaleRF = "https://autoenv.manhdevscale.com/RF/logon.aspx";


        } else if (runEnvironment.equalsIgnoreCase("QA1")) {

            WarehouseMobile = "http://bwvd-rdscale19.asia.manh.com/JsonConfigurator/landing-page";
            Scale = "https://hinst192.manhdevscale.com/scale/";
            ScaleRF = "https://cu2018update.ilsnetdev.pri/RF/logon.aspx";

        } else if (runEnvironment.equalsIgnoreCase("STAGE")) {

            WarehouseMobile = "https://sodactiveaad.manhdevscale.com/scale/";
            Scale = "https://hinst192.manhdevscale.com/scale/";
            ScaleRF = "https://awpdscrdp2016.ilsnetdev.pri/RF/logon.aspx";

        } else {
            Environment.loger.log(Level.FATAL, "Environment -'" + runEnvironment + "'is Invalid");
            throw new RuntimeException("Environment -" + runEnvironment + " is Invalid");
        }
    }
}
