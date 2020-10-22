package app.rootstock.api

import app.rootstock.data.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//
//
//
///**
// * Service for fetching news from REST API
// */
//interface NewsService {
//
////    /**
////     * Fetch "Popular" news
////     */
////    @GET("v2/top-headlines/")
////    fun fetchTopNews(
////        @Query("country") country: String,
////        @Query("pageSize") pageSize: Int = 100,
////        @Query("apiKey") apiKey: String = "05bd29e4cdba42cfa3fb117b03b9be87"
////    ): LiveData<ApiResponse<News>>
////
////    /**
////     * Fetch favourites news based on categories
////     */
////    @GET("v2/top-headlines/")
////    fun fetchFavouritesNews(
////        @Query("country") country: String,
////        @Query("pageSize") pageSize: Int = 100,
////        @Query("apiKey") apiKey: String = "05bd29e4cdba42cfa3fb117b03b9be87",
////        @Query("category") category: String
////    ): LiveData<ApiResponse<News>>
//
//
//}
data class UserSignUpModel(val email: String, val password: String)

interface UserSignUpService {
    @POST("/users/create")
    suspend fun createUser(@Body userSignUp: UserSignUpModel): Response<User>

}