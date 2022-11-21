package team57.debuggerpp.trace

import com.intellij.xdebugger.impl.DebuggerSupport
import com.intellij.xdebugger.impl.actions.DebuggerActionHandler
import com.intellij.xdebugger.impl.actions.XDebuggerActionBase

class SliceStepForward : XDebuggerActionBase() {
    override fun getHandler(debuggerSupport: DebuggerSupport): DebuggerActionHandler {
        return debuggerSupport.stepOverHandler
    }
}