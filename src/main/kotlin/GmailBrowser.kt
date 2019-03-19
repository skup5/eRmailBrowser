import org.openqa.selenium.remote.RemoteWebDriver

class GmailBrowser(private var driver: RemoteWebDriver, private var credentials: Credentials) : ErmailBrowser {
    override fun read(maxReadEmailCount: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
