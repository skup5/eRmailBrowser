import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.RemoteWebDriver

private const val LOGIN_URL = "https://ermail.cz"

/**
 *
 * @author Roman Zelenik
 */
class OriginErmailBrowser(private var driver: RemoteWebDriver, private var credentials: Credentials) : ErmailBrowser {
    override fun read(maxReadEmailCount: Int): Int {
        var readEmailCountdown = maxReadEmailCount
        val emailsTab = driver.windowHandle

        //  go through unread emails
        while (readEmailCountdown > 0) {
            try {
                // open unread email
                driver.waitForClickableElement(
                        By.cssSelector(".unreadEmails a[href*='ermail.cz']")).click()

                driver.switchToLastTab()

                // open eRmail
                driver.waitForClickableElement(
                        By.cssSelector("a[href*='ermail.cz/urlbind/']")
                ).click()
                Thread.sleep(500)
                readEmailCountdown--
                // switch tab back to email client
                driver.switchTo().window(emailsTab)
                // go back to email list
                driver.navigate().back()
            } catch (exception: WebDriverException) {
                exception.printStackTrace()
            }
        }

        Thread.sleep(5_000)
        driver.closeAllRightTabs(emailsTab)
        driver.switchTab(emailsTab)

        return maxReadEmailCount - readEmailCountdown

    }

    override fun login() {
        driver.get(LOGIN_URL)

        driver.waitForClickableElement(By.cssSelector("a[data-actiondata=loginWindow]")).click()
        val loginForm = driver.findElementByCssSelector("#loginWindow form")
        loginForm.findElement(By.cssSelector("input[name=email]")).sendKeys(credentials.email)
        loginForm.findElement(By.cssSelector("input[type=password]")).sendKeys(credentials.password)
        loginForm.findElement(By.cssSelector("button[type=submit]")).submit()

    }
}