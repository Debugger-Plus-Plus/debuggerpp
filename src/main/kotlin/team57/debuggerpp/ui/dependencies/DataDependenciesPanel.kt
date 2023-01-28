package team57.debuggerpp.ui.dependencies

import team57.debuggerpp.slicer.ProgramSlice

class DataDependenciesPanel : DependenciesPanel() {
    fun updateDependencies(dependencies: ProgramSlice.DataDependencies?) {
        removeAll()
        if (dependencies == null) {
            addNoDependenciesMessage("Data")
        } else {
            addTitleLabel("From", YELLOW)
            updateDependencies(dependencies.from)
            addTitleLabel("To", GREEN)
            updateDependencies(dependencies.to)
        }
        updateUI()
    }

    private fun updateDependencies(dependencies: Collection<ProgramSlice.DataDependency>) {
        for (dependency in dependencies) {
            if (dependency.variableName.isEmpty())
                continue
//            JavaPsiFacade.getInstance(project).findClass()
            addDependencyLine("${dependency.variableName}: ", dependency)
        }
        if (dependencies.isEmpty())
            addEmptyLabel()
    }
}