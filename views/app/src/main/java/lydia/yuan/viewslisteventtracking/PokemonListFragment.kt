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
import dagger.hilt.android.AndroidEntryPoint
import lydia.yuan.viewslisteventtracking.databinding.FragmentPokemonListBinding
import javax.inject.Inject

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var pokemonAdapter: PokemonAdapter

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

        binding.pokemonRecyclerView.apply {
            adapter = pokemonAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Fetch Pokémon data
        lifecycleScope.launchWhenResumed {
            pokemonViewModel.fetchNextPagePokemonList()
        }

        pokemonViewModel.pokemonList.observe(viewLifecycleOwner) {
            it?.let {
                Log.d("PokemonListFragment", "update adapter with new data: $it")
                pokemonAdapter = PokemonAdapter(it)
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
