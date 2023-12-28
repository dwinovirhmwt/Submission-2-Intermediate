package com.bangkit23dwinovirhmwt.storyhubsubmission2.di

import android.content.Context
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.AuthorizeRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.db.StoryHubDatabase
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.retrofit.ApiConfig
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.UserPreferences
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryHubRepository {
        val database = StoryHubDatabase.getDatabase(context)
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryHubRepository.getInstance(database,apiService, pref)
    }

    fun provideAuthRepository(context: Context): AuthorizeRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AuthorizeRepository.getInstance(apiService, pref)
    }
}