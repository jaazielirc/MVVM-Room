package com.jaax.edsa.modelo

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DBHModulo(val context: Context?) {

    @Provides
    @Singleton
    @Named("dbHelper")
    fun providesDBHelper() = DBHelper(this.context, DBHelper.nombreDB, null, DBHelper.version)
}