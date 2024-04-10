package lydia.yuan.viewslisteventtracking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import lydia.yuan.viewslisteventtracking.databinding.FragmentPreciseLoggingBinding

@AndroidEntryPoint
class PreciseLoggingFragment : Fragment() {

    private val pokemonViewModel: PokemonViewModel by viewModels()

    private lateinit var binding: FragmentPreciseLoggingBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreciseLoggingBinding.inflate(inflater, container, false)
        val pokemonAdapter = PokemonStaticAdpater()

        layoutManager = LinearLayoutManager(requireContext())
        binding.list.layoutManager = layoutManager
        binding.list.adapter = pokemonAdapter
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        recyclerView = binding.list
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (firstVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                    // Start timer for visible items
                    for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                        pokemonViewModel.startTimerForItemForPreciseLogging(i, TEST_DATA)
                    }

                    // Log the names of the first and last visible items
                    val firstVisibleItemName = TEST_DATA[firstVisibleItemPosition].name
                    val lastVisibleItemName = TEST_DATA[lastVisibleItemPosition].name
                    Log.d("PreciseLoggingFragment", "First visible item: $firstVisibleItemName")
                    Log.d("PreciseLoggingFragment", "Last visible item: $lastVisibleItemName")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Stop timer for non-visible items when scrolling stops
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for (i in 0 until pokemonAdapter.itemCount) {
                        if (i < firstVisibleItemPosition || i > lastVisibleItemPosition) {
                            pokemonViewModel.stopTimerForItem(i)
                        }
                    }
                }
            }
        })


        pokemonAdapter.submitList(TEST_DATA)

        return binding.root
    }
}
