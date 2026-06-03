package com.srizan.printy.feature.canvas

data class CanvasState(
    val elements: List<CanvasElement> = emptyList(),
    val selectedId: String? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val paperWidth: PaperWidth = PaperWidth.MM58
)

enum class PaperWidth(val px: Int, val label: String) {
    MM58(384, "58mm"),
    MM80(576, "80mm")
}
