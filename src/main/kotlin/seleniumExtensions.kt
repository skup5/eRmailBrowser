import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

// ---------------------- WebDriver extensions

fun WebDriver.findElementBy(by: By): WebElement? {
    return try {
        this.findElement(by)
    } catch (e: NoSuchElementException) {
        null
    }
}


inline fun WebDriver.switchTab(tab: String): WebDriver = switchTo().window(tab)

fun WebDriver.switchToLastTab() {
    val tabs = ArrayList(this.windowHandles)
    if (tabs.isNotEmpty()) {
        this.switchTab(tabs[tabs.size - 1])
    }
}

/**
 * Closes all tabs on right side of tab.
 */
fun WebDriver.closeAllRightTabs(tab: String) {
    val tabs = ArrayList(this.windowHandles)
    val tabIndex = tabs.indexOf(tab) + 1
    for (index in tabIndex until tabs.size) {
        this.switchTab(tabs[index]).close()
    }
}

fun WebDriver.waitForElement(by: By): WebElement {
    return WebDriverWait(this, TIME_OUT)
        .until(ExpectedConditions.visibilityOfElementLocated(by))
}

fun WebDriver.waitForClickableElement(by: By): WebElement {
    return WebDriverWait(this, TIME_OUT)
        .until(ExpectedConditions.elementToBeClickable(by))
}

// ---------------------- WebElement extensions

/**
 * @param by - The locating mechanism
 * @return The first matching element on the current context or null If no matching elements are found.
 * @see WebElement.findElement
 */
fun WebElement.findElementBy(by: By): WebElement? {
    return try {
        this.findElement(by)
    } catch (e: NoSuchElementException) {
        null
    }
}

fun WebElement.waitForClickableElement(by: By): WebElement {
    return WebDriverWait(
        (this as RemoteWebElement).wrappedDriver,
        TIME_OUT
    )
        .until(ExpectedConditions.elementToBeClickable(by))
}
