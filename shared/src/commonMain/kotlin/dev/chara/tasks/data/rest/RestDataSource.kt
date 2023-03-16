package dev.chara.tasks.data.rest

import dev.chara.tasks.data.preference.PreferenceDataSource
import dev.chara.tasks.model.LoginCredentials
import dev.chara.tasks.model.Move
import dev.chara.tasks.model.PasswordChange
import dev.chara.tasks.model.PasswordReset
import dev.chara.tasks.model.Profile
import dev.chara.tasks.model.Reorder
import dev.chara.tasks.model.SignUpCredentials
import dev.chara.tasks.model.Task
import dev.chara.tasks.model.TaskList
import dev.chara.tasks.model.TokenPair
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
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

expect fun createHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

class RestDataSource(
    private val preferenceDataSource: PreferenceDataSource,
    endpoint: Endpoint
) {
    private val endpointUrl: String = "${endpoint.url}/v2"

    /**
     * TODO create this outside of the RestDataSource so we can inject preferences as needed
     */
    private val client by lazy {
        createHttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Auth) {
                bearer {
                    realm = "Tasks"
                    loadTokens {
                        val tokens = preferenceDataSource.getApiTokens().first()

                        if (tokens == null) {
                            preferenceDataSource.clearAuthFields()
                            return@loadTokens null
                        }

                        BearerTokens(tokens.accessToken, tokens.refreshToken)
                    }
                    refreshTokens {
                        val refreshToken: String = oldTokens?.refreshToken
                            ?: preferenceDataSource.getApiTokens().first()?.refreshToken
                            ?: return@refreshTokens null

                        val result = refreshAuth(refreshToken)

                        if (result.isSuccess) {
                            val tokenPair = result.getOrThrow()
                            preferenceDataSource.setApiTokens(tokenPair)
                            BearerTokens(tokenPair.accessToken, tokenPair.refreshToken)
                        } else {
                            preferenceDataSource.clearAuthFields()
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.pathSegments.contains("auth") || request.url.pathSegments.contains(
                            "photo"
                        )
                    }
                }
            }
        }
    }

    suspend fun getUserProfile(): Result<Profile> = try {
        val response = client.get("$endpointUrl/self/profile")

        when (response.status) {
            HttpStatusCode.OK -> {
                val profile: Profile = response.body()
                Result.success(profile)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(ApiError.OtherServerError(response.body()))
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun updateUserProfile(profile: Profile) = try {
        val response = client.put("$endpointUrl/self/profile") {
            contentType(ContentType.Application.Json)
            setBody(profile)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun updateUserProfilePhoto(photo: ByteArray) = try {
        val response = client.submitFormWithBinaryData(
            "$endpointUrl/self/profile/photo",
            formData {
                append("image", photo, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                })
            }
        )

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun createUser(email: String, displayName: String, password: String): Result<Unit> =
        try {
            val response = client.post("$endpointUrl/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignUpCredentials(email, displayName, password))
            }

            when (response.status) {
                HttpStatusCode.Created -> {
                    Result.success(Unit)
                }

                HttpStatusCode.BadRequest, HttpStatusCode.Conflict -> {
                    Result.failure(ApiError.InvalidQuery(response.body()))
                }

                else -> {
                    Result.failure(response.body())
                }
            }
        } catch (ex: Throwable) {
            Result.failure(ex)
        }

    suspend fun authenticateUser(email: String, password: String): Result<TokenPair> = try {
        val response = client.post("$endpointUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginCredentials(email, password))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val tokenPair: TokenPair = response.body()
                Result.success(tokenPair)
            }

            HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    private suspend fun RefreshTokensParams.refreshAuth(refreshToken: String): Result<TokenPair> =
        try {
            val response = client.post("$endpointUrl/auth/refresh") {
                contentType(ContentType.Text.Plain)
                setBody(refreshToken)
                markAsRefreshTokenRequest()
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val tokenPair: TokenPair = response.body()
                    Result.success(tokenPair)
                }

                HttpStatusCode.BadRequest -> {
                    Result.failure(ApiError.InvalidQuery(response.body()))
                }

                else -> {
                    Result.failure(ApiError.OtherServerError(response.body()))
                }
            }
        } catch (ex: Throwable) {
            Result.failure(ex)
        }

    suspend fun changePassword(currentPassword: String, newPassword: String) = try {
        val response = client.post("$endpointUrl/auth/password") {
            contentType(ContentType.Application.Json)
            setBody(PasswordChange(currentPassword, newPassword))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(ApiError.OtherServerError(response.body()))
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun requestPasswordResetEmail(email: String) = try {
        val response = client.post("$endpointUrl/auth/forgot") {
            contentType(ContentType.Text.Plain)
            setBody(email)
        }

        when (response.status) {
            HttpStatusCode.Accepted -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun resetPassword(
        resetToken: String,
        newPassword: String
    ): Result<Unit> = try {
        val response = client.post("$endpointUrl/auth/reset") {
            contentType(ContentType.Application.Json)
            setBody(PasswordReset(resetToken, newPassword))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun getLists(): Result<List<TaskList>> = try {
        val response = client.get("$endpointUrl/self/lists")

        when (response.status) {
            HttpStatusCode.OK -> {
                val taskList: List<TaskList> = response.body()
                Result.success(taskList)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(ApiError.OtherServerError(response.body()))
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun createList(taskList: TaskList): Result<Unit> = try {
        val response = client.post("$endpointUrl/self/lists") {
            contentType(ContentType.Application.Json)
            setBody(taskList)
        }

        when (response.status) {
            HttpStatusCode.Created -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun updateList(listId: String, taskList: TaskList): Result<Unit> = try {
        val response = client.put("$endpointUrl/self/lists/$listId") {
            contentType(ContentType.Application.Json)
            setBody(taskList)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun deleteList(listId: String): Result<Unit> = try {
        val response = client.delete("$endpointUrl/self/lists/$listId")

        when (response.status) {
            HttpStatusCode.Accepted -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun getTasks(listId: String): Result<List<Task>> = try {
        val response = client.get("$endpointUrl/lists/$listId/tasks")

        when (response.status) {
            HttpStatusCode.OK -> {
                val tasks: List<Task> = response.body()
                Result.success(tasks)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(ApiError.OtherServerError(response.body()))
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun createTask(listId: String, task: Task): Result<Unit> = try {
        val response = client.post("$endpointUrl/lists/$listId/tasks") {
            contentType(ContentType.Application.Json)
            setBody(task)
        }

        when (response.status) {
            HttpStatusCode.Created -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun updateTask(listId: String, taskId: String, task: Task): Result<Unit> = try {
        val response = client.put("$endpointUrl/lists/$listId/tasks/$taskId") {
            contentType(ContentType.Application.Json)
            setBody(task)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun deleteTask(listId: String, taskId: String): Result<Unit> = try {
        val response = client.delete("$endpointUrl/lists/$listId/tasks/$taskId")

        when (response.status) {
            HttpStatusCode.Accepted -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun moveTask(
        oldListId: String,
        newListId: String,
        taskId: String,
        lastModified: Instant
    ): Result<Unit> = try {
        val response = client.post("$endpointUrl/lists/$oldListId/tasks/$taskId/move") {
            contentType(ContentType.Application.Json)
            setBody(Move(newListId, lastModified))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun reorderTask(
        listId: String,
        taskId: String,
        fromIndex: Int,
        toIndex: Int,
        lastModified: Instant
    ): Result<Unit> = try {
        val response = client.post("$endpointUrl/lists/$listId/tasks/$taskId/reorder") {
            contentType(ContentType.Application.Json)
            setBody(Reorder(fromIndex, toIndex, lastModified))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun clearCompletedTasks(listId: String): Result<Unit> = try {
        val response = client.post("$endpointUrl/lists/$listId/clear")

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }

    suspend fun linkFCMToken(fcmToken: String): Result<Unit> = try {
        val response = client.post("$endpointUrl/fcm/link") {
            setBody(fcmToken)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Result.success(Unit)
            }

            HttpStatusCode.BadRequest -> {
                Result.failure(ApiError.InvalidQuery(response.body()))
            }

            else -> {
                Result.failure(response.body())
            }
        }
    } catch (ex: Throwable) {
        Result.failure(ex)
    }
}

data class Endpoint(val url: String)

sealed class ApiError(message: String?) : Throwable(message) {
    class InvalidQuery(message: String?) : ApiError(message)
    class OtherServerError(message: String?) : ApiError(message)
}