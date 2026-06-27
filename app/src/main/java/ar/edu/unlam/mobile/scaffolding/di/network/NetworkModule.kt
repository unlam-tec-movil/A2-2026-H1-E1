package ar.edu.unlam.mobile.scaffolding.di.network

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.DraftDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.TokenManager
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.PostApiService
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.interfaces.TuiterApiService
import ar.edu.unlam.mobile.scaffolding.data.repositories.implementation.LoginRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.implementation.PostRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.implementation.ProfileInfoRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.implementation.RegisterRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.LoginRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.PostRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.ProfileInfoRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.interfaces.RegisterRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://tuiter.fragua.com.ar/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideTuiterApiServiceInstance(retrofit: Retrofit): TuiterApiService = retrofit.create(TuiterApiService::class.java)

    @Provides
    @Singleton
    fun provideLoginRepositoryInstance(tuiterApiService: TuiterApiService): LoginRepository = LoginRepositoryImpl(tuiterApiService)

    @Provides
    @Singleton
    fun provideRegisterRepositoryInstance(tuiterApiService: TuiterApiService): RegisterRepository = RegisterRepositoryImpl(tuiterApiService)

    @Provides
    @Singleton
    fun providePostRepositoryInstance(
        tuiterApiService: TuiterApiService,
        tokenManager: TokenManager,
        draftDao: DraftDao,
    ): PostRepository = PostRepositoryImpl(tuiterApiService, tokenManager, draftDao)

    @Provides
    @Singleton
    fun provideProfileInfoRepositoryInstance(tuiterApiService: TuiterApiService): ProfileInfoRepository =
        ProfileInfoRepositoryImpl(tuiterApiService)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun providePostApiService(retrofit: Retrofit): PostApiService = retrofit.create(PostApiService::class.java)
}
