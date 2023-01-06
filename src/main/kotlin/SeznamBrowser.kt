import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Level
import java.util.logging.Logger

private const val LOGIN_URL = "https://email.seznam.cz"
private const val ERMAIL_URL = "$LOGIN_URL/#eRmail"

class SeznamBrowser(private var driver: RemoteWebDriver, private var credentials: Credentials) : ErmailBrowser {

    val log = Logger.getGlobal()

    override fun login() {
        driver.get(LOGIN_URL)

        val loginFormSelector = "form.login"
        val loginUserNameInputSelector = "#login-username"
        val loginPasswordSelector = "#login-password"
        val loginSubmitSelector = "button[type=submit]"

        val loginForm = driver.findElementBy(By.cssSelector(loginFormSelector))
        if (loginForm == null) {
            log.severe("Cannot find login form by selector $loginFormSelector")
            return
        }

        val loginUserNameInput = loginForm.findElementBy(By.cssSelector(loginUserNameInputSelector))
        if (loginUserNameInput == null) {
            log.severe("Cannot find login form input by selector $loginUserNameInputSelector")
            return
        }

        loginUserNameInput.sendKeys(credentials.email)
        val loginSubmit = loginForm.findElementBy(By.cssSelector(loginSubmitSelector))
        if (loginSubmit == null) {
            log.severe("Cannot find login submit by selector $loginSubmitSelector")
            return
        }
        loginSubmit.submit()

        val loginPassword = loginForm.findElementBy(By.cssSelector(loginPasswordSelector))
        if (loginPassword == null) {
            log.severe("Cannot find login form input by selector $loginPasswordSelector")
            return
        }
        loginPassword.sendKeys(credentials.password)
        loginSubmit.submit()

        // if there is Security update page, click on Remind me later
        loginForm.findElementBy(By.cssSelector("""data-locale="secure-phone.phone.skip""""))?.click()
    }

    override fun read(maxReadEmailCount: Int): Int {
        var readEmailCountdown = maxReadEmailCount
        val emailsTab = driver.windowHandle

        //  choose eRmail folder
        driver.get(ERMAIL_URL)

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
            } catch (exception: TimeoutException) {
                log.log(Level.WARNING, "Cannot find element until timeout $TIME_OUT", exception)
            } catch (exception: WebDriverException) {
                log.log(Level.SEVERE, "Error while reading ermails.", exception)
            }
        }

        Thread.sleep(5_000)
        driver.closeAllRightTabs(emailsTab)
        driver.switchTab(emailsTab)

        return maxReadEmailCount - readEmailCountdown

    }

}