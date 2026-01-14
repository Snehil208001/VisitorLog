package com.visitor.log.di

import android.app.Application
import androidx.room.Room
import com.visitor.log.data.db.GuestDatabase
import com.visitor.log.data.network.GuestApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): GuestDatabase {
        return Room.databaseBuilder(
            app,
            GuestDatabase::class.java,
            "visitor_log_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideApiService(): GuestApiService {
        return Retrofit.Builder()
            .baseUrl("https://plannix.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GuestApiService::class.java)
    }
}