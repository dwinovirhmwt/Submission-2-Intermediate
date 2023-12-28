package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.gmaps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.Result
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.StoryHubRepository
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val storyHubRepository: StoryHubRepository) : ViewModel() {

    private var _listStoryLoc = MutableLiveData<Result<List<ListStoryItem>>>()
    val listStoryLoc: LiveData<Result<List<ListStoryItem>>> = _listStoryLoc

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                _listStoryLoc.value = Result.Loading
                storyHubRepository.getStoriesWithLocation().collect {
                    _listStoryLoc.value = it
                }
            } catch (e: Exception) {
                _listStoryLoc.value = Result.Error(e.message.toString())
            }
        }
    }
}