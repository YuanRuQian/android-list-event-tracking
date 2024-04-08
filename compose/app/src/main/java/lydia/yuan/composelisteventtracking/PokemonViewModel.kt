package lydia.yuan.composelisteventtracking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pokemon.GetPokemonsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val recordedIds = mutableSetOf<Int>()

    fun isPokemonCheckedOut(id: Int): Boolean {
        return recordedIds.contains(id)
    }

    fun checkoutPokemon(id: Int) {
        recordedIds.add(id)
    }

    fun logPokemonCheckout(pokemon: GetPokemonsQuery.Result) {
        Log.d("Pokemon", "Pokemon ${pokemon.name} checked out")
    }

    val pokemons: Flow<PagingData<GetPokemonsQuery.Result>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { repository.pokemonPagingSource() }
    )
        .flow
        // cachedIn allows paging to remain active in the viewModel scope, so even if the UI
        // showing the paged data goes through lifecycle changes, pagination remains cached and
        // the UI does not have to start paging from the beginning when it resumes.
        .cachedIn(viewModelScope)
}
