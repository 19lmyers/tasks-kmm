package dev.chara.tasks.android.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class RootNavTarget : Parcelable {

    @Parcelize
    object Welcome : RootNavTarget()

    /**
     * TODO make this one object with multiple Builder style constructors?
     */
    sealed class Home : RootNavTarget() {

        @Parcelize
        object Default : Home()

        @Parcelize
        class WithList(val listId: String) : Home()

        @Parcelize
        class WithTask(val taskId: String) : Home()
    }

    @Parcelize
    object Settings : RootNavTarget()

    // ChangeEmail

    // ValidateEmail

    @Parcelize
    object ChangePassword : RootNavTarget()

    @Parcelize
    object SignIn : RootNavTarget()

    @Parcelize
    object SignUp : RootNavTarget()

    @Parcelize
    object ForgotPassword : RootNavTarget()

    @Parcelize
    class ResetPassword(val resetToken: String) : RootNavTarget()
}