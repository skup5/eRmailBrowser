import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.RemoteWebDriver

private const val LOGIN_URL = "https://email.seznam.cz"

class SeznamBrowser(private var driver: RemoteWebDriver, private var credentials: Credentials) : ErmailBrowser {

    override fun login() {
        driver.get(LOGIN_URL)

        val loginForm = driver.findElementByCssSelector("form.login")
        loginForm.findElement(By.id("login-username")).sendKeys(credentials.email)
        loginForm.findElement(By.id("login-password")).sendKeys(credentials.password)
        loginForm.findElement(By.cssSelector("button[type=submit]")).submit()

    }

    override fun read(maxReadEmailCount: Int): Int {
        var readEmailCountdown = maxReadEmailCount
        val emailsTab = driver.windowHandle

        //  choose eRmail folder
        driver.waitForClickableElement(By.cssSelector("a[href*=eRmail]")).click()

        //  go through unread emails
        while (readEmailCountdown > 0) {
            try {
                // open unread email
                driver.waitForClickableElement(
                        By.cssSelector("#list .message-list .unread a[href*=eRmail]")
                ).click()
                // open eRmail
                driver.waitForClickableElement(
                        By.cssSelector(".message .body a[href*='ermail.cz/urlbind/']")
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

}