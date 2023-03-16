package dev.chara.tasks.util.validate

import com.chrynan.validator.ValidationError
import com.chrynan.validator.Validator

expect class PasswordValidator() : Validator<String, String>

sealed class PasswordValidationError(override val details: String? = null) : ValidationError {

    object InputIsBlank : PasswordValidationError(details = "Password must not be blank")
    object InputTooLong :
        PasswordValidationError(details = "Password must be less than 50 characters")

    class PasswordTooWeak(details: String?) : PasswordValidationError(details = details)
}