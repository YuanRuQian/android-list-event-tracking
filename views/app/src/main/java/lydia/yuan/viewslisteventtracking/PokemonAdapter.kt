package lydia.yuan.viewslisteventtracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pokemon.GetPokemonsQuery
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PokemonAdapter(
    private val isPokemonCheckedOut: (Int) -> Boolean,
    private val checkoutPokemon: (Int) -> Unit,
    private val logPokemonCheckout: (GetPokemonsQuery.Result) -> Unit
) : PagingDataAdapter<GetPokemonsQuery.Result, PokemonAdapter.ViewHolder>(POST_COMPARATOR) {

    private val timers: MutableMap<Int, Job?> = mutableMapOf()

    private val DELAY = 2000L

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = getItem(position)
        pokemon?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.bindingAdapterPosition
        startTimerForItem(position)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.bindingAdapterPosition
        stopTimerForItem(position)
    }

    private fun startTimerForItem(position: Int) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY)
            // Timer finished logic
            val pokemon = getItem(position)
            pokemon?.id?.let { id ->
                if (!isPokemonCheckedOut(id)) {
                    // If the Pokemon has not been checked out before, first record the id then log the checkout
                    checkoutPokemon(id)
                    logPokemonCheckout(pokemon)
                }
            }
        }
        timers[position] = job
    }

    private fun stopTimerForItem(position: Int) {
        timers[position]?.cancel()
        timers.remove(position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.pokemon_name)
        private val imageView: ImageView = view.findViewById(R.id.pokemon_image)

        fun bind(pokemon: GetPokemonsQuery.Result) {
            textView.text = pokemon.name
            Picasso.get().load(pokemon.image).into(imageView)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<GetPokemonsQuery.Result>() {
            override fun areItemsTheSame(oldItem: GetPokemonsQuery.Result, newItem: GetPokemonsQuery.Result): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GetPokemonsQuery.Result, newItem: GetPokemonsQuery.Result): Boolean =
                oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.image == newItem.image
        }
    }
}


