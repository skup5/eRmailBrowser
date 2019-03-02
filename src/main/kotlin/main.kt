import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import java.io.Console
import org.beryx.textio.TextIoFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement

data class Credentials(val email: String, val password: String)

const val timeOut: Long = 10

fun main() {
    val driver = ChromeDriver()
    try {
        println("Přečteno: ${seznamEmail(driver, 2)} eRmailů.")
        print("Stiskni enter pro ukončení:")
        readLine()
        driver.close()
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

private fun seznamEmail(driver: ChromeDriver, maxReadEmailCount: Int = 1): Int {
    var readEmailCountdown = maxReadEmailCount
    val emailsTab = driver.windowHandle

    val (email, password) = readLoginCredentials("Přihlášení do emailu")

    driver.get("https://email.seznam.cz")

    //  login
    val loginForm = driver.findElementByCssSelector("form.login")
    loginForm.findElement(By.id("login-username")).sendKeys(email)
    loginForm.findElement(By.id("login-password")).sendKeys(password)
    loginForm.findElement(By.cssSelector("button[type=submit]")).submit()

//    waitForLoad(driver)

    //  choose eRmail folder
    val eRmailFolder = driver.waitForClickableElement(By.cssSelector("a[href*=eRmail]"))
    eRmailFolder.click()

    //  go through unread emails
    while (readEmailCountdown > 0) {
        val unreadEmail = driver.waitForClickableElement(
                By.cssSelector("#list .message-list .unread a[href*=eRmail]")
        )
        // open unread email
        unreadEmail.click()
        // open eRmail
        driver.waitForClickableElement(By.cssSelector(".message .body a[href*='ermail.cz/urlbind/']")).click()
        readEmailCountdown--
        // switch tab back to email client
        driver.switchTo().window(emailsTab)
        // go back to email list
        driver.navigate().back()
    }

    Thread.sleep(6_000)
    driver.closeAllRightTabs(emailsTab)
    driver.switchTab(emailsTab)

    return maxReadEmailCount - readEmailCountdown

}

private inline fun WebDriver.switchTab(tab: String): WebDriver = switchTo().window(tab)

/**
 * Closes all tabs on right side of tab.
 */
private fun WebDriver.closeAllRightTabs(tab: String) {
    val tabs = ArrayList(this.windowHandles)
    val tabIndex = tabs.indexOf(tab) + 1
    for (index in tabIndex until tabs.size) {
        this.switchTab(tabs[index]).close()
    }
}

private fun WebElement.waitForClickableElement(by: By): WebElement {
    return WebDriverWait((this as RemoteWebElement).wrappedDriver, timeOut).until(ExpectedConditions.elementToBeClickable(by))
}

private fun WebDriver.waitForElement(by: By): WebElement {
    return WebDriverWait(this, timeOut).until(ExpectedConditions.visibilityOfElementLocated(by))
}

private fun WebDriver.waitForClickableElement(by: By): WebElement {
    return WebDriverWait(this, timeOut).until(ExpectedConditions.elementToBeClickable(by))
}

private fun waitForLoad(driver: WebDriver) {
    val pageLoadCondition =
            ExpectedCondition {
                (it as JavascriptExecutor).executeScript("return document.readyState") == "complete"
            }
    val wait = WebDriverWait(driver, timeOut)
    wait.until(pageLoadCondition)
}

private fun readLoginCredentials(title: String): Credentials {
    val textIO = TextIoFactory.getTextIO()
    textIO.textTerminal.println(title)

    val email = textIO.newStringInputReader()
            .read("Email")

    val password = textIO.newStringInputReader()
            .withInputMasking(true)
            .read("Password")

    textIO.dispose()

    return Credentials(email, password)
}

fun readLogin(): Array<String> {
    //Best to declare Console as a nullable type since System.console() may return null
    val console: Console? = System.console()

    when (console) {
        //In this case, the JVM is not connected to the console so we need to exit
        null -> {
            println("Not connected to console. Exiting")
            System.exit(-1)
        }
        //Otherwise we can proceed normally
        else -> {
            val userName = console.readLine("Email:")
            val pw = console.readPassword("Password:")
            val credentials = arrayOf(userName, pw.toString())

            //This is important! We don't know when the character array
            //will get garbage collected and we don't want it to sit around
            //in memory holding the password. We can't control when the array
            //gets garbage collected, but we can overwrite the password with
            //blank spaces so that it doesn't hold the password.
            for (i in 0 until pw.size) {
                pw[i] = ' '
            }

            return credentials
        }
    }

    return emptyArray()
}
