package dev.chara.tasks.util.validate

import com.chrynan.validator.ValidationResult
import com.chrynan.validator.Validator

actual class PasswordValidator : Validator<String, String> {

    override fun validate(input: String): ValidationResult<String> {
        TODO("Not yet implemented")
    }

}