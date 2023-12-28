package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit23dwinovirhmwt.storyhubsubmission2.R
import com.bangkit23dwinovirhmwt.storyhubsubmission2.databinding.ActivityDetailBinding
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.StoryHubModelFactory
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.launch
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.Result

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        StoryHubModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetailActivity)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showDetail()
        backToMain()
    }

    private fun backToMain() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        })
    }


    private fun showDetail() {
        val id = intent.getStringExtra("id")
        if (id != null) {
            lifecycleScope.launch {
                viewModel.getDetailStory(id).collect { result ->
                    when (result) {
                        is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.apply {
                                progressBar.visibility = View.GONE
                                Glide.with(this@DetailActivity)
                                    .load(result.data.story?.photoUrl)
                                    .transform(CenterCrop(), RoundedCorners(30)).into(ivDetail)

                                tvTitleDescription.text = result.data.story?.name
                                tvDescription.text = result.data.story?.description
                            }
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
    }
}