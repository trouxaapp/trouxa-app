package com.trouxaapp.app.di

import android.content.Context
import androidx.room.Room
import com.trouxaapp.app.data.local.AnalyzedLeiDao
import com.trouxaapp.app.data.local.TrouxaDatabase
import com.trouxaapp.app.data.remote.CamaraApiService
import com.trouxaapp.app.data.repository.TrouxaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTrouxaRepository(): TrouxaRepository = TrouxaRepository()

    @Provides
    @Singleton
    fun provideCamaraApi(): CamaraApiService = Retrofit.Builder()
        .baseUrl("https://dadosabertos.camara.leg.br/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CamaraApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrouxaDatabase =
        Room.databaseBuilder(context, TrouxaDatabase::class.java, "trouxa_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideAnalyzedLeiDao(db: TrouxaDatabase): AnalyzedLeiDao = db.analyzedLeiDao()
}
