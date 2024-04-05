package lydia.yuan.viewslisteventtracking

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(Level.BODY) // Change the log level as needed

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return ApolloClient.Builder()
            .serverUrl("https://graphql-pokeapi.vercel.app/api/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}
