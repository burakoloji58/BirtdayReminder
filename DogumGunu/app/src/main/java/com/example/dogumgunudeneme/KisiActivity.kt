package com.example.dogumgunudeneme

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dogumgunudeneme.databinding.ActivityKisiBinding
import java.util.Calendar

class KisiActivity : AppCompatActivity() {
    private lateinit var ulas: ActivityKisiBinding
    private lateinit var vt: VeritabaniYardimcisi
    override fun onCreate(savedInstanceState: Bundle?) {
        ulas = ActivityKisiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(ulas.root)

        vt = VeritabaniYardimcisi(this)

        val gelenKisi = intent.getSerializableExtra("kisiNesnesi") as Kisiler

        ulas.editTextTarih.setOnClickListener {
            val calendar = Calendar.getInstance()
            val yil = calendar.get(Calendar.YEAR)
            val ay = calendar.get(Calendar.MONTH)
            val gun = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker: DatePickerDialog
            datePicker = DatePickerDialog(
                this@KisiActivity,
                DatePickerDialog.OnDateSetListener { datePicker, i, i1, i2 ->
                    ulas.editTextTarih.setText("$i2/${i1 + 1}/$i")
                }, yil, ay, gun)

            datePicker.setTitle("Tarih Seçiniz")
            datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Ayarla", datePicker)
            datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Iptal", datePicker)
            datePicker.show()
        }

        val isim = gelenKisi.kisi_ad
        val no = gelenKisi.kisi_id
        val tarih = gelenKisi.kisi_tarih
        val not = gelenKisi.kisi_not

        ulas.editTextIsim.setText(isim)
        ulas.editTextTarih.setText(tarih)
        ulas.editTextNot.setText(not)

        ulas.imageFViewSil.setOnClickListener {
            KisilerDao().kisiSil(vt,no)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        ulas.buttonKaydet.setOnClickListener {
            val Yisim =  ulas.editTextIsim.text
            val Ytarih = ulas.editTextTarih.text
            val Ynot = ulas.editTextNot.text

            KisilerDao().kisiGuncelle(vt,no,Yisim.toString(),Ytarih.toString(),Ynot.toString())

            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        ulas.buttonIptal.setOnClickListener {
            onBackPressed()
        }


    }

}