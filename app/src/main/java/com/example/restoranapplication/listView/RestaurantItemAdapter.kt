import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.restoranapplication.R
import com.example.restoranapplication.RestaurantListActivity
import com.example.restoranapplication.RestaurantPageActivity
import com.example.restoranapplication.data.MenuItem
import com.example.restoranapplication.data.RestaurantData
import com.example.restoranapplication.data.deleteRestaurant
import com.example.restoranapplication.data.`interface`.OnRestaurantSelectedListener
import com.example.restoranapplication.data.updateRestaurant


class RestaurantItemAdapter(
    context: Context,
    private val resource: Int,
    private val admin: Boolean,
    private var restaurants: List<RestaurantData>,
    private val listener: OnRestaurantSelectedListener,
) : ArrayAdapter<RestaurantData>(context, resource, restaurants) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(resource, parent, false)

        val imageViewRestaurantIcon = view.findViewById<ImageView>(R.id.imageViewRestaurantIcon)
        val textViewRestaurantName = view.findViewById<TextView>(R.id.textViewRestaurantName)
        val textViewRestaurantAddress = view.findViewById<TextView>(R.id.textViewRestaurantAddress)
        val barRestaurantRating = view.findViewById<RatingBar>(R.id.barRestaurantRating)
        val deleteButton = view.findViewById<ImageView>(R.id.imageViewDelete)
        val restaurant = restaurants[position]
        deleteButton.visibility = if (admin) View.VISIBLE else View.GONE
        deleteButton.setOnClickListener {
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
                    val intent = Intent(context, RestaurantListActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        textViewRestaurantName.text = restaurant.name
        textViewRestaurantAddress.text = restaurant.address

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
                .putExtra("calling_activity", RestaurantListActivity::class.java.name)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }
        view.setOnLongClickListener {
            if (admin) {
                showEditRestaurantDialog(restaurant, position)
            }
            true
        }
        return view
    }

    private fun showEditRestaurantDialog(restaurant: RestaurantData, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_restaurant, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextRestaurantName)
        val editTextMenuItems = dialogView.findViewById<EditText>(R.id.editTextMenuItems)
        editTextMenuItems.visibility = View.GONE
        val editTextImageUrl = dialogView.findViewById<EditText>(R.id.editTextImageUrl)
        val editAddress = dialogView.findViewById<EditText>(R.id.editAddress)

        // Заполнение полей текущими данными ресторана
        editTextName.setText(restaurant.name)
        editTextImageUrl.setText(restaurant.imageURL)
        editAddress.setText(restaurant.address)

        AlertDialog.Builder(context)
            .setTitle("Редактировать ресторан")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = editTextName.text.toString()
                val imageUrl = editTextImageUrl.text.toString()
                val addressText = editAddress.text.toString()

                if (name.isNotEmpty() && imageUrl.isNotEmpty() && addressText.isNotEmpty()) {
                    try {
                        restaurant.name = name
                        restaurant.imageURL = imageUrl
                        restaurant.address = addressText

                        updateRestaurant(restaurant.id, restaurant)

                        val mutableRest = restaurants.toMutableList()
                        mutableRest[position] = restaurant
                        restaurants = mutableRest.toList()
                        listener.saveRestList(restaurants)
                        val intent = Intent(context, RestaurantListActivity::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()

                        Toast.makeText(
                            context,
                            "Ресторан обновлен: ${restaurant.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Ошибка при вводе данных. Проверьте формат.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }
}
