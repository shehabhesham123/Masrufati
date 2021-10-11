package com.example.masrufati

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

    }
    fun onClick(view: View){
        val title = when(view.id){
            R.id.NoteActivity_CardView_CardReader -> "Card reader"
            R.id.NoteActivity_CardView_Dispenser -> "Dispenser"
            R.id.NoteActivity_CardView_Gpru -> "GBRU"
            R.id.NoteActivity_CardView_Monitor ->"Monitor"
            R.id.NoteActivity_CardView_ReceiptPrinter -> "Receipt printer"
            else -> ".."
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.fragment_in,R.anim.fragment_out,R.anim.fragment_in,R.anim.fragment_out)
        ft.replace(R.id.fragmentContainer,ShowNotes.newInstance(title))
        ft.addToBackStack(null)
        ft.commit()
    }
}