import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import java.io.Console
import org.beryx.textio.TextIoFactory
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition


fun main() {
    val driver = ChromeDriver()
    try {
        seznamEmail(driver)

        readLine()
    } catch (exception: Exception) {
        exception.printStackTrace()
    } finally {
//        driver.close()
    }
}

private fun seznamEmail(driver: ChromeDriver) {
    val (email, password) = readLoginCredentials("Přihlášení do emailu")

    driver.get("https://email.seznam.cz")

    //  login
    val loginForm = driver.findElementByCssSelector("form.login")
    loginForm.findElement(By.id("login-username")).sendKeys(email)
    loginForm.findElement(By.id("login-password")).sendKeys(password)
    loginForm.findElement(By.cssSelector("button[type=submit]")).click()

    waitForLoad(driver)

    //  choose eRmail folder
    val eRmailFolder = driver.findElementByPartialLinkText("eRmail")
    eRmailFolder.click()

    waitForLoad(driver)

    //  go through unread emails
    val emailList = driver.findElementByCssSelector("#list .message-list")
    println(emailList.getAttribute("class"))
    for (unreadEmail in emailList.findElements(By.cssSelector(".unread a"))) {
        unreadEmail.click()
        break
    }

}

fun waitForLoad(driver: WebDriver) {
    val pageLoadCondition =
        ExpectedCondition {
            (it as JavascriptExecutor).executeScript("return document.readyState") == "complete"
        }
    val wait = WebDriverWait(driver, 30)
    wait.until(pageLoadCondition)
}

fun readLoginCredentials(title: String): Array<String> {
    val textIO = TextIoFactory.getTextIO()
    textIO.textTerminal.println(title)

    val email = textIO.newStringInputReader()
        .read("Email")

    val password = textIO.newStringInputReader()
        .withInputMasking(true)
        .read("Password")

    textIO.dispose()

    return arrayOf(email, password)
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
