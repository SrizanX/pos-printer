package com.srizan.util.ditherer

object DitherPresets {

    // Floyd-Steinberg: Divisor 16, diffuses 100% of the error — classic, fast
    val floydSteinberg = DitherKernel(16f, listOf(
        DitherOffset( 1, 0, 7f),
        DitherOffset(-1, 1, 3f), DitherOffset(0, 1, 5f), DitherOffset(1, 1, 1f)
    ))

    // Atkinson: Divisor 8, intentionally diffuses only 6/8ths of the error — high contrast
    val atkinson = DitherKernel(8f, listOf(
        DitherOffset( 1, 0, 1f), DitherOffset(2, 0, 1f),
        DitherOffset(-1, 1, 1f), DitherOffset(0, 1, 1f), DitherOffset(1, 1, 1f),
        DitherOffset( 0, 2, 1f)
    ))

    // Burkes: Divisor 32, diffuses 100% of the error over 2 rows
    val burkes = DitherKernel(32f, listOf(
        DitherOffset( 1, 0, 8f), DitherOffset( 2, 0, 4f),
        DitherOffset(-2, 1, 2f), DitherOffset(-1, 1, 4f), DitherOffset(0, 1, 8f), DitherOffset(1, 1, 4f), DitherOffset(2, 1, 2f)
    ))

    // Stucki: Divisor 42, diffuses 100% of the error over 3 rows — sharper than Burkes
    val stucki = DitherKernel(42f, listOf(
        DitherOffset( 1, 0, 8f), DitherOffset( 2, 0, 4f),
        DitherOffset(-2, 1, 2f), DitherOffset(-1, 1, 4f), DitherOffset(0, 1, 8f), DitherOffset(1, 1, 4f), DitherOffset(2, 1, 2f),
        DitherOffset(-2, 2, 1f), DitherOffset(-1, 2, 2f), DitherOffset(0, 2, 4f), DitherOffset(1, 2, 2f), DitherOffset(2, 2, 1f)
    ))

    // Jarvis-Judice-Ninke: Divisor 48, diffuses 100% of the error over 3 rows — smooth gradients
    val jarvisJudiceNinke = DitherKernel(48f, listOf(
        DitherOffset( 1, 0, 7f), DitherOffset( 2, 0, 5f),
        DitherOffset(-2, 1, 3f), DitherOffset(-1, 1, 5f), DitherOffset(0, 1, 7f), DitherOffset(1, 1, 5f), DitherOffset(2, 1, 3f),
        DitherOffset(-2, 2, 1f), DitherOffset(-1, 2, 3f), DitherOffset(0, 2, 5f), DitherOffset(1, 2, 3f), DitherOffset(2, 2, 1f)
    ))

    // Sierra (Sierra-3): Divisor 32, diffuses 100% of the error over 3 rows
    val sierra = DitherKernel(32f, listOf(
        DitherOffset( 1, 0, 5f), DitherOffset( 2, 0, 3f),
        DitherOffset(-2, 1, 2f), DitherOffset(-1, 1, 4f), DitherOffset(0, 1, 5f), DitherOffset(1, 1, 4f), DitherOffset(2, 1, 2f),
        DitherOffset(-1, 2, 2f), DitherOffset( 0, 2, 3f), DitherOffset(1, 2, 2f)
    ))

    // Sierra Two-Row: Divisor 16, simplified Sierra over 2 rows — balanced speed/quality
    val sierraTwoRow = DitherKernel(16f, listOf(
        DitherOffset( 1, 0, 4f), DitherOffset( 2, 0, 3f),
        DitherOffset(-2, 1, 1f), DitherOffset(-1, 1, 2f), DitherOffset(0, 1, 3f), DitherOffset(1, 1, 2f), DitherOffset(2, 1, 1f)
    ))

    // Sierra Lite: Divisor 4, minimal 3-coefficient kernel — fastest error diffusion
    val sierraLite = DitherKernel(4f, listOf(
        DitherOffset( 1, 0, 2f),
        DitherOffset(-1, 1, 1f), DitherOffset(0, 1, 1f)
    ))
}