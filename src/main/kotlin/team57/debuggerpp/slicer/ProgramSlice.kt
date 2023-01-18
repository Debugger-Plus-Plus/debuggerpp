package team57.debuggerpp.slicer

import ca.ubc.ece.resess.slicer.dynamic.core.slicer.DynamicSlice

class ProgramSlice(val dynamicSlice: DynamicSlice) {
    val sliceLinesUnordered: Map<String, Set<Int>> = run {
        val map = HashMap<String, MutableSet<Int>>()
        for (sliceNode in dynamicSlice.map { x -> x.o1.o1 }) {
            val set = map.getOrPut(sliceNode.javaSourceFile) { HashSet() }
            set.add(sliceNode.javaSourceLineNo)
        }
        return@run map
    }
}
