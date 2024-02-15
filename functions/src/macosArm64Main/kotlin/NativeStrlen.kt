package org.example
import kotlinx.cinterop.*

// Doesn't look pretty, but I didn't find a better way as SymbolName is no longer public
@OptIn(ExperimentalForeignApi::class)
fun strlenNativeImpl(ptr: CPointer<ByteVar>): ULong = libc.strlenNative(ptr)
