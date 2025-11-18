package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.UserApi
import com.numina.data.db.UserDao
import com.numina.data.db.toEntity
import com.numina.data.db.toUser
import com.numina.data.models.ErrorResponse
import com.numina.data.models.UpdateProfileRequest
import com.numina.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDao,
    private val gson: Gson
) {
    fun getCachedUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toUser() }
    }

    suspend fun fetchCurrentUser(): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val response = userApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.insertUser(user.toEntity())
                emit(Result.Success(user))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to fetch user: ${response.message()}"
                    }
                } else {
                    "Failed to fetch user: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val response = userApi.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.insertUser(user.toEntity())
                emit(Result.Success(user))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to update profile: ${response.message()}"
                    }
                } else {
                    "Failed to update profile: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }
}
