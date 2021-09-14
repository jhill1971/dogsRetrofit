package com.jameshill.dogsretrofit.api

import retrofit2.http.GET

const val BASE_URL = "https://random.dog"

interface ApiRequest {
    @GET (value = "/woof.json?ref=apilist.fun")
    suspend fun getRandomDog():ApiData
}