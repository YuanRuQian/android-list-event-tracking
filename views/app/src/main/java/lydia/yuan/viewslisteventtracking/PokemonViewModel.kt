package lydia.yuan.viewslisteventtracking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.pokemon.GetPokemonsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {

    private val _checkedOutPokemonIds = mutableSetOf<Int>()
    private val _checkedOutPokemonIdsLiveData = MutableLiveData<Set<Int>>()

    val checkedOutPokemonIdsLiveData: LiveData<Set<Int>> = _checkedOutPokemonIdsLiveData

    fun isPokemonCheckedOut(pokemonId: Int): Boolean {
        return _checkedOutPokemonIds.contains(pokemonId)
    }

    fun logPokemonCheckout(pokemonId: Int) {
        Log.d("PokemonViewModel", "Checked out Pokémon with ID: $pokemonId, pokemon info: ${_pokemonList.value?.find { it.id == pokemonId }}")
    }

    fun checkoutPokemon(pokemonId: Int) {
        _checkedOutPokemonIds.add(pokemonId)
        _checkedOutPokemonIdsLiveData.value = _checkedOutPokemonIds
    }

    fun uncheckoutPokemon(pokemonId: Int) {
        _checkedOutPokemonIds.remove(pokemonId)
        _checkedOutPokemonIdsLiveData.value = _checkedOutPokemonIds
    }

    private val _pokemonList = MutableLiveData<List<GetPokemonsQuery.Result>>(emptyList())
    val pokemonList: LiveData<List<GetPokemonsQuery.Result>> = _pokemonList

    private var offset: Int? = 0

    fun setPokemonList(pokemonList: List<GetPokemonsQuery.Result>) {
        _pokemonList.value = pokemonList
    }

    fun fetchNextPagePokemonList() {
        if (offset == null) {
            return
        }
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apolloClient.query(GetPokemonsQuery(limit = 10, offset = offset!!)).execute()
                }
                if (response.hasErrors()) {
                    Log.e("PokemonViewModel", "Error: ${response.errors}")
                } else {
                    val newPokemonsData = response.data?.pokemons?.results?.filterNotNull()
                    _pokemonList.value = _pokemonList.value?.plus((newPokemonsData ?: emptyList()))
                    offset = response.data?.pokemons?.nextOffset
                    Log.d("PokemonViewModel", "Fetched ${newPokemonsData?.size} Pokémon")
                    Log.d("PokemonViewModel", "Total Pokémon: ${_pokemonList.value?.size}")
                    Log.d("PokemonViewModel", "Next offset: $offset")
                    Log.d("PokemonViewModel", "Pokémon: ${_pokemonList.value}")
                }
            } catch (e: ApolloException) {
                Log.e("PokemonViewModel", "Error: ${e.message}")
            }
        }
    }
}
