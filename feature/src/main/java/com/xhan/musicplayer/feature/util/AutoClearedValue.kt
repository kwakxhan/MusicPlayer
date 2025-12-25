package com.xhan.musicplayer.feature.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class AutoClearedValue<T : Any>(
    fragment: Fragment,
    private val initializer: () -> T
) : ReadOnlyProperty<Fragment, T> {

    private var _value: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    _value = null
                }
            })
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value ?: initializer().also { _value = it }
    }
}

fun <T : Any> Fragment.autoCleared(initializer: () -> T): AutoClearedValue<T> {
    return AutoClearedValue(this, initializer)
}