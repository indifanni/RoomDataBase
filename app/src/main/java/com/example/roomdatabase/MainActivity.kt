package com.example.roomdatabase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.room.Constant
import com.example.roomdatabase.room.Note
import com.example.roomdatabase.room.NoteDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val db by lazy { NoteDB(this) }
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()

    }

    override fun onStart() {
        super.onStart()
        loadNote()

        }

    fun loadNote(){
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("MainActivity", "dbResponse$notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }
    fun setupListener(){
        button_create.setOnClickListener{
            intentEdit(0,Constant.TYPE_CREATE)
        }
    }
    fun intentEdit(noteId: Int,intentType: Int){
        startActivity(
            Intent(applicationContext,EditActivity::class.java)
                .putExtra("intent_id",noteId)
                .putExtra("intent_type",intentType )
        )
    }


    fun button_create(view: View){
        val pindah = Intent (this@MainActivity,EditActivity::class.java)
            startActivity(pindah)
        }

    private fun setupRecyclerView(){
        noteAdapter = NoteAdapter(arrayListOf(),object :NoteAdapter.OnAdapterListener{
            override fun onClick(note: Note) {
                //read detail note
                intentEdit(note.id,Constant.TYPE_READ)
            }

            override fun onUpdate(note: Note) {
                intentEdit(note.id,Constant.TYPE_UPDATE)
            }

            override fun onDelete(note: Note) {
                deleteDialog(note)
            }
        })


        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }
    private fun deleteDialog(note: Note){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfimasi")
            setMessage("Yakin Hapus ${note.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
}
