package lydia.yuan.viewslisteventtracking

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.pokemon.GetPokemonsQuery
import lydia.yuan.viewslisteventtracking.databinding.FragmentPokemonListBinding

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenResumed {


            val response = try {
                apolloClient(requireContext()).query(GetPokemonsQuery(limit = 10, offset = 0))
                    .execute()
            } catch (e: ApolloException) {
                return@launchWhenResumed
            }
            Log.d("PokemonListFragment", "response: ${response.data}")
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}