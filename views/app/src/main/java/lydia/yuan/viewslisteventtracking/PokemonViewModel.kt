package lydia.yuan.viewslisteventtracking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pokemon.GetPokemonsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 10

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val timerMapForPreciseLogging: MutableMap<Int, Job?> = mutableMapOf()

    private val DELAY = 2000L

    private val recordedIdsForPreciseLogging = mutableSetOf<Int>()
    private val recordedIds = mutableSetOf<Int>()

    fun isPokemonCheckedOut(id: Int): Boolean {
        return recordedIds.contains(id)
    }

    private fun isPokemonCheckedOutForPreciseLogging(id: Int): Boolean {
        return recordedIdsForPreciseLogging.contains(id)
    }

    fun checkoutPokemon(id: Int) {
        recordedIds.add(id)
    }

    private fun checkoutPokemonForPreciseLogging(id: Int) {
        recordedIdsForPreciseLogging.add(id)
    }

    fun logPokemonCheckout(pokemon: GetPokemonsQuery.Result) {
        Log.d("Pokemon", "Pokemon ${pokemon.name} checked out")
    }

    private fun logPokemonCheckoutForPreciseLogging(pokemon: GetPokemonsQuery.Result) {
        Log.d("Pokemon", "Pokemon ${pokemon.name} checked out for precise logging")
    }

    fun startTimerForItemForPreciseLogging(position: Int, pokemons: List<GetPokemonsQuery.Result>) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY)
            // Timer finished logic
            val pokemon = pokemons.getOrNull(position)
            pokemon?.id?.let { id ->
                if (!isPokemonCheckedOutForPreciseLogging(id)) {
                    // If the Pokemon has not been checked out before, first record the id then log the checkout
                    checkoutPokemonForPreciseLogging(id)
                    logPokemonCheckoutForPreciseLogging(pokemon)
                }
            }
        }
        timerMapForPreciseLogging[position] = job
    }

    fun stopTimerForItem(position: Int) {
        timerMapForPreciseLogging[position]?.cancel()
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
