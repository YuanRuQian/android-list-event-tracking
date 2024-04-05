package lydia.yuan.viewslisteventtracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pokemon.GetPokemonsQuery
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PokemonAdapter(private val dataSet: MutableList<GetPokemonsQuery.Result>, private val isPokemonCheckedOut: (Int) -> Boolean, private val checkoutPokemon: (Int) -> Unit, private val logPokemonCheckout: (Int) -> Unit) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    private val timers: MutableMap<Int, Job?> = mutableMapOf()

    private val DELAY = 2000L

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.adapterPosition
        startTimerForItem(position)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.adapterPosition
        stopTimerForItem(position)
    }

    private fun startTimerForItem(position: Int) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            delay(DELAY)
            // Timer finished logic
            val pokemonId = dataSet[position]?.id
            pokemonId?.let { id ->
                if (!isPokemonCheckedOut(id)) {
                    // If the Pokemon has not been checked out before, first record the id then log the checkout
                    checkoutPokemon(id)
                    logPokemonCheckout(id)
                }
            }
        }
        timers[position] = job
    }

    private fun stopTimerForItem(position: Int) {
        timers[position]?.cancel()
        timers.remove(position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.pokemon_name)
        val imageView: ImageView = view.findViewById(R.id.pokemon_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position]?.name
        Picasso.get().load(dataSet[position]?.image).into(holder.imageView)
    }

    override fun getItemCount(): Int = dataSet.size

    // Method to update the dataset
    fun updateDataSet(newDataSet: List<GetPokemonsQuery.Result>) {
        val oldSize = dataSet.size
        val newSize = newDataSet.size

        // Calculate the differences between old and new datasets
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldSize
            override fun getNewListSize(): Int = newSize

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return dataSet.getOrNull(oldItemPosition)?.id ==
                        newDataSet.getOrNull(newItemPosition)?.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return dataSet.getOrNull(oldItemPosition) == newDataSet.getOrNull(newItemPosition)
            }
        })

        // Update the dataset with the new data
        dataSet.clear()
        dataSet.addAll(newDataSet)

        // Dispatch the specific change events to the adapter
        diffResult.dispatchUpdatesTo(this)
    }


}
