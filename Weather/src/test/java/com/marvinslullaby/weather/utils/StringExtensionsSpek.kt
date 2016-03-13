package com.marvinslullaby.weather.utils

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.shouldEqual


class StringExtensionsSpek : Spek() {init {
  listOf(
    Pair("567", true),
    Pair("5", true),
    Pair("567905", true),
    Pair("some non numeric string", false),
    Pair("567a", false),
    Pair("v567", false),
    Pair("56-7", false),
    Pair("{567", false)
  ).forEach {
    val str = it.first
    val isNumeric = it.second
    given("$str") {
      on("checking if it is numeric") {
        val result = str.isNumeric()
        it("should return $isNumeric") {
          shouldEqual(isNumeric, result)
        }
      }
    }
  }
}}