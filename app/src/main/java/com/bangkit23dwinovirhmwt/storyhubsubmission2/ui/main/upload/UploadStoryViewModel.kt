package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.upload

import androidx.lifecycle.ViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.model.StoryUpModel
import java.io.File

class UploadStoryViewModel(private val storyHubRepository: StoryHubRepository) : ViewModel() {

    var storyData = StoryUpModel()

    fun validate(): Boolean {
        return storyData.description.isBlank()
    }

    suspend fun addStory(imageFile: File) = storyHubRepository.addStory(imageFile, storyData)
}