package lydia.yuan.viewslisteventtracking

import com.pokemon.GetPokemonsQuery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun providePokemons(): List<GetPokemonsQuery.Result> {
        // Provide the list of Pokémon results here
        return emptyList() // or fetch from some data source
    }
}
