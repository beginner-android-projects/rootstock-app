package app.rootstock.ui.signin

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import app.rootstock.BR
import java.io.Serializable


class SignInUser :
    BaseObservable(), Serializable {


    override fun toString(): String {
        return "User: $email"
    }

    companion object {
        fun build(): SignInUser {
            return SignInUser()
        }

        // only ascii chars with no spaces or tabs
        private val passwordRegex = """^[a-zA-Z0-9@\#\?+!:;'"*\-\$\_&\(\)^,./\\-]{6,32}$""".toRegex()

        // standard email regex
        private val emailRegex =
            """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".toRegex()
    }

    @Bindable
    var emailValid: Boolean = false
        get() = field

    @Bindable
    var passwordValid: Boolean = false
        get() = field


    @Bindable
    var email: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
            checkEmail()
        }
        get() = field

    @Bindable
    var password: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
            checkPassword()
        }
        get() = field

    private fun checkEmail() {
        emailValid = isEmailValid()
        notifyPropertyChanged(BR.emailValid)
    }

    private fun checkPassword() {
        passwordValid = isPasswordValid()
        notifyPropertyChanged(BR.passwordValid)
    }

    fun isEmailValid(): Boolean =
        email.matches(emailRegex)

    fun isPasswordValid(): Boolean =
        password.matches(passwordRegex)

    fun isDataValid() = isEmailValid() && isPasswordValid()
}