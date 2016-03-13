package com.marvinslullaby.weather.utils

fun String.isNumeric(): Boolean {
  return this.matches(Regex("\\d+"));
}

