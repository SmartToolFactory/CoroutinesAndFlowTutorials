package com.smarttoolfactory.tutorial2_1flowbasics.model

data class ActivityClassModel(val clazz: Class<*>, val description: String = clazz.name)
