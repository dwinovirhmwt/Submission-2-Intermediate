package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.di.Injection
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.detail.DetailViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.gmaps.MapsViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory.MainViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.upload.UploadStoryViewModel

class StoryHubModelFactory private constructor(private val storyHubRepository: StoryHubRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyHubRepository) as T
        }
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyHubRepository) as T
        }
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyHubRepository) as T
        }
        if (modelClass.isAssignableFrom(UploadStoryViewModel::class.java)) {
            return UploadStoryViewModel(storyHubRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: StoryHubModelFactory? = null
        fun getInstance(context: Context): StoryHubModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryHubModelFactory(Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}