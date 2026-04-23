//package com.example.localeats.ui
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.localeats.R
//import com.example.localeats.model.PhotoMeta
//
//class PhotoDetailActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_photo_detail)
//
//        val photo = intent.getParcelableExtra<PhotoMeta>("photo")!!
//
//        val fragment = PhotoDetailFragment.newInstance(photo)
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, fragment)
//            .commit()
//    }
//}