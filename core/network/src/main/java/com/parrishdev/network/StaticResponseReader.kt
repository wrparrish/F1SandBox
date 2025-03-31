package com.parrishdev.network

import android.content.Context
import com.parrishdev.network.responses.Driver
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.BufferedReader
import java.io.InputStreamReader

object StaticResponseReader
{
    fun getDrivers(context: Context, resourceId: Int): List<Driver> {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.use { it.readText() }

        val moshi = Moshi.Builder().build()

        val listType = Types.newParameterizedType(List::class.java, Driver::class.java)
        val adapter: JsonAdapter<List<Driver>> = moshi.adapter(listType)

        return adapter.fromJson(jsonString) ?: emptyList()
    }
}