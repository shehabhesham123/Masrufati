package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.masrufati.Note.Companion.toColor
import com.example.masrufati.Note.Companion.toInt
import kotlinx.android.synthetic.main.fragment_note.*
import java.lang.Exception
import kotlin.math.log

private const val NOTE_ID = "ID"
private const val NOTE_TITLE = "TITLE"
private const val NOTE_BODY = "BODY"
private const val NOTE_BACKGROUND = "BACKGROUND"

class NoteFragment : DialogFragment() {

    private lateinit var listener:NoteListener
    private var note:Note? = null
    private lateinit var journey: Journey
    private var background = Note.Color.WHITE

    interface NoteListener{
        fun onNoteDoneClick(note: Note,journey: Journey)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is NoteListener) listener = context
        else throw Exception("You don't implements from NoteListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            journey = BottomSheet.toJourney(it)
            val id = it.getInt(NOTE_ID,-1)
            val title = it.getString(NOTE_TITLE)
            val body = it.getString(NOTE_BODY)
            val background = it.getInt(NOTE_BACKGROUND)
            this.background = toColor(background)
            if(id != -1) note = Note(id,title,body,background)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(background){
            Note.Color.BLUE -> back.background = resources.getDrawable(android.R.color.holo_blue_bright)
            Note.Color.GREY -> back.background = resources.getDrawable(android.R.color.darker_gray)
            Note.Color.PURPLE -> back.background = resources.getDrawable(android.R.color.holo_purple)
        }

        NoteFragment_CardView_blue.setOnClickListener {
            back.background = resources.getDrawable(android.R.color.holo_blue_bright)
            background = Note.Color.BLUE
        }
        NoteFragment_CardView_gray.setOnClickListener {
            back.background = resources.getDrawable(android.R.color.darker_gray)
            background = Note.Color.GREY
        }
        NoteFragment_CardView_purple.setOnClickListener {
            back.background = resources.getDrawable(android.R.color.holo_purple)
            background = Note.Color.PURPLE
        }
        NoteFragment_CardView_white.setOnClickListener {
            back.background = resources.getDrawable(android.R.color.white)
            background = Note.Color.WHITE
        }

        NoteFragment_TextInputEditText_Title.setText(note?.getTitle())
        NoteFragment_TextInputEditText_Body.setText(note?.getBody())
        NoteFragment_TextView_NumOfLetters.text = "${NoteFragment_TextInputEditText_Body.text.toString().length}/1000"

        NoteFragment_TextInputEditText_Body.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                NoteFragment_TextView_NumOfLetters.text = "${p0?.length}/1000"
            }
        })

        NoteFragment_ImageView_Done.setOnClickListener {
            val title = NoteFragment_TextInputEditText_Title.text.toString()
            val body = NoteFragment_TextInputEditText_Body.text.toString()

            val note = Note(title,body,background)
            this.note?.let {
                note.setId(it.getId())
            }
            listener.onNoteDoneClick(note,journey)
            dismiss()
        }
    }

    companion object{
        fun newInstance(note:Note?,journey: Journey):NoteFragment {
            val args = BottomSheet.toBundle(journey)
            val fragment = NoteFragment()
            note?.let {
                args.putInt(NOTE_ID,note.getId())
                args.putString(NOTE_TITLE,note.getTitle())
                args.putString(NOTE_BODY,note.getBody())
                args.putInt(NOTE_BACKGROUND,toInt(note.getBackground()))
            }
            fragment.arguments = args
            return fragment
        }
    }

}