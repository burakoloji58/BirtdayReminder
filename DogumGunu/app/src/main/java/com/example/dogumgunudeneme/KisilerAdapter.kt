package com.example.dogumgunudeneme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

class KisilerAdapter(private val mContext: Context,private val kisilerList: List<Kisiler>)
    : RecyclerView.Adapter<KisilerAdapter.cardTasarimKisilerTutucu>() , Serializable{

    inner class cardTasarimKisilerTutucu(tasarim:View) : RecyclerView.ViewHolder(tasarim){

        val textViewAd : TextView
        val textViewTarih : TextView
        val textViewNot : TextView
        val cardKisi : CardView

        init {
            textViewAd = tasarim.findViewById(R.id.textViewAd)
            textViewTarih = tasarim.findViewById(R.id.textViewTarih)
            cardKisi = tasarim.findViewById(R.id.CardKisi)
            textViewNot = tasarim.findViewById(R.id.textViewNot)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardTasarimKisilerTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.card_tasarim_kisiler,parent,false)
        return cardTasarimKisilerTutucu(tasarim)
    }

    override fun getItemCount(): Int {
        return kisilerList.size
    }

    override fun onBindViewHolder(holder: cardTasarimKisilerTutucu, position: Int) {
        val kisi = kisilerList.get(position)

        holder.textViewAd.text = kisi.kisi_ad
        holder.textViewTarih.text = kisi.kisi_tarih
        holder.textViewNot.hint = kisi.kisi_not

        holder.cardKisi.setOnClickListener {
            val intent = Intent(mContext,KisiActivity::class.java)
            intent.putExtra("kisiNesnesi",kisi)
            mContext.startActivity(intent)
        }
    }
}