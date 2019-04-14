import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Logger

private const val LOGIN_URL = "https://accounts.google.com/b/0/addmailservice"

class GmailBrowser(private var driver: RemoteWebDriver, private var credentials: Credentials) : ErmailBrowser {

    override fun read(maxReadEmailCount: Int): Int {
        var readEmailCountdown = maxReadEmailCount
        val emailsTab = driver.windowHandle

        //  choose eRmail folder
        driver.waitForClickableElement(By.cssSelector("a[href*=eRmail]")).click()

        //  go through unread emails
        while (readEmailCountdown > 0) {

            Thread.sleep(3000)

            Logger.getGlobal().severe("searching unreaded email...")

            try {
                // open unread email
                driver.findElement(
                        By.cssSelector("tr.zA.zE span[email*=ermail]")).click()

                Logger.getGlobal().severe("opening unreaded email...")

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

        val loginForm = driver.findElementByTagName("form")
        loginForm.findElement(By.id("identifierId")).sendKeys(credentials.email)
        loginForm.waitForClickableElement(By.id("identifierNext")).click()
        loginForm.waitForClickableElement(By.cssSelector("input[type=password]")).sendKeys(credentials.email)
        loginForm.waitForClickableElement(By.id("passwordNext")).click()
    }

}
