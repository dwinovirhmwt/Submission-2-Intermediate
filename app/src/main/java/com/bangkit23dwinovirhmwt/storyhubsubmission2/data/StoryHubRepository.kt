package com.bangkit23dwinovirhmwt.storyhubsubmission2.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.db.StoryHubDatabase
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.model.StoryUpModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.DetailStoryResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ErrorResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ListStoryItem
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.StoryAddResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.retrofit.ApiService
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.paging.StoryRemoteMediator
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryHubRepository private constructor(
    private val storyHubDatabase: StoryHubDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreferences
) {

    suspend fun logout() = userPreference.logout()

    fun getSession() = userPreference.getSession()

    private fun handleError(e: HttpException): String {
        return try {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            errorBody.message.toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyHubDatabase, apiService),
            pagingSourceFactory = {
                storyHubDatabase.storyHubDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): Flow<Result<List<ListStoryItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            emit(Result.Success(response.listStory))
        } catch (e: HttpException) {
            emit(Result.Error(handleError(e)))
        }
    }

    suspend fun getDetailStory(id: String): Flow<Result<DetailStoryResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(handleError(e)))
        }
    }

    suspend fun addStory(
        imageFile: File,
        storyData: StoryUpModel
    ): Flow<Result<StoryAddResponse>> = flow {
        emit(Result.Loading)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val description = storyData.description.toRequestBody("text/plain".toMediaType())
        val lat = storyData.lat.toFloat()
        val lon = storyData.lon.toFloat()

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val response = apiService.addStory(multipartBody, description, lat, lon)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(handleError(e)))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryHubRepository? = null
        fun getInstance(
            storyDatabase: StoryHubDatabase,
            apiService: ApiService,
            userPreference: UserPreferences
        ): StoryHubRepository = instance ?: synchronized(this) {
            instance ?: StoryHubRepository(storyDatabase, apiService, userPreference)
        }.also { instance = it }
    }
}