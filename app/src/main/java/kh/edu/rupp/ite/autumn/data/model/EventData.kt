package kh.edu.rupp.ite.autumn.data.model

import android.os.Parcel
import android.os.Parcelable


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
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeByte(if (isSpecial) 1 else 0)
        parcel.writeByte(if (isFull) 1 else 0)
        parcel.writeString(description)
        parcel.writeString(thumbail)
        parcel.writeByte(if (isdeleted) 1 else 0)
        parcel.writeString(admin_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventData> {
        override fun createFromParcel(parcel: Parcel): EventData {
            return EventData(parcel)
        }

        override fun newArray(size: Int): Array<EventData?> {
            return arrayOfNulls(size)
        }
    }
}
