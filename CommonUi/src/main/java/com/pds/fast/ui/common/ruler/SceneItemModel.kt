package com.pds.fast.ui.common.ruler

import java.util.*

data class SceneItemModel(var id: String = "", var title: String = "", var background: String = "") {
    override fun equals(other: Any?) = other is SceneItemModel && id == other.id
    override fun hashCode() = Objects.hash(id)
}