package com.rayworks.resultadapter.error

/***
 * The converter to transform error string response to [ErrorMessage] subclass object.
 */
interface ErrorMessageConverter<out T : ErrorMessage> {
    fun convert(str: String): T
}