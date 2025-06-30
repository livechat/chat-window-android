package com.livechatinc.chatwidget.src.utils

import android.annotation.SuppressLint
import android.util.Log

@SuppressLint("LogTagMismatch")
object Logger {
    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, NONE;

        fun isLoggable(level: LogLevel): Boolean = level.ordinal <= this.ordinal

        companion object
    }

    private var logLevel: LogLevel = LogLevel.NONE
    private const val DEFAULT_TAG = "LiveChat"

    /**
     * Set the log level for the library.
     *
     * In order to see HTTP calls logs, you must set the level before [LiveChat.initialize]
     * @param level The desired log level.
     */
    @JvmStatic
    fun setLogLevel(level: LogLevel) {
        logLevel = level
    }

    @JvmStatic
    internal fun getLogLevel(): LogLevel = logLevel

    /**
     * Log a verbose message.
     */
    @JvmStatic
    fun v(message: String, tag: String = DEFAULT_TAG) {
        if (LogLevel.VERBOSE.isLoggable(logLevel)) {
            Log.v(tag, message)
        }
    }

    /**
     * Log a debug message.
     */
    @JvmStatic
    fun d(message: String, tag: String = DEFAULT_TAG) {
        if (LogLevel.DEBUG.isLoggable(logLevel)) {
            Log.d(tag, message)
        }
    }

    /**
     * Log an info message.
     */
    @JvmStatic
    fun i(message: String, tag: String = DEFAULT_TAG) {
        if (LogLevel.INFO.isLoggable(logLevel)) {
            Log.i(tag, message)
        }
    }

    /**
     * Log a warning message.
     */
    @JvmStatic
    fun w(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (LogLevel.WARN.isLoggable(logLevel)) {
            if (throwable != null) {
                Log.w(tag, message, throwable)
            } else {
                Log.w(tag, message)
            }
        }
    }

    /**
     * Log an error message.
     */
    @JvmStatic
    fun e(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (LogLevel.ERROR.isLoggable(logLevel)) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
}
