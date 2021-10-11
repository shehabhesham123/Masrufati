package com.example.masrufati

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_show_notes.*

class ShowNotes : Fragment() {

    private lateinit var accessDatabase: AccessDatabase
    private var listOfNote:MutableList<Note> = mutableListOf()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        accessDatabase = AccessDatabase.newInstance(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val title = it.getString("TITLE","11")
            listOfNote = accessDatabase.getNotes(title)
            when (title) {
                "Monitor" -> {
                    listOfNote.addAll(accessDatabase.getNotes("Touch"))
                }
                "GBRU" -> {
                    listOfNote.addAll(accessDatabase.getNotes("GBNA"))
                }
                else -> {}
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment_in
        return inflater.inflate(R.layout.fragment_show_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (listOfNote.size == 0){
            ShowNoteFragment_LinearLayout_NoJourney.visibility = View.VISIBLE
            ShowNoteFragment_RecyclerView.visibility = View.GONE
        }
        else{
            ShowNoteFragment_TextView_Title.text = listOfNote[0].getTitle()
            ShowNoteFragment_LinearLayout_NoJourney.visibility = View.GONE
            ShowNoteFragment_RecyclerView.visibility = View.VISIBLE
        }
        ShowNoteFragment_RecyclerView.adapter = Adapter(listOfNote)
        ShowNoteFragment_RecyclerView.setHasFixedSize(true)
        ShowNoteFragment_RecyclerView.layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
    }

    companion object{
        fun newInstance(title:String):ShowNotes {
            val args = Bundle()
            args.putString("TITLE",title)
            val fragment = ShowNotes()
            fragment.arguments = args
            return fragment
        }
    }


    class Adapter(private var list:MutableList<Note>):RecyclerView.Adapter<Adapter.VH>(){
        class VH(viewItem:View):RecyclerView.ViewHolder(viewItem)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.frame,parent,false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val textView = holder.itemView.findViewById<TextView>(R.id.textView)
            textView.text = list[position].getBody()
        }
    }
}