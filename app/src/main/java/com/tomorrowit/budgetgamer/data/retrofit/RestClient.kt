package com.tomorrowit.budgetgamer.data.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface RestClient {

    /**
     * This is the preferred order, all objects are maps of Strings and Object
     * <p>
     * parameters is Params field in postman
     * headers    is Headers field in postman
     * jsonBody   is Body field in postman as Gson JsonObject
     */

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/gameLinkHandler")
    fun gameLinkHandler(
        @Field("link") link: String,
        @Field("uid") uid: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/articleLinkHandler")
    fun articleLinkHandler(
        @Field("link") link: String,
        @Field("uid") uid: String
    ): Call<JsonObject>

}