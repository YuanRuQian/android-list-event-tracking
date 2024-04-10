package lydia.yuan.viewslisteventtracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lydia.yuan.viewslisteventtracking.databinding.FragmentLooseLoggingBinding


@AndroidEntryPoint
class LooseLoggingFragment : Fragment() {

    private val pokemonViewModel: PokemonViewModel by viewModels()

    private lateinit var binding: FragmentLooseLoggingBinding

    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLooseLoggingBinding.inflate(inflater, container, false)

        pokemonAdapter = PokemonAdapter(
            isPokemonCheckedOut = pokemonViewModel::isPokemonCheckedOut,
            checkoutPokemon = pokemonViewModel::checkoutPokemon,
            logPokemonCheckout = pokemonViewModel::logPokemonCheckout
        )

        binding.bindAdapter(pokemonAdapter = pokemonAdapter)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokemonViewModel.pokemons.collectLatest {
                    pokemonAdapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokemonAdapter.loadStateFlow.collect {
                    binding.prependProgress.isVisible = it.source.prepend is LoadState.Loading
                    binding.appendProgress.isVisible = it.source.append is LoadState.Loading
                }
            }
        }

    }
}


private fun FragmentLooseLoggingBinding.bindAdapter(pokemonAdapter: PokemonAdapter) {
    list.adapter = pokemonAdapter
    list.layoutManager = LinearLayoutManager(list.context)
    val decoration = DividerItemDecoration(list.context, DividerItemDecoration.VERTICAL)
    list.addItemDecoration(decoration)
}
