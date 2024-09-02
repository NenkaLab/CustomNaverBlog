package com.nl.customnaverblog.other

import com.nl.customnaverblog.Urls
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Client(val type: Urls.Type)