import org.beryx.textio.TextIoFactory
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

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
    fun readLine(prompt: String = ""): String = textIO.newStringInputReader()
        .withMinLength(0)
        .read(prompt)

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

const val timeOut: Long = 45

fun main(args: Array<String>) {
    try {
        val credentials = readLoginCredentials("Přihlášení do emailu")
        val maxReadEmailCount = Console.readNumber("Maximum otevřených emailů")
        val driver = ChromeDriver()
        val ermailBrowser = createBrowser(driver, credentials)
        Console.println("Přečteno: ${ermailBrowser.read(maxReadEmailCount.toInt())} eRmailů.")
        Console.print("Stiskni enter pro ukončení:")
        Console.readLine()
        driver.close()
    } catch (exception: Exception) {
        exception.printStackTrace()
    } finally {
        Console.exit()
    }
}

fun createBrowser(driver: RemoteWebDriver, credentials: Credentials): ErmailBrowser {
    val browser: ErmailBrowser
    when {
        credentials.email.endsWith("seznam.cz") -> browser = SeznamBrowser(driver, credentials)
        else -> browser = GmailBrowser(driver, credentials)
    }
    return browser
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
