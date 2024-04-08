package lydia.yuan.composelisteventtracking

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pokemon.GetPokemonsQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun isPokemonVisibleInLazyList(
    lazyListState: LazyListState,
    pokemon: GetPokemonsQuery.Result
): State<Boolean> {
    return derivedStateOf {
        lazyListState.layoutInfo.visibleItemsInfo.any { it.index == pokemon.id }
    }
}

@Composable
fun PokemonList() {
    val pokemonViewModel: PokemonViewModel = hiltViewModel()
    val lazyPagingItems = pokemonViewModel.pokemons.collectAsLazyPagingItems()

    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { it ->
                Log.d("LazyListStateChange", "Visible items: $it")
                it.forEach {
                    val pokemon = lazyPagingItems[it.index]
                    Log.d("LazyListStateChange", "Pokemon: $pokemon")
                }
            }
    }

    LazyColumn(
        state = lazyListState
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.id ?: 0 }
        ) { index ->
            PokemonItem(
                pokemon = lazyPagingItems[index]!!,
                isVisibleInTheLazyList = isPokemonVisibleInLazyList(
                    lazyListState,
                    lazyPagingItems[index]!!
                ).value
            )
        }
    }
}

@Composable
fun PokemonItem(pokemon: GetPokemonsQuery.Result, isVisibleInTheLazyList: Boolean) {

    var timerJob by remember { mutableStateOf<Job?>(null) }

    val pokemonViewModel: PokemonViewModel = hiltViewModel()

    // automatically cancel the timer when the item is removed from the list
    val scope = rememberCoroutineScope()

    LaunchedEffect(isVisibleInTheLazyList) {
        if (isVisibleInTheLazyList) {
            timerJob = startTimerForItem(
                scope = scope,
                pokemon = pokemon,
                isPokemonCheckedOut = pokemonViewModel::isPokemonCheckedOut,
                checkoutPokemon = pokemonViewModel::checkoutPokemon,
                logPokemonCheckout = pokemonViewModel::logPokemonCheckout
            )
        } else {
            timerJob?.cancel()
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Load image with Coil
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemon.image)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = pokemon.name ?: "",
                fontSize = 20.sp
            )
        }
    }
}

private fun startTimerForItem(
    scope: CoroutineScope,
    pokemon: GetPokemonsQuery.Result,
    isPokemonCheckedOut: (Int) -> Boolean,
    checkoutPokemon: (Int) -> Unit,
    logPokemonCheckout: (GetPokemonsQuery.Result) -> Unit
): Job {
    return scope.launch {
        delay(DELAY)
        pokemon.id?.let { id ->
            if (!isPokemonCheckedOut(id)) {
                // If the Pokemon has not been checked out before, first record the id then log the checkout
                checkoutPokemon(id)
                logPokemonCheckout(pokemon)
            }
        }
    }
}

private const val DELAY = 2000L