package dev.chara.tasks.shared.data.rest

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapBoth
import dev.chara.tasks.shared.data.ApiError
import dev.chara.tasks.shared.data.ClientError
import dev.chara.tasks.shared.data.preference.PreferenceDataSource
import dev.chara.tasks.shared.model.Profile
import dev.chara.tasks.shared.model.Task
import dev.chara.tasks.shared.model.TaskList
import dev.chara.tasks.shared.model.TaskListPrefs
import dev.chara.tasks.shared.model.TokenPair
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

expect fun createHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

class RestDataSource(private val preferenceDataSource: PreferenceDataSource, endpoint: Endpoint) {
    private val endpointUrl: String = endpoint.url

    /** TODO create this outside of the RestDataSource so we can inject preferences as needed? */
    private val client by lazy {
        createHttpClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(Auth) {
                bearer {
                    realm = "Tasks"
                    loadTokens {
                        val tokens = preferenceDataSource.getApiTokens().first()

                        if (tokens == null) {
                            preferenceDataSource.clearAuthFields()
                            return@loadTokens null
                        }

                        BearerTokens(tokens.access, tokens.refresh)
                    }
                    refreshTokens {
                        val refreshToken: String =
                            oldTokens?.refreshToken
                                ?: preferenceDataSource.getApiTokens().first()?.refresh
                                ?: return@refreshTokens null

                        refreshAuth(refreshToken)
                            .mapBoth(
                                success = { tokenPair ->
                                    preferenceDataSource.setApiTokens(tokenPair)
                                    BearerTokens(tokenPair.access, tokenPair.refresh)
                                },
                                failure = {
                                    preferenceDataSource.clearAuthFields()
                                    null
                                }
                            )
                    }
                    sendWithoutRequest {
                        /* We don't want to hammer the server with credential requests for every request we make */
                        true
                    }
                }
            }
        }
    }

    suspend fun getUserProfile() =
        try {
            val response = client.get("$endpointUrl/profile")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val profile: Profile = response.body()
                    Ok(profile)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getUserProfileFor(userId: String) =
        try {
            val response = client.get("$endpointUrl/profile/$userId")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val profile: Profile = response.body()
                    Ok(profile)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun updateUserProfile(profile: Profile) =
        try {
            val response =
                client.put("$endpointUrl/profile") {
                    contentType(ContentType.Application.Json)
                    setBody(profile)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun updateUserProfilePhoto(photo: ByteArray) =
        try {
            val response =
                client.submitFormWithBinaryData(
                    "$endpointUrl/profile/photo",
                    formData {
                        append(
                            "image",
                            photo,
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            }
                        )
                    }
                )

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable
    data class RegisterCredentials(
        val email: String,
        val displayName: String,
        val password: String
    )

    suspend fun createUser(email: String, displayName: String, password: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(RegisterCredentials(email, displayName, password))
                }

            when (response.status) {
                HttpStatusCode.Created -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Conflict -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class LoginCredentials(val email: String, val password: String)

    suspend fun authenticateUser(email: String, password: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginCredentials(email, password))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val tokenPair: TokenPair = response.body()
                    Ok(tokenPair)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Unauthorized -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    private suspend fun RefreshTokensParams.refreshAuth(refreshToken: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/refresh") {
                    contentType(ContentType.Text.Plain)
                    setBody(refreshToken)
                    markAsRefreshTokenRequest()
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val tokenPair: TokenPair = response.body()
                    Ok(tokenPair)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun changeEmail(newEmail: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/email") {
                    contentType(ContentType.Text.Plain)
                    setBody(newEmail)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Conflict -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun requestVerifyEmailResend() =
        try {
            val response = client.post("$endpointUrl/auth/email/resend")

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class PasswordChange(val currentPassword: String, val newPassword: String)

    suspend fun changePassword(currentPassword: String, newPassword: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/password") {
                    contentType(ContentType.Application.Json)
                    setBody(PasswordChange(currentPassword, newPassword))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class EmailVerification(val verifyToken: String, val email: String)

    suspend fun verifyEmail(verifyToken: String, email: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/verify") {
                    contentType(ContentType.Application.Json)
                    setBody(EmailVerification(verifyToken, email))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Unauthorized -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun requestPasswordResetEmail(email: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/forgot") {
                    contentType(ContentType.Text.Plain)
                    setBody(email)
                }

            when (response.status) {
                HttpStatusCode.Accepted -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Unauthorized -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class PasswordReset(val resetToken: String, val newPassword: String)

    suspend fun resetPassword(resetToken: String, newPassword: String) =
        try {
            val response =
                client.post("$endpointUrl/auth/reset") {
                    contentType(ContentType.Application.Json)
                    setBody(PasswordReset(resetToken, newPassword))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest,
                HttpStatusCode.Unauthorized -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getLists() =
        try {
            val response = client.get("$endpointUrl/lists")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val taskLists: List<TaskList> = response.body()
                    Ok(taskLists)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class Insert(val taskList: TaskList, val prefs: TaskListPrefs)

    suspend fun createList(taskList: TaskList, prefs: TaskListPrefs) =
        try {
            val response =
                client.post("$endpointUrl/lists") {
                    contentType(ContentType.Application.Json)
                    setBody(Insert(taskList, prefs))
                }

            when (response.status) {
                HttpStatusCode.Created -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun updateList(listId: String, taskList: TaskList) =
        try {
            val response =
                client.put("$endpointUrl/lists/$listId") {
                    contentType(ContentType.Application.Json)
                    setBody(taskList)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun reorderList(listId: String, fromIndex: Int, toIndex: Int, lastModified: Instant) =
        try {
            val response =
                client.post("$endpointUrl/lists/$listId/reorder") {
                    contentType(ContentType.Application.Json)
                    setBody(Reorder(fromIndex, toIndex, lastModified))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun deleteList(listId: String) =
        try {
            val response = client.delete("$endpointUrl/lists/$listId")

            when (response.status) {
                HttpStatusCode.Accepted -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getListPrefs(listId: String) =
        try {
            val response = client.get("$endpointUrl/lists/$listId/prefs")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val prefs: TaskListPrefs? = response.body()
                    Ok(prefs)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun updateListPrefs(listId: String, prefs: TaskListPrefs) =
        try {
            val response =
                client.put("$endpointUrl/lists/$listId/prefs") {
                    contentType(ContentType.Application.Json)
                    setBody(prefs)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getListMembers(listId: String) =
        try {
            val response = client.get("$endpointUrl/lists/$listId/members")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val profiles = response.body<List<Profile>>()
                    Ok(profiles)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun requestListInvite(listId: String) =
        try {
            val response = client.post("$endpointUrl/lists/$listId/invite")

            when (response.status) {
                HttpStatusCode.Created -> {
                    val token = response.bodyAsText()
                    Ok(token)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getListInviteInfo(inviteToken: String) =
        try {
            val response =
                client.post("$endpointUrl/lists/invite") {
                    contentType(ContentType.Text.Plain)
                    setBody(inviteToken)
                }
            when (response.status) {
                HttpStatusCode.OK -> {
                    val taskList = response.body<TaskList>()
                    Ok(taskList)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun requestListJoin(inviteToken: String) =
        try {
            val response =
                client.post("$endpointUrl/lists/join") {
                    contentType(ContentType.Text.Plain)
                    setBody(inviteToken)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun leaveList(listId: String) =
        try {
            val response = client.post("$endpointUrl/lists/$listId/leave")

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun removeMemberFromList(listId: String, memberId: String) =
        try {
            val response =
                client.post("$endpointUrl/lists/$listId/remove") {
                    contentType(ContentType.Text.Plain)
                    setBody(memberId)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun getTasks(listId: String) =
        try {
            val response = client.get("$endpointUrl/lists/$listId/tasks")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val tasks: List<Task> = response.body()
                    Ok(tasks)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun createTask(listId: String, task: Task) =
        try {
            val response =
                client.post("$endpointUrl/lists/$listId/tasks") {
                    contentType(ContentType.Application.Json)
                    setBody(task)
                }

            when (response.status) {
                HttpStatusCode.Created -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun updateTask(listId: String, taskId: String, task: Task) =
        try {
            val response =
                client.put("$endpointUrl/lists/$listId/tasks/$taskId") {
                    contentType(ContentType.Application.Json)
                    setBody(task)
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun deleteTask(listId: String, taskId: String) =
        try {
            val response = client.delete("$endpointUrl/lists/$listId/tasks/$taskId")

            when (response.status) {
                HttpStatusCode.Accepted -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    @Serializable data class Move(val newListId: String, val lastModified: Instant)

    suspend fun moveTask(
        oldListId: String,
        newListId: String,
        taskId: String,
        lastModified: Instant
    ) =
        try {
            val response =
                client.post("$endpointUrl/lists/$oldListId/tasks/$taskId/move") {
                    contentType(ContentType.Application.Json)
                    setBody(Move(newListId, lastModified))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ) =
        try {
            val response =
                client.post("$endpointUrl/lists/$listId/tasks/$taskId/reorder") {
                    contentType(ContentType.Application.Json)
                    setBody(Reorder(fromIndex, toIndex, lastModified))
                }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun clearCompletedTasks(listId: String) =
        try {
            val response = client.post("$endpointUrl/lists/$listId/tasks/clear")

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun linkFCMToken(fcmToken: String) =
        try {
            val response = client.post("$endpointUrl/fcm/link") { setBody(fcmToken) }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }

    suspend fun invalidateFcmToken(fcmToken: String) =
        try {
            val response = client.post("$endpointUrl/fcm/invalidate") { setBody(fcmToken) }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Ok(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    Err(ApiError.InvalidQuery(response.body()))
                }
                else -> {
                    Err(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Err(ClientError(ex))
        }
}

data class Endpoint(val url: String)
