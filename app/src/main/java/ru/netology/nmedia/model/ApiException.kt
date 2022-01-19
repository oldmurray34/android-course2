package ru.netology.nmedia.model

import java.io.IOException

class ApiException(val error: ApiError, throwable: Throwable? = null) : IOException(throwable)