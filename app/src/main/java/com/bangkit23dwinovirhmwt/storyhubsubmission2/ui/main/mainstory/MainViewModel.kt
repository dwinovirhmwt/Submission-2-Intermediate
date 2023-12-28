package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel (private val storyHubRepository: StoryHubRepository) : ViewModel() {

    val listStory: LiveData<PagingData<ListStoryItem>> =
        storyHubRepository.getStories().cachedIn(viewModelScope)

    fun getSession() = storyHubRepository.getSession()

    fun logout() {
        viewModelScope.launch {
            storyHubRepository.logout()
        }
    }
}