package com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.mainstory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit23dwinovirhmwt.storyhubsubmission2.R
import com.bangkit23dwinovirhmwt.storyhubsubmission2.adapter.LoadingStateAdapter
import com.bangkit23dwinovirhmwt.storyhubsubmission2.adapter.StoryHubListAdapter
import com.bangkit23dwinovirhmwt.storyhubsubmission2.databinding.ActivityMainBinding
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.StoryHubModelFactory
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.authorize.login.LoginActivity
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.gmaps.MapsActivity
import com.bangkit23dwinovirhmwt.storyhubsubmission2.ui.main.upload.UploadStoryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        StoryHubModelFactory.getInstance(this)
    }
    private lateinit var storyHubAdapter: StoryHubListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnAdd.setOnClickListener {
            toUploadStory()
        }

        checkSession()
        initRecylerView()
        getData()
    }


    private fun checkSession() {
        lifecycleScope.launch {
            viewModel.getSession().collect { session ->
                if (!session.isLogin) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))

                }

            }
        }
    }

    private fun initRecylerView() {
        storyHubAdapter = StoryHubListAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = storyHubAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        binding.rvStory.adapter = storyHubAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyHubAdapter.retry()
            }
        )

        viewModel.listStory.observe(this) {
            storyHubAdapter.submitData(lifecycle, it)
        }
    }

    private fun toUploadStory() {
        startActivity(Intent(this@MainActivity, UploadStoryActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                return true
            }

            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            R.id.action_logout -> {
                val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                val scope = CoroutineScope(dispatcher)
                scope.launch {
                    viewModel.logout()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}