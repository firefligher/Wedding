package dev.fir3.wedding.external

import joptsimple.ArgumentAcceptingOptionSpec

internal inline fun <reified TValue> ArgumentAcceptingOptionSpec<*>.ofType() =
    ofType(TValue::class.java)
