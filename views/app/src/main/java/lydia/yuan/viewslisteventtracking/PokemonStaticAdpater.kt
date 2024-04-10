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

class PokemonStaticAdpater : RecyclerView.Adapter<PokemonStaticAdpater.ViewHolder>() {

    private var pokemons: List<GetPokemonsQuery.Result> = listOf()

    override fun getItemCount(): Int {
        return pokemons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.bind(pokemon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_item_layout, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.pokemon_name)
        private val imageView: ImageView = view.findViewById(R.id.pokemon_image)

        fun bind(pokemon: GetPokemonsQuery.Result) {
            textView.text = pokemon.name
            Picasso.get().load(pokemon.image).into(imageView)
        }
    }

    fun submitList(newPokemonList: List<GetPokemonsQuery.Result>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = pokemons.size
            override fun getNewListSize(): Int = newPokemonList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldPokemon = pokemons[oldItemPosition]
                val newPokemon = newPokemonList[newItemPosition]
                return oldPokemon.id == newPokemon.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldPokemon = pokemons[oldItemPosition]
                val newPokemon = newPokemonList[newItemPosition]
                return oldPokemon.id == newPokemon.id && oldPokemon.name == newPokemon.name && oldPokemon.image == newPokemon.image
            }
        })
        pokemons = newPokemonList
        diffResult.dispatchUpdatesTo(this)
    }
}
