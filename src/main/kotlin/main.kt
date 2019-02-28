import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebElement
import java.io.Console
import java.io.Serializable

fun main() {
    println("Přihlašení do emailu")
    val (email, password) = readLogin()
    println("$email, $password")

    val driver = ChromeDriver()
    driver.get("https://email.seznam.cz")

//    val headline = driver.findElementByTagName("h1")
//    println(headline.text)

    val loginForm = driver.findElementByCssSelector("form.login")
    loginForm.findElement(By.id("login-username")).sendKeys(email)
    loginForm.findElement(By.id("login-password")).sendKeys(password)
    loginForm.findElement(By.ByCssSelector("button[type=submit]")).submit()

//    driver.close()
}

fun readLoginCredentials(): Array<String> {
    print("Email:")
    val email = readLine()!!
    print("Heslo:")
    val password = readLine()!!
    return arrayOf(email, password)
}

fun readLogin(): Array<String> {
    //Best to declare Console as a nullable type since System.console() may return null
    val console : Console? = System.console()

    when (console){
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
            for (i in 0 until pw.size){
                pw[i] = ' '
            }

            return credentials
        }
    }

    return emptyArray()
}
