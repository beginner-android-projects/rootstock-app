package app.rootstock.ui.signup

import app.rootstock.api.UserSignUpModel
import app.rootstock.api.UserSignUpService
import app.rootstock.data.user.User
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

//
//fun <T, A> performGetOperation(databaseQuery: () -> LiveData<T>,
//                               networkCall: suspend () -> Resource<A>,
//                               saveCallResult: suspend (A) -> Unit): LiveData<Resource<T>> =
//    liveData(Dispatchers.IO) {
//        emit(Resource.loading())
//        val source = databaseQuery.invoke().map { Resource.success(it) }
//        emitSource(source)
//
//        val responseStatus = networkCall.invoke()
//        if (responseStatus.status == SUCCESS) {
//            saveCallResult(responseStatus.data!!)
//
//        } else if (responseStatus.status == ERROR) {
//            emit(Resource.error(responseStatus.message!!))
//            emitSource(source)
//        }
//    }

class SignUpLoader @Inject constructor(private val signUpService: UserSignUpService) {

    suspend fun register(user: SignUpUser): Response<User> {
        return signUpService.createUser(UserSignUpModel(user.email, user.password))
    }

}

@Singleton
class RegisterRepository @Inject constructor(
    private val signUpLoader: SignUpLoader
) {

    suspend fun register(signUpUser: SignUpUser): Response<User> {
        return signUpLoader.register(signUpUser)
    }
}