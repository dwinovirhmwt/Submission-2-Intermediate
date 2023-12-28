package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.detail

import androidx.lifecycle.ViewModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository

class DetailViewModel (private val storyHubRepository: StoryHubRepository) : ViewModel() {
    suspend fun getDetailStory(id: String) = storyHubRepository.getDetailStory(id)
}