import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Logger


data class Credentials(val email: String, val password: String)

const val TIME_OUT: Long = 45

fun main(args: Array<String>) {
    try {
        val credentials = readLoginCredentials("Přihlášení do emailu")
        val driver = ChromeDriver()
        val ermailBrowser = createBrowser(driver, credentials)
        ermailBrowser.login()
        Logger.getGlobal().severe("loging...")
        var again = false
        do {
            Console.focus()
            val maxReadEmailCount = Console.readNumber("Maximum otevřených emailů")
            Logger.getGlobal().severe("reading...")
            Console.println("Přečteno: ${ermailBrowser.read(maxReadEmailCount.toInt())} eRmailů.")
            Console.print("Pokračovat ve čtení? [ano/ne]")
            val answer = Console.readLine("Pokracovat")
            again = answer.isNotEmpty() && answer[0] == 'a'
        } while (again)
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
        else -> browser = OriginErmailBrowser(driver, credentials)
    }
    return browser
}

private fun readLoginCredentials(title: String): Credentials {
    Console.println(title)
    val email = Console.readLine("Email")
    val password = Console.readPassword("Password")
    return Credentials(email, password)
}
