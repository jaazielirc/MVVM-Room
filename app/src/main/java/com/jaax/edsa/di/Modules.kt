package com.jaax.edsa.di

import android.content.Context
import com.jaax.edsa.data.RoomRepository
import com.jaax.edsa.data.db.DatabaseEDSA
import com.jaax.edsa.data.viewmodel.AccountViewModelFactory
import com.jaax.edsa.data.viewmodel.EmailViewModelFactory
import com.jaax.edsa.data.viewmodel.UserViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) = DatabaseEDSA.invoke(context)

    @Singleton
    @Provides
    fun provideUserDao(db: DatabaseEDSA) = db.userDao()

    @Singleton
    @Provides
    fun provideEmailDao(db: DatabaseEDSA) = db.emailDao()

    @Singleton
    @Provides
    fun provideAccountDao(db: DatabaseEDSA) = db.accountDao()

    @Singleton
    @Provides
    fun provideUsername(db: DatabaseEDSA) = runBlocking { db.userDao().getUser()?.name ?: "" }
}

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Singleton
    @Provides
    fun provideUserViewModelFactory(repository: RoomRepository) = UserViewModelFactory(repository)

    @Singleton
    @Provides
    fun provideEmailViewModelFactory(repository: RoomRepository, username: String) =
        EmailViewModelFactory(repository, username)
}