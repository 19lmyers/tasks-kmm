package dev.chara.tasks.android.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class NavTarget : Parcelable {

    @Parcelize
    object Welcome : NavTarget()

    /**
     * TODO make this one object with multiple Builder style constructors?
     */
    sealed class Home : NavTarget() {

        @Parcelize
        object Default : Home()

        @Parcelize
        class WithList(val listId: String) : Home()

        @Parcelize
        class WithTask(val taskId: String) : Home()
    }

    @Parcelize
    object Profile : NavTarget()

    @Parcelize
    object Settings : NavTarget()

    // ChangeEmail

    // ValidateEmail

    @Parcelize
    object ChangePassword : NavTarget()

    @Parcelize
    object SignIn : NavTarget()

    @Parcelize
    object SignUp : NavTarget()

    @Parcelize
    object ForgotPassword : NavTarget()

    @Parcelize
    class ResetPassword(val resetToken: String) : NavTarget()
}