package com.atlasstudio.utbmap.di

import android.app.Application
import androidx.room.Room
import com.atlasstudio.utbmap.BuildConfig
import com.atlasstudio.utbmap.data.LocationOfficeDatabase
import com.atlasstudio.utbmap.net.service.ApiTalksService
import com.atlasstudio.utbmap.net.service.CoordinateTranslationService
import com.atlasstudio.utbmap.net.service.RuianService
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
    private val apiTalksKey: String = "O6BwsGTZzTaekkB2gm6wX9f6zZlYLr6c5wuIutaB"

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

    @Provides
    fun provideLocationDao(db: LocationOfficeDatabase) = db.locationDao()

    @Provides
    fun provideAllDao(db: LocationOfficeDatabase) = db.allDao()

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("x-api-key", apiTalksKey)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    } else {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("x-api-key", apiTalksKey)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
    }

    @Singleton
    @Provides
    @Named("CoordinateTranslationRetrofit")
    fun provideCoordinateTranslationRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://geoportal.cuzk.cz/")
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideCoordinateTranslationService(@Named("CoordinateTranslationRetrofit") coordinateTranslationRetrofit: Retrofit) = coordinateTranslationRetrofit.create(CoordinateTranslationService::class.java)

    @Singleton
    @Provides
    @Named("RuianRetrofit")
    fun provideRuianRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://ags.cuzk.cz/arcgis/rest/services/RUIAN/Vyhledavaci_sluzba_nad_daty_RUIAN/MapServer/exts/GeocodeSOE/tables/1/")
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRuianService(@Named("RuianRetrofit") ruianRetrofit: Retrofit) = ruianRetrofit.create(RuianService::class.java)

    @Singleton
    @Provides
    @Named("ApiTalksRetrofit")
    fun provideApiTalksRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().registerTypeAdapterFactory(
                SingleToArrayTypeAdapter.FACTORY).create()))
        .baseUrl("https://api.apitalks.store/apitalks.com/")
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiTalksService(@Named("ApiTalksRetrofit") apiTalksRetrofit: Retrofit) = apiTalksRetrofit.create(ApiTalksService::class.java)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope