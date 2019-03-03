import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.beryx.textio.TextIoFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement

data class Credentials(val email: String, val password: String)

object Console {
    private val textIO = TextIoFactory.getTextIO()

    /**
     * Prints a message that possibly contains line separators.
     */
    fun print(msg: String = "") = textIO.textTerminal.print(msg)

    /**
     * Prints a message that possibly contains line separators and subsequently prints a line separator.
     */
    fun println(msg: String = "") = textIO.textTerminal.println(msg)

    /**
     * @param prompt the messages to be displayed for prompting the user to enter the value
     */
    fun readLine(prompt: String = ""): String = textIO.newStringInputReader().read(prompt)

    /**
     * User will see just * instead of right characters.
     * @param prompt the messages to be displayed for prompting the user to enter the value
     */
    fun readPassword(prompt: String = ""): String = textIO.newStringInputReader()
        .withInputMasking(true)
        .read(prompt)

    /**
     * Closes console.
     */
    fun exit() = textIO.dispose()

    /**
     * @param prompt the messages to be displayed for prompting the user to enter the value
     */
    fun readNumber(prompt: String = ""): Number = textIO.newDoubleInputReader().read(prompt)
}

const val timeOut: Long = 10

fun main(args: Array<String>) {
    val driver = ChromeDriver()
    try {
        val credentials = readLoginCredentials("Přihlášení do emailu")
        val maxReadEmailCount = Console.readNumber("Maximum otevřených emailů")
        Console.println("Přečteno: ${seznamEmail(driver, credentials, maxReadEmailCount.toInt())} eRmailů.")
        Console.print("Stiskni enter pro ukončení:")
        Console.readLine()
        driver.close()
    } catch (exception: Exception) {
        exception.printStackTrace()
    } finally {
        Console.exit()
    }
}

private fun seznamEmail(driver: ChromeDriver, credentials: Credentials, maxReadEmailCount: Int = 1): Int {
    var readEmailCountdown = maxReadEmailCount
    val emailsTab = driver.windowHandle

    driver.get("https://email.seznam.cz")

    //  login
    val loginForm = driver.findElementByCssSelector("form.login")
    loginForm.findElement(By.id("login-username")).sendKeys(credentials.email)
    loginForm.findElement(By.id("login-password")).sendKeys(credentials.password)
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
    return WebDriverWait(
        (this as RemoteWebElement).wrappedDriver,
        timeOut
    ).until(ExpectedConditions.elementToBeClickable(by))
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
    Console.println(title)
    val email = Console.readLine("Email")
    val password = Console.readPassword("Password")
    return Credentials(email, password)
}
