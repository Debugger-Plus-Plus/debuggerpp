package team57.debuggerpp.dbgcontroller.pages;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JButtonFixture;
import com.intellij.remoterobot.fixtures.JTextFieldFixture;
import com.intellij.remoterobot.fixtures.JTreeFixture;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runners.MethodSorters;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static org.testng.Assert.assertTrue;

@Test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UITest {
    private static RemoteRobot robot;
    private static final Locator DEBUGGER_PP_UP_BTN = byXpath("//div[@class='ActionButton' and @myaction='Debug with Dynamic Slicing using Debugger++ (Debug selected configuration with dynamic slicing using Debugger++)']");
    private static final Locator DEBUGGER_PP_DOWN_BTN = byXpath("//div[@text='Debugger++']");


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

    @BeforeAll
    public static void setupClass() throws InterruptedException {
        String javaVersion = System.getProperty("java.version");
        assertTrue(javaVersion.startsWith("11"));
        System.out.println("Set up, " + javaVersion);
        robot = new RemoteRobot("http://127.0.0.1:8082");

        final var projectRootDir = new File("src/test/TestProject").getAbsolutePath();

        if (!robot.getFinder().findMany(byXpath("//div[@class='FlatWelcomeFrame']")).isEmpty()) {
            System.out.println("Enter Welcome page");
            WelcomeFrameFixture welcomeFrame = robot.find(WelcomeFrameFixture.class);
            welcomeFrame.openProjectLink().doubleClick();

            JTextFieldFixture projectSelectionField = robot.find(JTextFieldFixture.class, byXpath("//div[@class='BorderlessTextField']"), Duration.ofSeconds(5));
            projectSelectionField.setText(projectRootDir);
            robot.find(JButtonFixture.class, byXpath("//div[@text='OK']"), Duration.ofSeconds(5)).clickWhenEnabled();

//            while (robot.getFinder().findMany(byXpath("//div[@text='Trust Project']")).isEmpty()) {
//                Thread.sleep(2000);
//            }
//            robot.find(JButtonFixture.class, byXpath("//div[@text='Trust Project']")).click();
            System.out.println("Finished Welcome page\n");
        }

        // Open project files
//        System.out.println("Open project\n");
        Locator projectTreeLocator = byXpath("//div[@class='ProjectViewTree']");
        if (robot.getFinder().findMany(projectTreeLocator).isEmpty()) {
            robot.find(JButtonFixture.class, byXpath("//div[@tooltiptext='Project']"), Duration.ofSeconds(15)).clickWhenEnabled();
        }

        System.out.println("Get Project Tree\n");
        JTreeFixture projectTree = robot.find(JTreeFixture.class, projectTreeLocator, Duration.ofSeconds(15));
        System.out.println("Received Project Tree\n");
        projectTree.expand("TestProject", "src", "Main");

        try {
            robot.find(JTreeFixture.class, projectTreeLocator).doubleClickPath(new String[]{"src", "Main.java"}, true);
        } catch (Exception e) {
            robot.find(JTreeFixture.class, projectTreeLocator).doubleClickPath(new String[]{"src", "Main"}, true);
        }
        System.out.println("Opened Project\n");

        // Wait for indexes to load
        while (!robot.find(JButtonFixture.class, DEBUGGER_PP_UP_BTN).isEnabled()) {
            Thread.sleep(2000);
        }

        if (!robot.getFinder().findMany(byXpath("//div[@text='Got It']")).isEmpty()) {
            robot.find(JButtonFixture.class, byXpath("//div[@text='Got It']")).click();
        }
        Thread.sleep(5000);
        System.out.println("Finished Indexing\n");
        Assertions.assertFalse(robot.getFinder().findMany(DEBUGGER_PP_UP_BTN).isEmpty());
    }

    @Test
    public void mainTest() throws InterruptedException {
        testDebuggerppBtnWithoutSlicingCriteria();
        testDebuggerppBtnNothingMsg();
        
    }

    public void testDebuggerppBtnWithoutSlicingCriteria() throws InterruptedException {
        System.out.println("Click on Debugger Button");
        JButtonFixture debuggerppUpBtn = robot.find(JButtonFixture.class, DEBUGGER_PP_UP_BTN);
        debuggerppUpBtn.clickWhenEnabled();
        while(robot.getFinder().findMany(byXpath("//div[@class='MyDialog']")).isEmpty()){
            Thread.sleep(2000);
        }
        Assertions.assertFalse(robot.getFinder().findMany(byXpath("//div[@class='MyDialog']")).isEmpty());
        Assertions.assertTrue(robot.find(JButtonFixture.class, byXpath("//div[@text.key='button.ok']")).isEnabled());
        System.out.println("No Slicing Criteria Windows popped up");

        // Debugger++ down button should appear
        Assertions.assertFalse(robot.getFinder().findMany(DEBUGGER_PP_DOWN_BTN).isEmpty());
        Assertions.assertTrue(robot.find(JButtonFixture.class, DEBUGGER_PP_DOWN_BTN).isEnabled());
        System.out.println("Debugger++ down button appeared");
        robot.find(JButtonFixture.class, byXpath("//div[@text.key='button.ok']")).click();
    }

    public void testDebuggerppBtnNothingMsg(){
        if(!robot.getFinder().findMany(DEBUGGER_PP_DOWN_BTN).isEmpty()){
            robot.find(JButtonFixture.class, DEBUGGER_PP_DOWN_BTN).click();
            //display "nothing to show"
            Assertions.assertFalse(robot.getFinder().findMany(byXpath("//div[@visible_text='Nothing to show']")).isEmpty());
            robot.find(JButtonFixture.class, byXpath("//div[@myvisibleactions='[Show Options Menu (null), Hide (Hide active tool window)]']//div[@myaction.key='tool.window.hide.action.name']")).click();
        }
    }

//    private void runSlicer(Keyboard keyboard, String fileName, String lineNumber) {
//        JTextFieldFixture file = robot.find(
//                JTextFieldFixture.class,
//                byXpath("//div[@accessiblename='Slicing Criterion']//div[@class='TextFieldWithBrowseButton']")
//        );
//        JTextFieldFixture line = robot.find(
//                JTextFieldFixture.class,
//                byXpath("//div[@accessiblename='Slicing Criterion']//div[@class='JTextField']")
//        );
//
//        file.click();
//        clearAndWrite(keyboard, fileName);
//        line.click();
//        clearAndWrite(keyboard, lineNumber);
//        keyboard.enter();
//    }
//
//    public void runSlicer(String fileName, String lineNumber) {
//        runSlicer(new Keyboard(robot), fileName, lineNumber);
//    }
}
