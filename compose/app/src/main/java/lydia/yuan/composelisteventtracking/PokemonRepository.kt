package lydia.yuan.composelisteventtracking

import com.apollographql.apollo3.ApolloClient

import javax.inject.Inject

class PokemonRepository @Inject constructor(private val apolloClient: ApolloClient) {
    fun pokemonPagingSource() = PokemonPagingSource(apolloClient)
}

