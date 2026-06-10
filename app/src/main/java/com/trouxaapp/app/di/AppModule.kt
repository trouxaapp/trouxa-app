package com.trouxaapp.app.di

import android.content.Context
import androidx.room.Room
import com.trouxaapp.app.data.local.DeputadoDao
import com.trouxaapp.app.data.local.DespesaDao
import com.trouxaapp.app.data.local.FeedDao
import com.trouxaapp.app.data.local.IndicadorDao
import com.trouxaapp.app.data.local.LeiDao
import com.trouxaapp.app.data.local.SyncLogDao
import com.trouxaapp.app.data.local.TrouxaDatabase
import com.trouxaapp.app.data.remote.BcbApiService
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.util.UserPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrouxaDatabase =
        Room.databaseBuilder(context, TrouxaDatabase::class.java, "trouxa_db")
            // All tables are re-syncable caches, so a destructive migration
            // costs one re-sync, not user data.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLeiDao(db: TrouxaDatabase): LeiDao = db.leiDao()

    @Provides
    fun provideIndicadorDao(db: TrouxaDatabase): IndicadorDao = db.indicadorDao()

    @Provides
    fun provideDeputadoDao(db: TrouxaDatabase): DeputadoDao = db.deputadoDao()

    @Provides
    fun provideDespesaDao(db: TrouxaDatabase): DespesaDao = db.despesaDao()

    @Provides
    fun provideFeedDao(db: TrouxaDatabase): FeedDao = db.feedDao()

    @Provides
    fun provideSyncLogDao(db: TrouxaDatabase): SyncLogDao = db.syncLogDao()

    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): UserPrefs = UserPrefs(context)

    @Provides
    @Singleton
    fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheSize = 10L * 1024 * 1024
        return OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideCamaraApi(client: OkHttpClient): CamaraApiService = Retrofit.Builder()
        .baseUrl("https://dadosabertos.camara.leg.br/api/v2/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CamaraApiService::class.java)

    @Provides
    @Singleton
    fun provideBcbApi(client: OkHttpClient): BcbApiService = Retrofit.Builder()
        .baseUrl(BcbApiService.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BcbApiService::class.java)
}
