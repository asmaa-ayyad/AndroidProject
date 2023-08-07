package com.example.test2.model

import android.net.Uri
import com.google.firebase.firestore.DocumentId

data class mealdata (
    @DocumentId
    var m_id:String,
    var name:String, var price:String, var rate:Double, var description:String, var imageUrl: Uri,var restid:String , var issold :String, var numofreq : Int)
{
    constructor() : this ("","","",0.0,"", Uri.parse(""),"","",0)
}