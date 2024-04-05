package lydia.yuan.viewslisteventtracking

import androidx.lifecycle.MutableLiveData
import com.pokemon.GetPokemonsQuery
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun providePokemons(): MutableLiveData<MutableList<GetPokemonsQuery.Result>> {
        // Provide the list of Pokémon results here
        return MutableLiveData()
    }
}
