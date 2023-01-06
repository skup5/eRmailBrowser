import org.openqa.selenium.SessionNotCreatedException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.logging.Logger


data class Credentials(val email: String, val password: String)

const val TIME_OUT: Long = 45
val log = Logger.getGlobal()

fun main(args: Array<String>) {
    var exitStatus = -1;
    try {
        val driver = ChromeDriver()
        val credentials = readLoginCredentials("Přihlášení do emailu")
        val ermailBrowser = createBrowser(driver, credentials)
        ermailBrowser.login()
        log.info("loging...")
        var again = false
        do {
            Console.focus()
            val maxReadEmailCount = Console.readNumber("Maximum otevřených emailů")
            log.info("reading...")
            Console.println("Přečteno: ${ermailBrowser.read(maxReadEmailCount.toInt())} eRmailů.")
            Console.print("Pokračovat ve čtení? [ano/ne]")
            val answer = Console.readLine()
            again = answer.isNotEmpty() && answer[0] == 'a'
        } while (again)
        log.info("closing...")
        driver.close()
        exitStatus = 0
    } catch (exception: SessionNotCreatedException){
        Console.error(exception.localizedMessage)
        Console.pause()
    }
    catch (exception: Exception) {
        exception.printStackTrace()
    } finally {
        Console.exit()
        System.exit(exitStatus)
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
    val password = Console.readPassword("Heslo")
    return Credentials(email, password)
}
