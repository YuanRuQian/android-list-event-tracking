package lydia.yuan.viewslisteventtracking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import lydia.yuan.viewslisteventtracking.databinding.FragmentPokemonListBinding
import javax.inject.Inject

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    private lateinit var pokemonAdapter: PokemonAdapter

    private val pokemonViewModel: PokemonViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pokemonAdapter = PokemonAdapter(
            dataSet = pokemonViewModel.pokemonList.value?.toMutableList() ?: mutableListOf(),
            isPokemonCheckedOut = pokemonViewModel::isPokemonCheckedOut,
            checkoutPokemon = pokemonViewModel::checkoutPokemon,
            logPokemonCheckout = pokemonViewModel::logPokemonCheckout
        )

        binding.pokemonRecyclerView.apply {
            adapter = pokemonAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launchWhenResumed {
            // uncomment this line to dynamically fetch data
            pokemonViewModel.fetchNextPagePokemonList()
            // uncomment this line to use static test data due to limitation of test API's restrictions on the number of requests per minute
            // pokemonViewModel.setPokemonList(TEST_DATA)
        }

        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("PokemonListFragment", "update adapter with new data: $it")
                pokemonAdapter.updateDataSet(it.toMutableList())
                binding.pokemonRecyclerView.apply {
                    adapter = pokemonAdapter
                    // layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
