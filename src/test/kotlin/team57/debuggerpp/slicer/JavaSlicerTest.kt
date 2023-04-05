package team57.debuggerpp.slicer

import ca.ubc.ece.resess.slicer.dynamic.core.graph.Parser
import ca.ubc.ece.resess.slicer.dynamic.slicer4j.Slicer
import com.intellij.util.io.exists
import com.intellij.util.io.readText
import junit.framework.TestCase
import team57.debuggerpp.util.SourceLocation
import team57.debuggerpp.util.Utils
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.jar.JarInputStream
import java.util.zip.ZipInputStream
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString


class JavaSlicerTest : TestCase() {
    companion object {
        private val loggerPath: String
        private val modelsPath: String
        private val stubDroidPath: String
        private val taintWrapperPath: String

        init {
            val loggerFile = kotlin.io.path.createTempFile("slicer4-logger-", ".jar")
            val loggerJar = Slicer::class.java.getResourceAsStream("/DynamicSlicingLogger.jar")!!
            Files.copy(loggerJar, loggerFile, StandardCopyOption.REPLACE_EXISTING)
            loggerPath = loggerFile.toString()

            val modelsDirectory = createTempDirectory("slicer4-models-")
            val modelsZip = Slicer::class.java.getResourceAsStream("/models.zip")!!
            Utils.unzipAll(ZipInputStream(modelsZip), modelsDirectory)
            modelsPath = modelsDirectory.toString()
            stubDroidPath = modelsDirectory.resolve("summariesManual").toString()
            taintWrapperPath = modelsDirectory.resolve("EasyTaintWrapperSource.txt").toString()
        }
    }

    private val slicer = JavaSlicer()

    private val outputDirectory = createTempDirectory("slicer4j-outputs-")
    private val staticLog = outputDirectory.resolve("slicer4j-static.log")
    private val outJarPath = outputDirectory.resolve("instrumented.jar")
    private val stdoutLog = outputDirectory.resolve("instrumented-stdout.log")
    private val icdgLog = outputDirectory.resolve("icdg.log")

    private fun runTest(
        jarPathName: String, slicingFromLine: SourceLocation,
        expectedRawTrace: String?, expectedSliceLog: String, expectedDependenciesLogSha256: String?
    ) {
        val jarPath = this.javaClass.classLoader.getResource(jarPathName)!!.path
        val processDirs = Collections.singletonList(jarPath)

        // Open the folder for debugging
        // Desktop.getDesktop().open(outputDirectory.toFile())

        // Check instrumentation
        assertFalse(outJarPath.exists())
        slicer.instrumentJar(jarPath, staticLog.pathString, outputDirectory, outJarPath.pathString)
        assertTrue(outJarPath.exists())

        val jar = outJarPath.pathString.replace("\\", "/")
        val mainClass = JarInputStream(BufferedInputStream(FileInputStream(jarPath)))
            .manifest.mainAttributes.getValue("Main-Class")

        // Run and get log
        val process = Runtime.getRuntime().exec("java -cp $jar $mainClass")
        stdoutLog.bufferedWriter().use { output ->
            InputStreamReader(process.inputStream).use { input ->
                input.copyTo(output)
            }
        }
        process.waitFor()

        // save trace
        val trace = Parser.readFile(stdoutLog.pathString, staticLog.pathString)
        assertNotNull(trace)

        val traceLog = outputDirectory.resolve("trace.log")
        assertFalse(traceLog.exists())
        slicer.saveTrace(trace, outputDirectory)
        assertTrue(traceLog.exists())

        val rawTraceLog = outputDirectory.resolve("raw-trace.log")
        assertFalse(rawTraceLog.exists())
        slicer.extractRawTrace(stdoutLog, outputDirectory)
        if (expectedRawTrace != null) {
            assertEquals(expectedRawTrace, rawTraceLog.readText())
        }

        // get ICDG Graph
        val icdg = slicer.createDynamicControlFlowGraph(icdgLog, trace, Collections.singletonList(jarPath))
        assertNotNull(icdg)

        // Get slicing location
        val slicingCriteria = slicer.locateSlicingCriteria(icdg, slicingFromLine)
        assertNotNull(slicingCriteria)

        // Slice!
        val dynamicSlice = slicer.slice(
            outputDirectory.pathString, icdg, processDirs,
            slicingCriteria.map { s -> s.lineNo }, stubDroidPath, taintWrapperPath,
            null, null, true, false
        )
        assertNotNull(dynamicSlice)

        // Check slicer output
        val actualSliceLog = Utils.readTextReplacingLineSeparator(outputDirectory.resolve("slice.log"))
        assertEquals(HashSet(expectedSliceLog.split("\n")), HashSet(actualSliceLog.split("\n")))
        if (expectedDependenciesLogSha256 != null) {
            assertEquals(
                expectedDependenciesLogSha256,
                Utils.getFileContentSha256(outputDirectory.resolve("slice-dependencies.log"))
            )
        }
    }

    fun testBasic() {
        runTest(
            "TestProjectBasic.jar", SourceLocation("Main", 11),
            "20:1-21:1-3:1-4:1-7:1-8:1-11:1-5:1-6:1-",
            "Main:10\nMain:16\nMain:18\nMain:23\nMain:11",
            "462f343119270269af4a6e44c4745f69e7f0a75a2c3c25dbb6e3989922328a4f"
        )
    }

    fun testTiny() {
        runTest(
            "TestProjectTiny.jar", SourceLocation("Main", 4),
            "3:1-6:1-7:1-4:1-5:1-",
            "Main:3\nMain:7\nMain:8\nMain:4",
            "7883b7b0979c3865b2e66af0e62c3bb6e12c656f88d67fcced5a52d46b012a17"
        )
    }

    fun testException() {
        runTest(
            "TestProjectException.jar", SourceLocation("Main", 7),
            "3:1-8:1-4:1-14:1-16:1-9:1-10:1-12:1-19:1-20:1-7:1-6:1-",
            "Main:3\nMain:12\nMain:5\nMain:25\nMain:28\nMain:16\nMain:17\nMain:31\nMain:7",
            "cfd7c3e696ecfe9d696a119f1d1c46225b6877c54e0370063dddb593dc0da214"
        )
    }

    fun testStaticVariable() {
        runTest(
            "TestProjectStaticVariable.jar", SourceLocation("Main", 7),
            "16:1-17:1-3:1-9:1-10:1-4:1-11:1-12:1-5:1-13:1-14:1-6:1-7:1-8:1-",
            "Main:5\nMain:16\nMain:6\nMain:20\nMain:21\nMain:7",
            "4bc8747f1f2ebe5eff6a1612cf691131c2fa9e56c609324f2db06e135395496b"
        )
    }

    fun testMultithreading() {
        runTest(
            "TestProjectMultithreading.jar", SourceLocation("Main", 36),
            null, // Nondeterministic
            "Main:8\nMain\$lambda_main_0__1:-1\nMain:16\nMain:35\nMain\$lambda_main_1__2:-1\nMain:17\nMain:27\nMain:28\nMain:36",
            null, // Nondeterministic
        )
    }

    fun testMultipleClasses() {
        runTest(
            "TestProjectMultipleClasses.jar", SourceLocation("Main2", 7),
            null, // Nondeterministic
            "Main2:18\nMain2:6\nMain2:7\nMain:8\nMain:7\nMain2:15\nMain2:-1",
            "14b58959344c43b0883aaa01b45bce71f4f69a7b87b3bcaef458572632af1fa8"
        )
    }
}
