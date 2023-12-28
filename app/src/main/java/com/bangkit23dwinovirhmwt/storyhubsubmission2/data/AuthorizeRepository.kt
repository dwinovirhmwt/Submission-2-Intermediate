package com.bangkit23dwinovirhmwt.storyhubsubmission2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.model.LoginModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.model.RegisterModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.ErrorResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.LoginResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.response.RegisterResponse
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.network.retrofit.ApiService
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.UserModel
import com.bangkit23dwinovirhmwt.storyhubsubmission2.data.preference.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AuthorizeRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) { private val authRegister = MediatorLiveData<Result<RegisterResponse?>>()
    private val authLogin = MediatorLiveData<Result<LoginResponse?>>()

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse?>> {
        authRegister.value = Result.Loading

        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    authRegister.value = Result.Success(response.body())
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        authRegister.value = errorMessage.message?.let { Result.Error(it) }
                    } catch (e: Exception) {
                        authRegister.value = Result.Error("Error parsing error response")
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                authRegister.value = Result.Error(t.message.toString())
            }
        })

        return authRegister
    }

    fun loginUser(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse?>> {
        authLogin.value = Result.Loading

        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    if (loginResult != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPreferences.saveSession(
                                UserModel(
                                    loginResult.userId.toString(),
                                    loginResult.name.toString(),
                                    "Bearer ${loginResult.token.toString()}",
                                    true
                                )
                            )
                        }
                    }
                    authLogin.value = Result.Success(response.body())
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        authLogin.value = errorMessage.message?.let { Result.Error(it) }
                    } catch (e: Exception) {
                        authLogin.value = Result.Error("Error parsing error response")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                authLogin.value = Result.Error(t.message.toString())
            }

        })

        return authLogin
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    companion object {
        @Volatile
        private var instance: AuthorizeRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): AuthorizeRepository = instance ?: synchronized(this) {
            instance ?: AuthorizeRepository(apiService, userPreferences)
        }.also { instance = it }
    }
}