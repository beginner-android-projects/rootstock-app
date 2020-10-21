package app.rootstock.ui.signup

import org.junit.Test

class SignUpUserTest {

    private val user = SignUpUser()

    private val emailMap = hashSetOf(
        "123" to false,
        "asdmsadop123" to false,
        "asdmsadop123@mail.ru" to true,
        "asdmp@asd.ru" to true,
        "" to false,
        "asmodp@com.ru" to true,
        "asd@amazon.com" to true,
        "asdm.asdas@aop.fi" to true,
    )

    private val passwordMap = hashSetOf(
        "123" to false,
        "12345" to false,
        "123456" to true,
        "1234567909" to true,
        "123456fyuv" to true,
        "qodnasdoaps" to true,
        "ajqpw0asdxzcA9(!)#!(aoda,d" to false,
        "ajqpweqdamsdp12314839190asdxzcA9(!)#!(aoda,ddsadopajd" to false,
        "\uD83D\uDE0A(!)#!(aoda,ddsadopajd" to false,
        "3213\uD83D\uDE0A(1dopajd" to false,
        "dsawqwea#+-#\\_&" to true,
        "12345 6fyuv" to false,
        "asdasdeq213,./#()" to true,
        "21312321\\././,./zxc1023-" to true,
        "21312321\\.    /./,./zxc1023-" to false,
    )

    @Test
    fun emailCheck() {
        for ((email, valid) in emailMap) {
            user.email = email
            assert(user.isEmailValid() == valid)
        }
    }

    @Test
    fun passwordCheck() {
        for ((pass, valid) in passwordMap) {
            user.password = pass
            assert(user.isPasswordValid() == valid)
        }
    }
}