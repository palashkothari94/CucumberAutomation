/* Purpose		:Channels-Application related reusable methods.
 * Developed By	:Brij
 * Modified By	:
 * Modified Date:
 * Reviewed By	:
 * Reviewed Date:
 */
package functions;


import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SCALE extends Utility {

    public static final String SW = null;
    private static final String DATE_FORMAT = "MMddyyyy";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    public WebDriver LaunchBrowser(String URL) {
        super.LaunchBrowser(URL);
        if (GetBrowserInfo().startsWith("IE") && GetTitle().startsWith("Certificate Error")) {
            driver.get("javascript:document.getElementById('overridelink').click();");
        }
        return driver;
    }

    public WebDriver LaunchAppiumBrowser(String URL) throws MalformedURLException {
        super.LaunchAppiumBrowser(URL);
        if (GetBrowserInfo().startsWith("IE") && GetTitle().startsWith("Certificate Error")) {
            driver.get("javascript:document.getElementById('overridelink').click();");
        }
        return driver;
    }

    public void WaitForPageload() {
        super.WaitForPageload();
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

}






