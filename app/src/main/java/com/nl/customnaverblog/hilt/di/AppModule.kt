package com.nl.customnaverblog.hilt.di

import com.nl.customnaverblog.hilt.impl.ContentRepositoryImpl
import com.nl.customnaverblog.hilt.impl.FeedRepositoryImpl
import com.nl.customnaverblog.hilt.impl.UserStatusRepositoryImpl
import com.nl.customnaverblog.hilt.repo.ContentRepository
import com.nl.customnaverblog.hilt.repo.FeedRepository
import com.nl.customnaverblog.hilt.repo.UserStatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFeedRepository(
        impl: FeedRepositoryImpl
    ): FeedRepository = impl

    @Singleton
    @Provides
    fun provideUserStatusRepository(
        impl: UserStatusRepositoryImpl
    ): UserStatusRepository = impl

    @Singleton
    @Provides
    fun provideContentRepository(
        impl: ContentRepositoryImpl
    ): ContentRepository = impl

}