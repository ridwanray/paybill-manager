package com.thomaskioko.daraja.repository.api.util.livedata

import android.arch.lifecycle.LiveData

/**
 * A LiveData class that has `null` value.
 */
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {
    init {
        // use post instead of set since this can be created on any thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }
}
