package com.rampu.erasmapp.channels.ui.questions

import com.rampu.erasmapp.channels.domian.QuestionStatus

enum class QuestionFilter(val label: String){
    OPEN("Open"),
    ANSWERED("Answered");

    fun matches(status: QuestionStatus): Boolean = when(this){
        OPEN -> status == QuestionStatus.OPEN
        ANSWERED -> status == QuestionStatus.ANSWERED || status == QuestionStatus.LOCKED
    }
}