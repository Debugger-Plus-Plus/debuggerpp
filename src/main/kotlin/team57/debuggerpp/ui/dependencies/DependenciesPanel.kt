package team57.debuggerpp.ui.dependencies

import team57.debuggerpp.slicer.ProgramSlice
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

abstract class DependenciesPanel : JPanel() {
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
        val l = JLabel(
            "<html>${prefix}" +
                    "<font color='#5693E2'>" +
                    "${dependency.location.clazz} at Line ${dependency.location.lineNo}" +
                    "</font></html>"
        )
        l.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        add(l)
    }
}