package kh.edu.rupp.ite.autumn.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventData(
    val date: String,
    val isFull: Boolean?,
    val booking_info: List<String>?,
    val isdeleted: Boolean?,
    val event_info: List<EventInfo>

): Parcelable
