package com.example.dogumgunudeneme


import android.app.DatePickerDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogumgunudeneme.databinding.ActivityMainBinding
import java.time.LocalDate
import java.util.Calendar


class MainActivity : AppCompatActivity() , SearchView.OnQueryTextListener{
    private lateinit var ulas: ActivityMainBinding
    private lateinit var kisilerListe: ArrayList<Kisiler>
    private lateinit var adapter: KisilerAdapter
    private lateinit var vt: VeritabaniYardimcisi
    override fun onCreate(savedInstanceState: Bundle?) {
        ulas = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(ulas.root)

        vt = VeritabaniYardimcisi(this)

        ulas.toolbar.title = "Doğum Günü Hatırlatıcı"
        setSupportActionBar(ulas.toolbar)

        ulas.rv.setHasFixedSize(true)
        ulas.rv.layoutManager = LinearLayoutManager(this)

        TumKisi()
        bildirimOlustur()
        ulas.fabButton.setOnClickListener {

            fabTikla()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_menu, menu)

            val item = menu?.findItem(R.id.action_ara)

            val searchView = item?.actionView as SearchView
            searchView.setOnQueryTextListener(this)

            return return super.onCreateOptionsMenu(menu)
        }

    override fun onQueryTextSubmit(query: String): Boolean {
        aramaYap(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        aramaYap(newText)
        return true
    }

    fun fabTikla(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_tasarim, null)

        mDialogView.findViewById<EditText>(R.id.editTextTarihF).setOnClickListener{
                val calendar = Calendar.getInstance()
                val yil= calendar.get(Calendar.YEAR)
                val ay= calendar.get(Calendar.MONTH)
                val gun= calendar.get(Calendar.DAY_OF_MONTH)
                val datePicker: DatePickerDialog
                datePicker = DatePickerDialog(this@MainActivity, DatePickerDialog.OnDateSetListener { datePicker, i, i1, i2 ->

                    mDialogView.findViewById<EditText>(R.id.editTextTarihF).setText( "$i2/${i1+1}/$i")
                                                                                                    },yil, ay, gun)
                datePicker.setTitle("Tarih Seçiniz")
                datePicker.setButton(DialogInterface.BUTTON_POSITIVE,"Ayarla", datePicker)
                datePicker.setButton(DialogInterface.BUTTON_NEGATIVE,"Iptal", datePicker)
                datePicker.show()
            }
        val textView = TextView(this)
        textView.text = "Kişi Ekle"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setTextColor(Color.BLACK)

        val isim = mDialogView.findViewById<EditText>(R.id.editTextIsimF)
        val tarih = mDialogView.findViewById<EditText>(R.id.editTextTarihF)
        val not = mDialogView.findViewById<EditText>(R.id.editTextNotF)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setCustomTitle(textView)
            .setPositiveButton("Kaydet"){
                    dialogInterface, i ->

                KisilerDao().kisiEkle(vt,isim.text.toString().trim(),tarih.text.toString().trim(),not.text.toString().trim())

                TumKisi()

            }.setNegativeButton("ÇIK"){
                    dialogInterface, i ->

            }.show()
        mBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        mBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        mBuilder.getWindow()?.setBackgroundDrawableResource(R.color.gri)
        mBuilder.show()
    }

    fun TumKisi(){
        kisilerListe = KisilerDao().tumKisileriGetir(vt)

        adapter = KisilerAdapter(this, kisilerListe)

        ulas.rv.adapter = adapter
    }

    fun aramaYap(aramaKelime: String){

        kisilerListe =  KisilerDao().kisiAra(vt,aramaKelime)

        adapter = KisilerAdapter(this, kisilerListe)

        ulas.rv.adapter = adapter

    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun bildirimOlustur(){

        var builder:NotificationCompat.Builder

        val bildirimYoneticisi = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this,this@MainActivity::class.java)

        val gidilecekIntent = PendingIntent.getActivity(applicationContext,1,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val todayN = LocalDate.now()

        // Bugünün ay ve gün bilgilerini al
        val monthN = todayN.monthValue // Ay, 1'den başlar
        val dayN = todayN.dayOfMonth+1

        for (person in kisilerListe) {
            val personName = person.kisi_ad
            val birthday = person.kisi_tarih

            if (birthday != null) {

                val parts = birthday.split("/")
                val day = parts[0].toInt()
                val month = parts[1].toInt()
                // Doğum gününden 1 gün önce bildirim gönder
                if (isTomorrowSameMonthAndDay(month,day,monthN,dayN)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        val kanalId = "kanalId"
                        val kanalAd = "kanalAd"
                        val kanalTanitim = "kanalTanitim"
                        val kanalOnceligi = NotificationManager.IMPORTANCE_HIGH

                        var kanal : NotificationChannel? = bildirimYoneticisi.getNotificationChannel(kanalId)

                        if(kanal == null){
                            kanal = NotificationChannel(kanalId,kanalAd,kanalOnceligi)
                            kanal.description = kanalTanitim
                            bildirimYoneticisi.createNotificationChannel(kanal)
                        }

                        builder = NotificationCompat.Builder(this,kanalId)

                        builder.setContentTitle("Doğum Gününü Kaçırma!")
                            .setContentText("${personName} kişisinin yarın Doğum günü!!")
                            .setSmallIcon(R.mipmap.bildirim_resim)
                            .setAutoCancel(true)
                            .setContentIntent(gidilecekIntent)


                    }else{
                        builder = NotificationCompat.Builder(this)

                        builder.setContentTitle("Doğum Gününü Kaçırma!")
                            .setContentText("${personName} kişisinin yarın Doğum günü!!")
                            .setSmallIcon(R.mipmap.bildirim_resim)
                            .setContentIntent(gidilecekIntent)
                            .setAutoCancel(true).priority = Notification.PRIORITY_HIGH
                    }
                    bildirimYoneticisi.notify(1,builder.build())
                }
            }
        }
    }

    private fun isTomorrowSameMonthAndDay(birthdayM: Int,birthdayD: Int, todayM: Int, todayD: Int): Boolean {
        if(birthdayM==todayM && birthdayD==todayD-1){
            return true
        }else
            return false
    }

}