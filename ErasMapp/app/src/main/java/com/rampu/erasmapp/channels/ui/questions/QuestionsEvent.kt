package com.rampu.erasmapp.channels.ui.questions

sealed interface QuestionsEvent {
    data class TitleChanged(val v: String) : QuestionsEvent
    data class BodyChanged(val v: String) : QuestionsEvent
    data object CreateQuestion: QuestionsEvent
    data class ShowCreateDialog(val show: Boolean): QuestionsEvent
    data class FilterChanged(val filter: QuestionFilter) : QuestionsEvent
}