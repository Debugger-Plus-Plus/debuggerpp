package team57.debuggerpp.ui.dependencies

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

abstract class DependenciesPanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
    }

    protected fun addTitleLabel(title: String) {
        val l = JLabel("${title}:")
        l.border = BorderFactory.createEmptyBorder(5, 0, 5, 0)
        add(l)
    }

    protected fun addEmptyLabel() {
        val l = JLabel("// Empty")
        l.foreground = Color.GRAY
        l.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
        add(l)
    }
}