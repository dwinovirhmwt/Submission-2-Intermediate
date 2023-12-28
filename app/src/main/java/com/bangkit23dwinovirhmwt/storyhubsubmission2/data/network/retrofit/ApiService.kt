package com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.retrofit

import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.DetailStoryResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.LoginResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.RegisterResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.StoryAddResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): StoryResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): StoryAddResponse
}