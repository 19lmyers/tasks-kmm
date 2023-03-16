package dev.chara.tasks.util.validate

import com.chrynan.validator.Invalid
import com.chrynan.validator.Valid
import com.chrynan.validator.ValidationResult
import com.chrynan.validator.Validator
import me.gosimple.nbvcxz.Nbvcxz

actual class PasswordValidator : Validator<String, String> {
    override fun validate(input: String): ValidationResult<String> {
        if (input.isBlank()) return Invalid(PasswordValidationError.InputIsBlank)

        if (input.count() > 50) return Invalid(PasswordValidationError.InputTooLong)

        val result = nbvcxz.estimate(input)

        if (!result.isMinimumEntropyMet)
            return Invalid(PasswordValidationError.PasswordTooWeak(result.feedback.warning))

        return Valid(input)
    }

    companion object {
        val nbvcxz = Nbvcxz()
    }
}