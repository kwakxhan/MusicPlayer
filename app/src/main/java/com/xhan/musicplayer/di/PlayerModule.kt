package com.xhan.musicplayer.di

import com.xhan.musicplayer.data.controller.MusicControllerImpl
import com.xhan.musicplayer.domain.controller.MusicController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindMusicController(
        musicControllerImpl: MusicControllerImpl
    ): MusicController
}