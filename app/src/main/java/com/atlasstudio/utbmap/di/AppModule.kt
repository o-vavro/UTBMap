package com.atlasstudio.utbmap.di

import android.app.Application
import androidx.room.Room
import com.atlasstudio.utbmap.BuildConfig
import com.atlasstudio.utbmap.data.LocationOfficeDatabase
/*import com.atlasstudio.utbmap.net.service.ApiTalksService
import com.atlasstudio.utbmap.net.service.CoordinateTranslationService
import com.atlasstudio.utbmap.net.service.RuianService*/
import com.atlasstudio.utbmap.utils.SingleToArrayTypeAdapter
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(
        app: Application,
//        callback: OfficeDatabase.Callback
    ) = Room.databaseBuilder(app, LocationOfficeDatabase::class.java, "location_office_database")
        .fallbackToDestructiveMigration()
        //.addCallback(callback)
        .build()

    @Provides
    fun provideOfficeDao(db: LocationOfficeDatabase) = db.officeDao()

    /*@Provides
    fun provideLocationDao(db: LocationOfficeDatabase) = db.locationDao()

    @Provides
    fun provideAllDao(db: LocationOfficeDatabase) = db.allDao()*/

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope