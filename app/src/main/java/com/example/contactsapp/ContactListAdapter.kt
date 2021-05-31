package com.example.contactsapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.ContactListAdapter.ViewType.SMALL
import com.squareup.picasso.Picasso
import java.io.Serializable
import kotlinx.android.parcel.Parcelize

import android.util.Pair as Pair

const val ARG_TILE_VIEW = 0
const val ARG_LIST_VIEW = 1

interface OnItemClickedListener {
    fun onClick(viewHolder: ContactListAdapter.BindableViewHolder, contact: Contact)
}
class ContactListAdapter internal constructor(
    val context: Context,
    private val resource: Int,
    private val itemList: List<Contact>,
    private val layoutManager: GridLayoutManager? = null,
    val onClickListener: OnItemClickedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType {
        SMALL,
        DETAILED
    }

    abstract class BindableViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBindViewHolder(contact: Contact, context: Context)
        abstract fun transitionOptions(activity: Activity): ActivityOptions
    }

    class ListViewHolder(val listItemView: View) : BindableViewHolder(listItemView)   {
        var name = itemView.findViewById<TextView>(R.id.listName)
        var avatar = itemView.findViewById<ImageView>(R.id.listAvatar)
        var accountStatus = itemView.findViewById<ImageView>(R.id.listOnlineIndicator)
        override fun onBindViewHolder(contact: Contact, context: Context) {
            name.setText(contact.name)
            val iconPixelSize = (context.resources.displayMetrics.density * 48).toInt()
            Picasso.get()
                .load(contact.avatar)
                .resize(iconPixelSize, iconPixelSize).into(avatar)
            accountStatus.visibility = if (contact.accountStatus) View.VISIBLE else View.GONE
        }

        override fun transitionOptions(activity: Activity): ActivityOptions {
            name.transitionName = "nameTransition"
            avatar.transitionName = "imageTransition"

            return ActivityOptions.makeSceneTransitionAnimation(activity,
                Pair(name, "nameTransition"), Pair(avatar, "imageTransition"))
        }
    }

    class GridViewHolder(val gridItemView: View) : BindableViewHolder(gridItemView)   {
        var avatar = itemView.findViewById<ImageView>(R.id.gridAvatar)
        var accountStatus = itemView.findViewById<ImageView>(R.id.gridOnlineIndicator)
        override fun onBindViewHolder(contact: Contact, context: Context) {
            val iconPixelSize = (context.resources.displayMetrics.density * 64).toInt()
            Picasso.get()
                .load(contact.avatar)
                .resize(iconPixelSize, iconPixelSize).into(avatar)
            accountStatus.visibility = if (contact.accountStatus) View.VISIBLE else View.GONE
        }

        override fun transitionOptions(activity: Activity): ActivityOptions {
            avatar.transitionName = "imageTransition"

            return ActivityOptions.makeSceneTransitionAnimation(activity, Pair(avatar, "imageTransition"))
        }
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        if (viewType == SMALL.ordinal) {
            val contactView = inflater.inflate(R.layout.list_view_item, parent, false)
            return ListViewHolder(contactView)
        }
        val contactView = inflater.inflate(R.layout.grid_view_item, parent, false)
        return GridViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val contact: Contact = itemList.get(position)
        (viewHolder as? BindableViewHolder)?.let {
            it.onBindViewHolder(contact, context)
            it.view.setOnClickListener { v ->
                onClickListener.onClick(it, contact)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutManager?.spanCount == 1) ViewType.SMALL.ordinal
        else ViewType.DETAILED.ordinal
    }
}

@Parcelize
class Contact(var avatar: String, var accountStatus: Boolean, var name: String, var email: String): Parcelable {

}