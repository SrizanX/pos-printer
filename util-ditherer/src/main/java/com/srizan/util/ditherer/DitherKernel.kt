package com.srizan.util.ditherer

data class DitherKernel(
    val divisor: Float,
    val distribution: List<DitherOffset>
)