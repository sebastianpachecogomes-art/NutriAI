package com.example.nutriai

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class NinoAdapter(private val ninos: MutableList<Nino>) :
    RecyclerView.Adapter<NinoAdapter.NinoViewHolder>() {

    class NinoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.child_avatar)
        val nombre: TextView = view.findViewById(R.id.child_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NinoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nino, parent, false)
        return NinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NinoViewHolder, position: Int) {
        val nino = ninos[position]
        holder.nombre.text = nino.nombre

        val file = nino.fotoPath?.let { File(it) }
        if (file != null && file.exists()) {
            holder.avatar.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.avatar.setPadding(0, 0, 0, 0)
            holder.avatar.setImageURI(Uri.fromFile(file))
        } else {
            holder.avatar.scaleType = ImageView.ScaleType.CENTER_INSIDE
            val pad = (8 * holder.itemView.resources.displayMetrics.density).toInt()
            holder.avatar.setPadding(pad, pad, pad, pad)
            holder.avatar.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    override fun getItemCount(): Int = ninos.size

    // Reemplaza la lista completa: la usan tanto el buscador como la recarga al volver a la pantalla
    fun actualizarLista(nuevaLista: List<Nino>) {
        ninos.clear()
        ninos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
