package com.nextlevelprogrammers.cherry1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter: RecyclerView.Adapter<ViewHolder>() {
    val contactList:ArrayList<contactInfo> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.contactview,parent,false))
    }
    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current=contactList[position]
        holder.name.text="Name-${current.name}"
        holder.number.text="Phone-${current.number}"

    }
    fun update(update:contactInfo){
        contactList.add(update)
        notifyDataSetChanged()
    }
}
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name=itemView.findViewById<TextView>(R.id.nameTextView)
    val number=itemView.findViewById<TextView>(R.id.numberTextView)
}