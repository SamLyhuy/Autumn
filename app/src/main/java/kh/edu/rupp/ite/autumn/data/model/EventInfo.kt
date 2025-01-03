package kh.edu.rupp.ite.autumn.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventInfo (

    val name: String,
    val time: String,
    val description: String,
    val isSpecial: Boolean,
    val thumbnail: String?,
    val _id: String?,

): Parcelable

