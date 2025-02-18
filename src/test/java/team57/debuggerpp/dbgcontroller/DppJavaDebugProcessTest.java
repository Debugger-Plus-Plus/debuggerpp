package team57.debuggerpp.dbgcontroller;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.guieffect.qual.UI;
import org.junit.Test;
import team57.debuggerpp.slicer.JavaSlicer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DppJavaDebugProcessTest {

    public DppJavaDebugProcessTest() throws IOException {
    }

    @Test
    public void testSliceLogFile() {
        try {
            // check slice.log
            FileWriter myWriter = new FileWriter("sliceTest.log");
            myWriter.write(
                "team57.debuggerpp.Main:35\n" +
                    "team57.debuggerpp.Main:18\n" +
                    "team57.debuggerpp.Main:20\n" +
                    "team57.debuggerpp.Main:21\n" +
                    "team57.debuggerpp.Main:22\n" +
                    "team57.debuggerpp.Main:28\n" +
                    "team57.debuggerpp.Main:31\n" +
                    "team57.debuggerpp.Main:36\n");
            myWriter.close();
            JavaSlicer javaSlicer = new JavaSlicer();
//            javaSlicer.instrumentJar();

            File file1 = new File("sliceTest.log");
            Path generatedFile = Paths.get("src\\test\\kotlin\\team57\\debuggerpp\\execute\\generatedFile\\slice.log");
//            File file2 = new File(String.valueOf(generatedFile));
//            Desktop.getDesktop().open(file1);
//            Desktop.getDesktop().open(file2);
            filesCompareByLine(generatedFile, file1.toPath());
            boolean isTwoEqual = filesCompareByLine(generatedFile, file1.toPath());
            assertEquals(isTwoEqual, true);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Test
    public void testTraceLofFile() {
        try {
            // check trace.log
            FileWriter myWriter = new FileWriter("traceTest.log");
            myWriter.write(
                        "18, <team57.debuggerpp.Main: void main(java.lang.String[])>, $stack2 = staticinvoke <team57.debuggerpp.Main: int test1(int,int)>(10, 15), 1\n" +
                            "11, <team57.debuggerpp.Main: int test1(int,int)>, z = y - 5, 1\n" +
                            "11, <team57.debuggerpp.Main: int test1(int,int)>, r = z + 5, 1\n" +
                            "11, <team57.debuggerpp.Main: int test1(int,int)>, if x <= 0 goto tmpString = \"13\", 1\n" +
                            "12, <team57.debuggerpp.Main: int test1(int,int)>, z = x + y, 1\n" +
                            "12, <team57.debuggerpp.Main: int test1(int,int)>, goto [?= tmpString = \"14\"], 1\n" +
                            "14, <team57.debuggerpp.Main: int test1(int,int)>, goto [?= tmpString = \"15\"], 1\n" +
                            "15, <team57.debuggerpp.Main: int test1(int,int)>, if r == 15 goto tmpString = \"17\", 1\n" +
                            "17, <team57.debuggerpp.Main: int test1(int,int)>, return z, 1\n" +
                            "19, <team57.debuggerpp.Main: void main(java.lang.String[])>, $stack3 = <java.lang.System: java.io.PrintStream out>, 1\n" +
                            "19, <team57.debuggerpp.Main: void main(java.lang.String[])>, virtualinvoke $stack3.<java.io.PrintStream: void println(int)>($stack2), 1\n" +
                            "20, <team57.debuggerpp.Main: void main(java.lang.String[])>, return, 1\n");
            myWriter.close();
            File file1 = new File("traceTest.log");
            Path generatedFile = Paths.get("src\\test\\kotlin\\team57\\debuggerpp\\execute\\generatedFile\\trace.log");
//            File file2 = new File(String.valueOf(generatedFile));
//            Desktop.getDesktop().open(file1);
//            Desktop.getDesktop().open(file2);
            filesCompareByLine(generatedFile, file1.toPath());
            boolean isTwoEqual = filesCompareByLine(generatedFile, file1.toPath());
            assertEquals(isTwoEqual, true);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }




    public static boolean filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String line1 = "", line2 = "";
            while ((line1 = bf1.readLine()) != null) {
                line2 = bf2.readLine();
                if (line2 == null || !line1.equals(line2)) {
                    return false;
                }
                lineNumber++;
            }
            return bf2.readLine() == null;
        }
    }


    public void testRunToPosition() {

    }
}