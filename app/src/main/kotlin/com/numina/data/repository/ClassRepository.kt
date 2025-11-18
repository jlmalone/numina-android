package com.numina.data.repository

import com.google.gson.Gson
import com.numina.data.api.ClassesApi
import com.numina.data.db.FitnessClassDao
import com.numina.data.db.toEntity
import com.numina.data.db.toFitnessClass
import com.numina.data.models.ClassListResponse
import com.numina.data.models.ErrorResponse
import com.numina.data.models.FitnessClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepository @Inject constructor(
    private val classesApi: ClassesApi,
    private val fitnessClassDao: FitnessClassDao,
    private val gson: Gson
) {
    fun getCachedClasses(limit: Int = 50): Flow<List<FitnessClass>> {
        return fitnessClassDao.getRecentClasses(limit).map { entities ->
            entities.map { it.toFitnessClass() }
        }
    }

    suspend fun fetchClasses(
        page: Int = 1,
        perPage: Int = 20,
        dateFrom: String? = null,
        dateTo: String? = null,
        type: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Int? = null,
        priceMin: Double? = null,
        priceMax: Double? = null,
        intensityMin: Int? = null,
        intensityMax: Int? = null
    ): Flow<Result<ClassListResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = classesApi.getClasses(
                page = page,
                perPage = perPage,
                dateFrom = dateFrom,
                dateTo = dateTo,
                type = type,
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                priceMin = priceMin,
                priceMax = priceMax,
                intensityMin = intensityMin,
                intensityMax = intensityMax
            )
            if (response.isSuccessful && response.body() != null) {
                val classListResponse = response.body()!!
                // Cache the classes
                fitnessClassDao.insertClasses(
                    classListResponse.classes.map { it.toEntity() }
                )
                emit(Result.Success(classListResponse))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to fetch classes: ${response.message()}"
                    }
                } else {
                    "Failed to fetch classes: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun fetchClassById(classId: String): Flow<Result<FitnessClass>> = flow {
        emit(Result.Loading)
        try {
            val response = classesApi.getClassById(classId)
            if (response.isSuccessful && response.body() != null) {
                val fitnessClass = response.body()!!
                fitnessClassDao.insertClass(fitnessClass.toEntity())
                emit(Result.Success(fitnessClass))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "Failed to fetch class details: ${response.message()}"
                    }
                } else {
                    "Failed to fetch class details: ${response.message()}"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}", e))
        }
    }

    suspend fun clearOldCache() {
        // Clear cache older than 24 hours
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        fitnessClassDao.deleteOldClasses(oneDayAgo)
    }
}
