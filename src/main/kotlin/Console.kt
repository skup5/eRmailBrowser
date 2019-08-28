import org.beryx.textio.TextIoFactory
import java.awt.Window

/**
 *
 * @author Roman Zelenik
 */
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
     * Waits to user key press action.
     */
    fun pause() {
        textIO.newCharInputReader().withDefaultValue('.').read()
    }

    /**
     * Prints error message.
     */
    fun error(errorMsg: String) {
        textIO.textTerminal.executeWithPropertiesConfigurator(
                { props -> props.setPromptColor("red") },
                { terminal -> terminal.println(errorMsg) }
        )
    }

    /**
     * Closes console.
     */
    fun exit() = textIO.dispose()

    /**
     * @param prompt the messages to be displayed for prompting the user to enter the value
     */
    fun readNumber(prompt: String = ""): Number = textIO.newDoubleInputReader().read(prompt)

    fun focus() {
        val activeWindow = getSelectedWindow(Window.getWindows())
        activeWindow?.requestFocusInWindow()
    }

    private fun getSelectedWindow(windows: Array<Window>): Window? {
        var result: Window? = null
        for (i in windows.indices) {
            val window = windows[i]
            if (window.isActive) {
                result = window
            } else {
                val ownedWindows = window.ownedWindows
                if (ownedWindows != null) result = getSelectedWindow(ownedWindows)
            }
        }
        return result
    }
}