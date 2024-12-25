package kh.edu.rupp.ite.autumn.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventData(
    val name: String,
    val date: String,
    val time: String,
    val isSpecial: Boolean,
    val isFull: Boolean,
    val description: String,
    val thumbail: String,
    //val booking_table: List<Booking>,
    val isdeleted: Boolean,
    val admin_id: String
): Parcelable
