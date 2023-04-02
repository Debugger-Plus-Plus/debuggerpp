package team57.debuggerpp.dbgcontroller.pages;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JButtonFixture;
import com.intellij.remoterobot.fixtures.JTextFieldFixture;
import com.intellij.remoterobot.fixtures.JTreeFixture;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import org.junit.jupiter.api.BeforeAll;
import org.testng.annotations.Test;

import java.io.File;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static org.testng.Assert.assertTrue;

@Test
public class UITest {
    private static RemoteRobot robot;

    // Utils
    private void clearAndWrite(Keyboard keyboard, String text, int textLength) {
        for(int i = 0; i < textLength; i++) {
            keyboard.backspace();
        }
        keyboard.enterText(text);
    }

    private void clearAndWrite(Keyboard keyboard, String text) {
        clearAndWrite(keyboard, text, 10);
    }

    @Test
    public static void setupClass() throws InterruptedException{
        String javaVersion = System.getProperty("java.version");
        assertTrue(javaVersion.startsWith("11"));
        System.out.println("Set up, " + javaVersion);
        robot = new RemoteRobot("http://127.0.0.1:8082");

        final var projectRootDir = new File("src/test/TestProject").getAbsolutePath();

        if (!robot.getFinder().findMany(byXpath("//div[@class='FlatWelcomeFrame']")).isEmpty()) {
            WelcomeFrameFixture welcomeFrame = robot.find(WelcomeFrameFixture.class);
            welcomeFrame.openProjectLink().doubleClick();

            // Open the rover project
            JTextFieldFixture projectSelectionField = robot.find(JTextFieldFixture.class, byXpath("//div[@class='BorderlessTextField']"));
            Thread.sleep(3000);
            projectSelectionField.setText(projectRootDir);
            Thread.sleep(3000);
            robot.find(JButtonFixture.class, byXpath("//div[@text='OK']")).clickWhenEnabled();

            while (robot.getFinder().findMany(byXpath("//div[@text='Trust Project']")).isEmpty()) {
                Thread.sleep(2000);
            }
            robot.find(JButtonFixture.class, byXpath("//div[@text='Trust Project']")).click();
        }

        // Open project files
        Locator projectTreeLocator = byXpath("//div[@class='ProjectViewTree']");
        if (robot.getFinder().findMany(projectTreeLocator).isEmpty()) {
            robot.find(JButtonFixture.class, byXpath("//div[@tooltiptext='Project']")).clickWhenEnabled();
        }

        JTreeFixture projectTree = robot.find(JTreeFixture.class, projectTreeLocator);
        projectTree.expand("TestProject", "src", "Main");

        try {
            robot.find(JTreeFixture.class, projectTreeLocator).doubleClickPath(new String[]{"src", "Main.java"}, true);
        } catch (Exception e) {
            robot.find(JTreeFixture.class, projectTreeLocator).doubleClickPath(new String[]{"src", "Main"}, true);
        }

//        if (robot.getFinder().findMany(TABLE_LOCATOR).isEmpty()) {
//            // Open plugin if not already opened
//            robot.find(JButtonFixture.class, byXpath("//div[@text='Slicer4J']")).clickWhenEnabled();
//        }
//
//        // Wait for indexes to load
//        while (robot.getFinder().findMany(TABLE_LOCATOR).isEmpty()) {
//            Thread.sleep(2000);
//        }
//
//        if (!robot.getFinder().findMany(byXpath("//div[@text='Got It']")).isEmpty()) {
//            robot.find(JButtonFixture.class, byXpath("//div[@text='Got It']")).click();
//        }
//
//        Thread.sleep(10000);
//
//        // verify a sanity check that all the plugin components are there
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_UPDATE_LOCATOR).isEmpty());
//        Assertions.assertTrue(robot.find(JButtonFixture.class, TOOLBAR_UPDATE_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_RUN_LOCATOR).isEmpty());
//        Assertions.assertTrue(robot.find(JButtonFixture.class, TOOLBAR_RUN_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_STOP_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.find(JButtonFixture.class, TOOLBAR_STOP_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_MOVEUP_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.find(JButtonFixture.class, TOOLBAR_MOVEUP_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_MOVEDOWN_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.find(JButtonFixture.class, TOOLBAR_MOVEDOWN_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(TOOLBAR_SETTINGS_LOCATOR).isEmpty());
//        Assertions.assertTrue(robot.find(JButtonFixture.class, TOOLBAR_SETTINGS_LOCATOR).isEnabled());
//        Assertions.assertFalse(robot.getFinder().findMany(SC_FILE_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.getFinder().findMany(SC_LINE_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.getFinder().findMany(SC_VARS_LOCATOR).isEmpty());
//        Assertions.assertFalse(robot.getFinder().findMany(TABLE_LOCATOR).isEmpty());



    }

    private void runSlicer(Keyboard keyboard, String fileName, String lineNumber) {
        JTextFieldFixture file = robot.find(
                JTextFieldFixture.class,
                byXpath("//div[@accessiblename='Slicing Criterion']//div[@class='TextFieldWithBrowseButton']")
        );
        JTextFieldFixture line = robot.find(
                JTextFieldFixture.class,
                byXpath("//div[@accessiblename='Slicing Criterion']//div[@class='JTextField']")
        );

        file.click();
        clearAndWrite(keyboard, fileName);
        line.click();
        clearAndWrite(keyboard, lineNumber);
        keyboard.enter();
    }

    public void runSlicer(String fileName, String lineNumber) {
        runSlicer(new Keyboard(robot), fileName, lineNumber);
    }
}
