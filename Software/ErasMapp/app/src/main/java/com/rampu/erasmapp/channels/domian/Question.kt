package com.rampu.erasmapp.channels.domian

data class Question(
    val id: String,
    val channelId: String,
    val title: String,
    val body: String,
    val authorId: String,
    val authorLabel: String,
    val authorPhotoUrl: String? = null,
    val createdAt: Long,
    val lastActivityAt: Long = createdAt,
    val lastMessagePreview: String = "",
    val answerCount: Long = 0L,
    val status: QuestionStatus = QuestionStatus.OPEN,
    val acceptedAnswerId: String? = null
)

enum class QuestionStatus(val firestoreValue: String){
    OPEN("open"),
    ANSWERED("answered"),
    LOCKED("locked");

    companion object{
        fun fromFirestore(value: String?): QuestionStatus = when(value?.lowercase()){
            ANSWERED.firestoreValue -> ANSWERED
            LOCKED.firestoreValue -> LOCKED
            else -> OPEN
        }
    }
}
