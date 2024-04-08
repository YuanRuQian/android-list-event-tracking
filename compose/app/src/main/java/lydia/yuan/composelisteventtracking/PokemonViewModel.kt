package lydia.yuan.composelisteventtracking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.pokemon.GetPokemonsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 10

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

    val pokemons = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { repository.pokemonPagingSource() }
    )
        .flow
        .cachedIn(viewModelScope)
}
