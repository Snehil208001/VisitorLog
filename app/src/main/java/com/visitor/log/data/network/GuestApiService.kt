package com.visitor.log.data.network

import com.visitor.log.data.model.GuestApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GuestApiService {
    @FormUrlEncoded
    @POST("web/guest_list.php")
    suspend fun getGuests(
        @Field("page") page: Int,
        @Field("limit") limit: Int
    ): GuestApiResponse
}