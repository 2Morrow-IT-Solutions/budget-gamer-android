@file:Suppress("DEPRECATION")

package com.tomorrowit.budgetgamer.common.config.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import com.tomorrowit.budgetgamer.R
import java.io.ByteArrayOutputStream

fun Activity.navigateToNewActivity(targetActivity: Activity) {
    this.runOnUiThread {
        val intent = Intent(this, targetActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}

fun Activity.openActivityFade(targetActivity: Activity) {
    this.runOnUiThread {
        val intent = Intent(this, targetActivity::class.java)
        this.startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}

fun Activity.openActivityPush(targetActivity: Activity) {
    this.runOnUiThread {
        val intent = Intent(Intent(this, targetActivity::class.java))
        startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                R.anim.push_in_right,
                R.anim.push_out_left
            )
        }
    }
}

fun Activity.navigateToNewActivityAny(targetActivity: Activity, myMap: Map<String, Any>) {
    this.runOnUiThread {
        val intent = Intent(Intent(this, targetActivity::class.java))
        for (entry in myMap.entries) {
            val key = entry.key
            when (val value = entry.value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Bitmap -> {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    intent.putExtra(key, byteArray)
                }

                is Uri -> intent.putExtra(key, value.toString())
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}

fun Activity.openActivityFadeAny(targetActivity: Activity, myMap: Map<String, Any>) {
    this.runOnUiThread {
        val intent = Intent(Intent(this, targetActivity::class.java))
        for (entry in myMap.entries) {
            val key = entry.key
            when (val value = entry.value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Bitmap -> {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    intent.putExtra(key, byteArray)
                }

                is Uri -> intent.putExtra(key, value.toString())
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        }
        startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}

fun Activity.openActivityPushAny(targetActivity: Activity, myMap: Map<String, Any>) {
    this.runOnUiThread {
        val intent = Intent(Intent(this, targetActivity::class.java))
        for (entry in myMap.entries) {
            val key = entry.key
            when (val value = entry.value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Bitmap -> {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    intent.putExtra(key, byteArray)
                }

                is Uri -> intent.putExtra(key, value.toString())
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        }
        startActivity(intent)
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                R.anim.push_in_right,
                R.anim.push_out_left
            )
        }
    }
}

fun Activity.openActivityCloseActual(targetActivity: Activity) {
    this.runOnUiThread {
        this.finish()
        startActivity(Intent(this, targetActivity::class.java))
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                R.anim.push_in_right,
                R.anim.push_out_left
            )
        }
    }
}

fun Activity.openActivityCloseActualAny(targetActivity: Activity, myMap: Map<String, Any>) {
    this.runOnUiThread {
        val intent = Intent(Intent(this, targetActivity::class.java))
        for (entry in myMap.entries) {
            val key = entry.key
            when (val value = entry.value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Bitmap -> {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    value.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    intent.putExtra(key, byteArray)
                }

                is Uri -> intent.putExtra(key, value.toString())
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        }
        startActivity(intent)
        this.finish()
        if (Build.VERSION.SDK_INT < 34) {
            this.overridePendingTransition(
                R.anim.push_in_right,
                R.anim.push_out_left
            )
        }
    }
}

fun Activity.finishAnimation() {
    if (Build.VERSION.SDK_INT < 34) {
        this.overridePendingTransition(
            R.anim.push_in_left,
            R.anim.push_out_right
        )
    }
}

fun Activity.finishAnimationFade() {
    if (Build.VERSION.SDK_INT < 34) {
        this.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}