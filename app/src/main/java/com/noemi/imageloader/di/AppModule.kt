package com.noemi.imageloader.di

import com.noemi.imagecache.ImageDiskCache
import com.noemi.imagecache.ImageMemoryCache
import com.noemi.imageloader.network.ZipoAPI
import com.noemi.imageloader.remotedatasource.ZipoImageDataSource
import com.noemi.imageloader.remotedatasource.ZipoImageDataSourceImpl
import com.noemi.imageloader.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun providesOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(interceptor).build()


    @Provides
    @Singleton
    fun providesZipoAPI(client: OkHttpClient): ZipoAPI =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(ZipoAPI::class.java)

    @Singleton
    @Provides
    fun providesDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providesImageDataSource(zipoAPI: ZipoAPI, dispatcher: CoroutineDispatcher): ZipoImageDataSource = ZipoImageDataSourceImpl(zipoAPI, dispatcher)


    @Provides
    @Singleton
    fun providesImageCache(): ImageMemoryCache = ImageMemoryCache.getInstance()


    @Provides
    @Singleton
    fun providesDiskCache(): ImageDiskCache = ImageDiskCache.getInstance()
}