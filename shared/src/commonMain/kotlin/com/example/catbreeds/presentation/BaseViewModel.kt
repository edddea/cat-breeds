package com.example.catbreeds.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class BaseViewModel {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    open fun clear() {
        scope.cancel()
    }
}
