package com.rampu.erasmapp.channels.ui.threads

import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.channels.domian.Answer
import com.rampu.erasmapp.channels.domian.Question
import com.rampu.erasmapp.channels.domian.QuestionStatus
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.common.util.formatTime
import com.rampu.erasmapp.ui.theme.ErasMappTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ThreadScreen(
    onBack: () -> Unit,
    onEvent: (event: ThreadEvent) -> Unit,
    state: ThreadUiState
) {
    val context = LocalContext.current
    val question = state.question
    val status = question?.status ?: QuestionStatus.OPEN
    val acceptedAnswerId = question?.acceptedAnswerId
    val acceptedIndex =
        if (acceptedAnswerId != null) state.answers.indexOfFirst { it.id == acceptedAnswerId } else -1

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var highlightId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.toastMsg) {
        state.toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        !state.errorMsg.isNullOrBlank() -> {
            ErrorMessage(
                message = state.errorMsg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                    Text(text = state.channelTitle, style = MaterialTheme.typography.titleLarge)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        question?.let { question ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        UserAvatar(
                                            label = question.authorLabel,
                                            photoUrl = question.authorPhotoUrl,
                                            size = 32.dp
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = question.authorLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ThreadStatus(status = status, onToggleLock = {
                                            onEvent(
                                                ThreadEvent.ToggleLock
                                            )
                                        }, modifier = Modifier.width(20.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = formatTime(context, question.createdAt),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = question.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = question.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                if (acceptedIndex >= 0) {
                                    TextButton(onClick = {
                                        scope.launch {
                                            val targetIndex = 1 + acceptedIndex
                                            listState.animateScrollToItem(targetIndex)
                                            highlightId = acceptedAnswerId
                                            delay(2000)
                                            highlightId = null
                                        }
                                    }) { Text(stringResource(R.string.go_to_accepted_answer)) }
                                }


                            }
                        }
                    }

                    if (state.answers.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_answers_yet),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }

                    items(state.answers, key = { it.id }) { answer ->
                        AnswerBubble(
                            answer = answer,
                            isMine = answer.authorId == state.currentUserId,
                            timeText = formatTime(context, answer.createdAt),
                            isAccepted = answer.id == acceptedAnswerId,
                            highlight = answer.id == highlightId
                        )
                    }
                }

                if (state.showMessageBox) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        LabeledInputField(
                            value = state.newAnswer,
                            placeholder = stringResource(R.string.your_answer),
                            onValueChange = { onEvent(ThreadEvent.BodyChanged(it)) },
                            modifier = Modifier.weight(1f),
                            enabled = !state.isSaving
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { onEvent(ThreadEvent.PostAnswer) },
                            modifier = Modifier.align(Alignment.CenterVertically),
                            enabled = state.canSendAnswer
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(
                                    R.string.send
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThreadStatus(status: QuestionStatus, onToggleLock: () -> Unit, modifier: Modifier = Modifier) {
    val icon =
        if (status == QuestionStatus.LOCKED) painterResource(R.drawable.lock) else painterResource(R.drawable.lock_open)

    IconButton(
        onClick = onToggleLock,
        modifier = modifier
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }

}

@Composable
fun AnswerBubble(answer: Answer, isMine: Boolean, timeText: String, isAccepted: Boolean, highlight: Boolean) {
    val baseColor = when {
        isAccepted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        isMine -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val target = if(highlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else baseColor
    val bgColor by animateColorAsState(target)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMine) {
            UserAvatar(label = answer.authorLabel, photoUrl = answer.authorPhotoUrl, size = 36.dp)
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .fillMaxWidth(0.85f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = answer.authorLabel, style = MaterialTheme.typography.labelSmall
                )
                if (isAccepted) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(R.string.accepted_answer),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
            Text(text = answer.body, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isAccepted) {
                    Text(
                        text = stringResource(R.string.accepted),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ThreadScreenPreview() {
    ErasMappTheme {
        ThreadScreen(
            onBack = {},
            onEvent = {},
            state = ThreadUiState(
                channelId = "channelId",
                channelTitle = "Channel title",
                questionId = "questionId",
                question = Question(
                    id = "questionId",
                    channelId = "channelId",
                    title = "Title",
                    body = "Question body text here",
                    authorId = "authorId",
                    authorLabel = "Author name",
                    authorPhotoUrl = null,
                    createdAt = System.currentTimeMillis() - 2 * DateUtils.DAY_IN_MILLIS,
                    lastActivityAt = System.currentTimeMillis(),
                    lastMessagePreview = "Last message preview",
                    answerCount = 1
                ),
                answers = listOf(
                    Answer(
                        id = "answerId",
                        channelId = "channelId",
                        questionId = "questionId",
                        body = "This is a preview answer",
                        authorId = "authorId",
                        authorLabel = "Author Name",
                        createdAt = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS
                    )
                ),
                isLoading = false,
                errorMsg = null,
                isSignedOut = false,
                isSaving = false,
                newAnswer = "",
                currentUserId = "userId",
                canSendAnswer = true
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun ThreadScreenAcceptedPinnedPreview() {
    ErasMappTheme {
        ThreadScreen(
            onBack = {},
            onEvent = {},
            state = ThreadUiState(
                channelId = "channelId",
                channelTitle = "Dorms",
                questionId = "questionId",
                question = Question(
                    id = "questionId",
                    channelId = "channelId",
                    title = "Can I switch dorm rooms?",
                    body = "I want to swap rooms with a friend. What's the process?",
                    authorId = "authorId",
                    authorLabel = "luka",
                    authorPhotoUrl = null,
                    createdAt = System.currentTimeMillis() - 2 * DateUtils.DAY_IN_MILLIS,
                    lastActivityAt = System.currentTimeMillis() - DateUtils.HOUR_IN_MILLIS,
                    lastMessagePreview = "You can submit a request...",
                    answerCount = 3,
                    status = QuestionStatus.ANSWERED,
                    acceptedAnswerId = "answer_1"
                ),
                answers = listOf(
                    Answer(
                        id = "answer_1",
                        channelId = "channelId",
                        questionId = "questionId",
                        body = "You can submit a room-change request in the dorm office. Bring both student IDs.",
                        authorId = "staff1",
                        authorLabel = "admin",
                        createdAt = System.currentTimeMillis() - 3 * DateUtils.HOUR_IN_MILLIS
                    ),
                    Answer(
                        id = "answer_2",
                        channelId = "channelId",
                        questionId = "questionId",
                        body = "We did this last semester; took 2 days after approval.",
                        authorId = "user2",
                        authorLabel = "marko",
                        createdAt = System.currentTimeMillis() - 2 * DateUtils.HOUR_IN_MILLIS
                    ),
                    Answer(
                        id = "answer_3",
                        channelId = "channelId",
                        questionId = "questionId",
                        body = "Is it possible to change only within the same dorm building?",
                        authorId = "user3",
                        authorLabel = "ana",
                        createdAt = System.currentTimeMillis() - DateUtils.HOUR_IN_MILLIS
                    )
                ),
                isLoading = false,
                errorMsg = null,
                isSignedOut = false,
                isSaving = false,
                newAnswer = "",
                currentUserId = "user2",
                canSendAnswer = true,
                showMessageBox = true
            )
        )
    }
}