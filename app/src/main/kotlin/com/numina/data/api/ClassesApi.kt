package com.numina.data.api

import com.numina.data.models.ClassListResponse
import com.numina.data.models.FitnessClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ClassesApi {
    @GET("api/v1/classes")
    suspend fun getClasses(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("type") type: String? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Int? = null, // in kilometers
        @Query("price_min") priceMin: Double? = null,
        @Query("price_max") priceMax: Double? = null,
        @Query("intensity_min") intensityMin: Int? = null,
        @Query("intensity_max") intensityMax: Int? = null
    ): Response<ClassListResponse>

    @GET("api/v1/classes/{id}")
    suspend fun getClassById(@Path("id") classId: String): Response<FitnessClass>
}
