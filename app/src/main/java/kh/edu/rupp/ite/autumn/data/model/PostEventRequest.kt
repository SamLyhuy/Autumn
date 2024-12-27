package kh.edu.rupp.ite.autumn.data.model

data class PostEventRequest(
    val date: String,
    val event_info: List<EventInfo>
)