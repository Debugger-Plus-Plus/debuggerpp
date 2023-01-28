package team57.debuggerpp.ui.dependencies

import team57.debuggerpp.slicer.ProgramSlice

class ControlDependenciesPanel : DependenciesPanel() {
    fun updateDependencies(dependencies: ProgramSlice.ControlDependencies?) {
        removeAll()
        if (dependencies == null) {
            addNoDependenciesMessage("Control")
        } else {
            addTitleLabel("From", YELLOW)
            updateDependencies(dependencies.from)
            addTitleLabel("To", GREEN)
            updateDependencies(dependencies.to)
        }
        updateUI()
    }

    private fun updateDependencies(dependencies: Collection<ProgramSlice.ControlDependency>) {
        for (dependency in dependencies) {
//            JavaPsiFacade.getInstance(project).findClass()
            addDependencyLine("", dependency)
        }
        if (dependencies.isEmpty())
            addEmptyLabel()
    }
}