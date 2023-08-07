package com.example.test2.model

import android.net.Uri
import com.google.firebase.firestore.GeoPoint

data class restaurantdata (var r_id:String, var name:String, var address:String, var rate:Double, var location: GeoPoint, var imageUrl:Uri)
{
    constructor() : this ("","","",0.0,GeoPoint(0.0,0.0), Uri.parse(""))
}