package com.example.hopper.data.remote.api

import com.example.hopper.data.remote.api.dto.AggregatedCrowdDto
import com.example.hopper.data.remote.api.dto.ApiResponseDto
import com.example.hopper.data.remote.api.dto.ArtistDto
import com.example.hopper.data.remote.api.dto.CrowdReportDto
import com.example.hopper.data.remote.api.dto.PandalDto
import com.example.hopper.data.remote.api.dto.TithiDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HopperApiService {

    @GET("v1/pandals")
    suspend fun getPandals(
        @Query("festival") festival: String,
        @Query("year") year: Int,
        @Header("If-Modified-Since") ifModifiedSince: String? = null
    ): Response<ApiResponseDto<List<PandalDto>>>

    @GET("v1/crowd/{pandalId}")
    suspend fun getAggregatedCrowd(
        @Path("pandalId") pandalId: String
    ): Response<ApiResponseDto<AggregatedCrowdDto>>

    @POST("v1/crowd")
    suspend fun submitCrowdReport(
        @Body report: CrowdReportDto
    ): Response<ApiResponseDto<Unit>>

    @GET("v1/calendar")
    suspend fun getCalendar(
        @Query("festival") festival: String,
        @Query("year") year: Int
    ): Response<ApiResponseDto<List<TithiDto>>>

    @GET("v1/artists")
    suspend fun getArtists(): Response<ApiResponseDto<List<ArtistDto>>>
}
