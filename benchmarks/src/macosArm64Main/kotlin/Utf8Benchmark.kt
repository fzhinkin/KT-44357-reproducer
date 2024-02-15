package org.example

import kotlinx.benchmark.*
import kotlinx.cinterop.*
import platform.Foundation.NSString
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Warmup(iterations = 2, time = 1, BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, BenchmarkTimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class Utf8Benchmark {
    @Param("well-formed-utf8", "\uD83D\uDE18\uFE0F\uD83D\uDE02\uD83D\uDE00\uD83D\uDE1D")
    var original: String = ""
    var originalBytes = ByteArray(0)
    var originalBytesPtr: CPointer<ByteVar> = nativeHeap.allocArray(0)

    @Setup
    fun setup() {
        originalBytes = original.repeat(128).encodeToByteArray()
        originalBytesPtr = nativeHeap.allocArrayOf(originalBytes)
    }

    @TearDown
    fun cleanup() {
        nativeHeap.free(originalBytesPtr)
    }

    @Benchmark
    fun toKString(): String {
        return originalBytesPtr.toKStringFromUtf8()
    }

    @Benchmark
    @OptIn(BetaInteropApi::class)
    fun copyingValidatingNSString(): String {
        return NSString.create(uTF8String = originalBytesPtr).toString()
    }

    @Benchmark
    fun strlenMemcpyThenDecodeByteArray(): String {
        val count = strlenNativeImpl(originalBytesPtr)
        ByteArray(count.toInt()).usePinned {
            memcpy(it.addressOf(0), originalBytesPtr, count.toULong())
            return it.get().decodeToString()
        }
    }

    @Benchmark
    fun copyToByteArrayThenDecodeByteArray(): String {
        val count = originalBytes.size
        ByteArray(count).usePinned {
            memcpy(it.addressOf(0), originalBytesPtr, count.toULong())
            return it.get().decodeToString()
        }
    }

    @Benchmark
    fun justDecodeByteArray(): String = originalBytes.decodeToString()
}
