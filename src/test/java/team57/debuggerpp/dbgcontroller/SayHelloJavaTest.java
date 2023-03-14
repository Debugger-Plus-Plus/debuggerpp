package team57.debuggerpp.dbgcontroller;

import com.automation.remarks.junit5.Video;
import com.intellij.remoterobot.RemoteRobot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import team57.debuggerpp.SayHelloKotlinTest;
import team57.debuggerpp.pages.WelcomeFrame;
import team57.debuggerpp.util.RemoteRobotExtension;
import team57.debuggerpp.util.StepsLogger;

import java.time.Duration;

import static com.intellij.remoterobot.fixtures.dataExtractor.TextDataPredicatesKt.startsWith;

@ExtendWith(RemoteRobotExtension.class)
public class SayHelloJavaTest {
    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @Test
    @Disabled
    @Video
    void checkSayHello(final RemoteRobot remoteRobot) {
        final WelcomeFrame welcomeFrame = remoteRobot.find(WelcomeFrame.class, Duration.ofSeconds(10));
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));
        if (!welcomeFrame.hasText("Say Hello")) {
            welcomeFrame.getMoreActions().click();
            welcomeFrame.getHeavyWeightPopup().findText("Say Hello").click();
        } else {
            welcomeFrame.findText("Say Hello").click();
        }
        final SayHelloKotlinTest.HelloWorldDialog helloDialog = remoteRobot.find(SayHelloKotlinTest.HelloWorldDialog.class);
        assert (helloDialog.getTextPane().hasText("Hello World!"));
        helloDialog.getOk().click();
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));
    }
}
