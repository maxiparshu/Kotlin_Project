import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.restoranapplication.R
import com.example.restoranapplication.RestaurantPageActivity
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.deleteRestaurant
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener


class RestaurantItemAdapter(
    context: Context,
    private val resource: Int,
    private val admin: Boolean,
    private var restaurants: List<RestaurantData>,
    private val listener: OnRestaurantSelectedListener
) : ArrayAdapter<RestaurantData>(context, resource, restaurants) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(resource, parent, false)

        val imageViewRestaurantIcon = view.findViewById<ImageView>(R.id.imageViewRestaurantIcon)
        val textViewRestaurantName = view.findViewById<TextView>(R.id.textViewRestaurantName)
        val barRestaurantRating = view.findViewById<RatingBar>(R.id.barRestaurantRating)
        val deleteButton = view.findViewById<ImageView>(R.id.imageViewDelete)
        val restaurant = restaurants[position]
        if (!admin) {
            deleteButton.visibility = View.GONE
        }
        else {
            deleteButton.visibility = View.VISIBLE
        }
        deleteButton.setOnClickListener{
            AlertDialog.Builder(context)
                .setTitle("Подтвердить удаление")
                .setMessage("Уверены что хотите удалить ресторан ${restaurant.name}?")
                .setPositiveButton("Да") { _, _ ->
                    // Удаляем ресторан из списка
                    val mutableRest = restaurants.toMutableList()
                    mutableRest.removeAt(position)
                    deleteRestaurant(restaurant.id)
                    restaurants = mutableRest
                    listener.saveRestList(restaurants)
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        textViewRestaurantName.text = restaurant.name

        barRestaurantRating.isEnabled = true
        barRestaurantRating.setIsIndicator(true)
        barRestaurantRating.rating = restaurant.rating

        Glide.with(context)
            .load(restaurant.imageURL)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(imageViewRestaurantIcon)

        view.setOnClickListener {
            listener.saveRestaurant(restaurant)
            val intent = Intent(context, RestaurantPageActivity::class.java)
            context.startActivity(intent)
        }

        return view
    }
}
