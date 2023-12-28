package com.bangkit23dwinovirhmwt.storyhubsubmission2

import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ListStoryItem
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.StoryResponse

object DataDummy {
    fun generateDummy(): StoryResponse {
        val listStory = ArrayList<ListStoryItem>()
        for (list in 0..100) {
            val story = ListStoryItem(
                id = "id_$list",
                photoUrl = "https://upload.wikimedia.org/wikipedia/id/0/0b/Your_Name_poster.png",
                createdAt = "2023-12-13T08:17:35.997Z",
                name = "name $list",
                description = "desc $list",
                lat = list.toDouble() * 10,
                lon = list.toDouble() * 10
            )
            listStory.add(story)
        }

        return StoryResponse(
            error = false,
            message = "Story Succesfully",
            listStory = listStory
        )
    }
}