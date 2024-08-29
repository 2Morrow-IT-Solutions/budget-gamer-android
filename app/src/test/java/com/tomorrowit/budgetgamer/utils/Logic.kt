package com.tomorrowit.budgetgamer.utils

enum class FieldType {
    Name,
    Email,
    Password
}

object Logic {

    fun isFieldCorrect(fieldType: FieldType, value: String): Boolean {
        return when (fieldType) {
            FieldType.Name -> value.isNotEmpty()
            FieldType.Email -> value.isValidEmail()
            FieldType.Password -> value.isValidPassword()
        }
    }

    private fun String.isValidEmail(): Boolean {
        // Basic email regex to check validity
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        // Password validation: at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, and 1 special character
        val passwordPattern =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}\$")
        return this.matches(passwordPattern)
    }
}