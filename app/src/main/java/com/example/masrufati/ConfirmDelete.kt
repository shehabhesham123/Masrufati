package com.example.masrufati

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDelete(private var lam:()->Unit):DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog : AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialog.setMessage("Do you want to delete this journey")
        dialog.setPositiveButton("Yes"){_, _ ->  lam() }
        dialog.setNegativeButton("No"){_, _ -> dismiss()}
        return dialog.create()
    }
}