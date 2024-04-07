package lydia.yuan.viewslisteventtracking

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.pokemon.GetPokemonsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.max

private const val STARTING_KEY = 0
class PokemonPagingSource(
    private val apolloClient: ApolloClient
) : PagingSource<Int, GetPokemonsQuery.Result>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetPokemonsQuery.Result> {

        val startKey = params.key ?: STARTING_KEY
        val range = startKey.until(startKey + params.loadSize)

        return try {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(GetPokemonsQuery(limit = params.loadSize, offset = startKey)).execute()
            }
            if (response.hasErrors()) {
                LoadResult.Error(IOException("Error fetching data"))
            } else {
                Log.d("PokemonPagingSource", "load data | start from: $startKey, data: ${response.data?.pokemons?.results}")
                val data = response.data?.pokemons?.results?.filterNotNull() ?: emptyList()
                LoadResult.Page(
                    data = data,
                    prevKey = when (startKey) {
                        STARTING_KEY -> null
                        else -> when (val prevKey = ensureValidKey(key = range.first - params.loadSize)) {
                            // We're at the start, there's nothing more to load
                            STARTING_KEY -> null
                            else -> prevKey
                        }
                    },
                    nextKey = range.last + 1
                )
            }
        } catch (e: ApolloException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GetPokemonsQuery.Result>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val pokemon = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = pokemon.id?.minus((state.config.pageSize / 2)) ?: STARTING_KEY)
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}
