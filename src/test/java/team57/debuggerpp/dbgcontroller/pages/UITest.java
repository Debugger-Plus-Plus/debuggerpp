package team57.debuggerpp.dbgcontroller.pages;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.*;
import com.intellij.remoterobot.fixtures.ContainerFixture;
import com.intellij.remoterobot.fixtures.JButtonFixture;
import com.intellij.remoterobot.fixtures.JTextFieldFixture;
import com.intellij.remoterobot.fixtures.JTreeFixture;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import org.assertj.swing.core.MouseButton;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runners.MethodSorters;
import org.testng.annotations.Test;
import team57.debuggerpp.pages.IdeaFrame;
import team57.debuggerpp.pages.WelcomeFrame;

import java.io.File;
import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
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

    public static void openProject() throws InterruptedException {
        String javaVersion = System.getProperty("java.version");
        assertTrue(javaVersion.startsWith("11"));
        System.out.println("Set up, " + javaVersion);
        robot = new RemoteRobot("http://127.0.0.1:8082");

        if (!robot.getFinder().findMany(byXpath("//div[@class='FlatWelcomeFrame']")).isEmpty()) {
            System.out.println("Enter Welcome page");
            WelcomeFrame welcomeFrame = robot.find(WelcomeFrame.class);
            welcomeFrame.openProject(new File("src/test/TestProject").getAbsolutePath());
            System.out.println("Finished Welcome page\n");
        }

        // Open project file
        final IdeaFrame idea = robot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());
        step("Open Main.java", () -> {
            final ContainerFixture projectView = idea.getProjectViewTree();
            if (!projectView.hasText("src")) {
                projectView.findText(idea.getProjectName()).doubleClick();
                waitFor(() -> projectView.hasText("src"));
                projectView.findText("src").doubleClick();
            }
            if (!projectView.hasText("Main")) {
                projectView.findText(idea.getProjectName()).doubleClick();
                waitFor(() -> projectView.hasText("src"));
                projectView.findText("src").doubleClick();
            }
            projectView.findText("Main").doubleClick();
        });

        // Wait for indexes to load
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());
        if (!robot.getFinder().findMany(byXpath("//div[@text='Got It']")).isEmpty()) {
            robot.find(JButtonFixture.class, byXpath("//div[@text='Got It']")).click();
        }
        System.out.println("Finished Indexing\n");
        Assertions.assertFalse(robot.getFinder().findMany(DEBUGGER_PP_UP_BTN).isEmpty());
    }

    @Test
    public void uiTestMain() throws InterruptedException {
        openProject();
        testDebuggerppBtnWithoutSlicingCriteria();
        testDebuggerppBtnNothingMsg();
        testRightClick();
        checkGrayoutLine();
    }


    private void testDebuggerppBtnWithoutSlicingCriteria() throws InterruptedException {
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
    private void testDebuggerppBtnNothingMsg(){
        if(!robot.getFinder().findMany(DEBUGGER_PP_DOWN_BTN).isEmpty()){
            robot.find(JButtonFixture.class, DEBUGGER_PP_DOWN_BTN).click();
            //display "nothing to show"
            Assertions.assertFalse(robot.getFinder().findMany(byXpath("//div[@visible_text='Nothing to show']")).isEmpty());
            robot.find(JButtonFixture.class, byXpath("//div[@myvisibleactions='[Show Options Menu (null), Hide (Hide active tool window)]']//div[@myaction.key='tool.window.hide.action.name']")).click();
        }
        else{
            System.out.println("Cannot find expect element\n");
        }
    }

    private void testRightClick() throws InterruptedException {
        if(!robot.getFinder().findMany(byXpath("//div[@class='EditorComponentImpl']")).isEmpty()){
//            int offset = robot.find(EditorFixture.class, byXpath("//div[@class='EditorComponentImpl']")).getCaretOffset();
            robot.find(EditorFixture.class, byXpath("//div[@class='EditorComponentImpl']")).clickOnOffset(609, MouseButton.RIGHT_BUTTON, 1);
        }
        else{
            System.out.println("Cannot find expect element\n");
        }
        robot.find(JButtonFixture.class, byXpath("//div[@text='Start Slicing from Line']"), Duration.ofSeconds(10)).click();
        Thread.sleep(5000);

        //if there is a Decompiler Warning
        if (!robot.getFinder().findMany(byXpath("//div[@class='DialogRootPane']")).isEmpty()) {
            System.out.println("Warning window popped up\n");
            robot.find(JButtonFixture.class, byXpath("//div[contains(@text.key, 'button.accept')]"), Duration.ofSeconds(10)).click();
        }
    }

    private void checkGrayoutLine(){
        //TODO
    }

    private void testExecutedLine(){

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
