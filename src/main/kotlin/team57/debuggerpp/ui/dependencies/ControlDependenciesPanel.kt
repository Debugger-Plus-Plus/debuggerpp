package team57.debuggerpp.ui.dependencies

import team57.debuggerpp.slicer.ProgramSlice
import javax.swing.BorderFactory
import javax.swing.JLabel

class ControlDependenciesPanel : DependenciesPanel() {
    fun updateDependencies(dependencies: ProgramSlice.ControlDependencies?) {
        removeAll()
        if (dependencies == null) {
            add(JLabel("Control dependencies of this line is unavailable"))
        } else {
            updateDependencies("From", dependencies.from)
            updateDependencies("To", dependencies.to)
        }
        updateUI()
    }

    private fun updateDependencies(title: String, dependencies: Collection<ProgramSlice.ControlDependency>) {
        addTitleLabel(title)
        for (dependency in dependencies) {
//            JavaPsiFacade.getInstance(project).findClass()
            val l = JLabel("${dependency.location.clazz} at Line ${dependency.location.lineNo}")
            l.border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            add(l)
        }
        if (dependencies.isEmpty())
            addEmptyLabel()
    }
}