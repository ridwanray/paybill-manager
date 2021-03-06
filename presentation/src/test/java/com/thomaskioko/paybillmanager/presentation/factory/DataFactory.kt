package com.thomaskioko.paybillmanager.presentation.factory

import com.thomaskioko.paybillmanager.domain.model.BillCategory
import java.util.*

object DataFactory {

    fun randomUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return Math.random().toInt()
    }

    fun randomLong(): Long {
        return Math.random().toLong()
    }

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun makeBillCategory(): BillCategory {
        return BillCategory(randomUuid(), randomUuid())
    }


}