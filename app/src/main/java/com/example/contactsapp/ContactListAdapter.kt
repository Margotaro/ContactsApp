package com.example.contactsapp

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

const val ARG_TILE_VIEW = 0
const val ARG_LIST_VIEW = 1

interface OnItemClickedListener {

}
internal class ContactListAdapter internal constructor(
        val context: Context,
        private val resource: Int,
        private val itemList: List<Contact>,
        val onClickListener: OnItemClickedListener
) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    internal class ViewHolder(val listItemView: View) : RecyclerView.ViewHolder(listItemView)   {
        var name = itemView.findViewById<TextView>(R.id.listName)
        var avatar = itemView.findViewById<ImageView>(R.id.listAvatar)
        fun onItemClicked(posrition: Int, onClickListener: OnItemClickedListener) {

        }
    }

    override fun getItemCount(): Int {
        return this.itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.list_view_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contact: Contact = itemList.get(position)
        val avatar = viewHolder.avatar
        val name = viewHolder.name
        name.setText(contact.name)
        val pxIcon = context.dpToPx(48)
        Picasso.get()
            .load(contact.avatar)
            .resize(pxIcon,pxIcon).into(avatar)

        viewHolder.onItemClicked(position, onClickListener)
    }

    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

internal class ContactGridAdapter internal constructor(
    context: Context,
    private val resource: Int,
    private val itemList: List<Contact>
) : ArrayAdapter<ContactGridAdapter.ItemHolder>(context, resource) {

    internal class ItemHolder {
        var avatar: ImageView? = null
    }

    override fun getCount(): Int {
        return this.itemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val holder: ItemHolder

        convertView?.let { convertView ->
            holder = convertView.tag as ItemHolder

            val avatarValue = itemList[position].avatar
            val avatarView = holder.avatar
            Picasso.get()
                .load(avatarValue)
                .resize(48,48).into(avatarView)

            return convertView
        }

        val newConvertView = LayoutInflater.from(context).inflate(resource, null)
        holder = ItemHolder()
        holder.avatar = newConvertView.findViewById(R.id.gridAvatar)
        newConvertView.tag = holder
        return newConvertView
    }
}

class Contact(avatar: String, accountStatus: Boolean, name: String, email: String) {
    var avatar: String
    var accountStatus: Boolean
    var name: String
    var email: String

    init {
        this.accountStatus = accountStatus
        this.avatar = avatar
        this.email = email
        this.name = name
    }
}