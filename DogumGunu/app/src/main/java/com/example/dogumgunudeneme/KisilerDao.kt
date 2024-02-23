package com.example.dogumgunudeneme

import android.content.ContentValues

class KisilerDao {

    fun kisiEkle(vt : VeritabaniYardimcisi, kisi_ad:String,kisi_tarih:String,kisi_not:String){

        val db = vt.writableDatabase
        val values = ContentValues()

        values.put("kisi_ad",kisi_ad)
        values.put("kisi_tarih",kisi_tarih)
        values.put("kisi_not",kisi_not)

        db.insertOrThrow("kisiler",null,values)
        db.close()
    }

    fun kisiGuncelle(vt : VeritabaniYardimcisi,kisi_id:Int, kisi_ad:String,kisi_tarih:String,kisi_not:String){

        val db = vt.writableDatabase
        val values = ContentValues()

        values.put("kisi_ad",kisi_ad)
        values.put("kisi_tarih",kisi_tarih)
        values.put("kisi_not",kisi_not)

        db.update("kisiler",values,"kisi_id=?", arrayOf(kisi_id.toString()))
        db.close()
    }

    fun kisiSil(vt : VeritabaniYardimcisi,kisi_id:Int){
        val db = vt.writableDatabase
        db.delete("kisiler","kisi_id=?", arrayOf(kisi_id.toString()))
        db.close()
    }

    fun tumKisileriGetir(vt : VeritabaniYardimcisi) : ArrayList<Kisiler>{

        val kisilerArrayList = ArrayList<Kisiler>()
        val db = vt.writableDatabase
        val c = db.rawQuery("SELECT * FROM kisiler",null)

        while (c.moveToNext()){
            val kisi = Kisiler(c.getInt(c.getColumnIndex("kisi_id")),
                c.getString(c.getColumnIndex("kisi_ad")),
                c.getString(c.getColumnIndex("kisi_tarih")),
                c.getString(c.getColumnIndex("kisi_not")))

            kisilerArrayList.add(kisi)
        }
        return  kisilerArrayList
    }

    fun kisiAra(vt : VeritabaniYardimcisi , keyWord:String): ArrayList<Kisiler>{
        val db = vt.writableDatabase
        val kisilerArrayList = ArrayList<Kisiler>()
        val c = db.rawQuery("SELECT * FROM kisiler WHERE kisi_ad like '%$keyWord%'",null)

        while (c.moveToNext()){
            val kisi = Kisiler(c.getInt(c.getColumnIndex("kisi_id")),
                c.getString(c.getColumnIndex("kisi_ad")),
                c.getString(c.getColumnIndex("kisi_tarih")),
                c.getString(c.getColumnIndex("kisi_not")))

            kisilerArrayList.add(kisi)
        }
        return  kisilerArrayList
    }




}