package com.rayworks.resultadapter.error

/***
 * The base class indicates the specific error info received from backend.
 * Its subclass should be defined according to your requirement. And a converter
 * [ErrorMessageConverter] used to map String to your subclass object also needs to be implemented.
 */
open class ErrorMessage()