package lydia.yuan.viewslisteventtracking

import com.apollographql.apollo3.ApolloClient

import javax.inject.Inject

class PokemonRepository @Inject constructor(private val apolloClient: ApolloClient) {
    fun pokemonPagingSource() = PokemonPagingSource(apolloClient)
}
