package team57.debuggerpp

import com.automation.remarks.junit5.Video
import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.search.locators.byXpath

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import team57.debuggerpp.pages.WelcomeFrame
import team57.debuggerpp.util.RemoteRobotExtension
import team57.debuggerpp.util.StepsLogger
import java.time.Duration

@ExtendWith(RemoteRobotExtension::class)
class SayHelloKotlinTest {
    init {
        StepsLogger.init()
    }

    @Test
    @Disabled
    @Video
    fun checkHelloMessage(remoteRobot: RemoteRobot) = with(remoteRobot) {
        find(WelcomeFrame::class.java, timeout = Duration.ofSeconds(10)).apply {
            if (hasText("Say Hello")) {
                findText("Say Hello").click()
            } else {
                moreActions.click()
                heavyWeightPopup.findText("Say Hello").click()
            }
        }

        val helloDialog = find(HelloWorldDialog::class.java)

        assert(helloDialog.textPane.hasText("Hello World!"))
        helloDialog.ok.click()
    }

    @DefaultXpath("title Hello", "//div[@title='Hello' and @class='MyDialog']")
    class HelloWorldDialog(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(remoteRobot, remoteComponent) {
        val textPane: ComponentFixture
            get() = find(byXpath("//div[@class='Wrapper']//div[@class='JEditorPane']"))
        val ok: ComponentFixture
            get() = find(byXpath("//div[@class='JButton' and @text='OK']"))
    }
}