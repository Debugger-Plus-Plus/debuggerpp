package team57.debuggerpp.ui.dependencies

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import team57.debuggerpp.slicer.ProgramSlice
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import javax.swing.*


abstract class DependenciesPanel(protected val project: Project) : JPanel() {
    companion object {
        val YELLOW: Color = Color.decode("#FFC000")
        val GREEN: Color = Color.decode("#00B050")
    }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
    }

    protected fun addTitleLabel(title: String, foreground: Color) {
        val l = JLabel("${title}:")
        l.foreground = foreground
        l.border = BorderFactory.createEmptyBorder(5, 0, 5, 0)
        add(l)
    }

    protected fun addEmptyLabel() {
        val l = JLabel("// Empty")
        l.foreground = Color.GRAY
        l.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        add(l)
    }

    protected fun addNoDependenciesMessage(name: String) {
        val l = JLabel("$name dependencies of this line is unavailable")
        l.border = BorderFactory.createEmptyBorder(5, 0, 5, 0)
        add(l)
    }

    protected fun addDependencyLine(prefix: String, dependency: ProgramSlice.Dependency) {
        val l = JButton()
        val searchScope = GlobalSearchScope.allScope(project)
        var clazz: PsiClass? = null

        ApplicationManager.getApplication().invokeAndWait {
            clazz = JavaPsiFacade.getInstance(project).findClass(dependency.location.clazz, searchScope)
        }

        clazz?.containingFile?.let { file ->
            l.addActionListener {
                OpenFileDescriptor(project, file.virtualFile, dependency.location.lineNo - 1, Int.MAX_VALUE)
                    .navigate(false)
            }
        }

        l.text = "<html>${prefix}" +
                "<font color='#5693E2'>" +
                "${dependency.location.clazz} at Line ${dependency.location.lineNo}" +
                "</font>" +
                "</html>"

        l.isFocusPainted = false
        l.margin = Insets(0, 0, 0, 0)
        l.isContentAreaFilled = false
        l.isBorderPainted = false
        l.isOpaque = false
        l.maximumSize = Dimension(l.preferredSize.width, 18)
        l.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)

        add(l)
    }
}