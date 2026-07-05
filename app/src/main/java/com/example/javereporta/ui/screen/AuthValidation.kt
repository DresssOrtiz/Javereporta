package com.example.javereporta.ui.screen

object AuthValidation {
    private val specialCharacters = """!@#$%^&*()_+-={}[]|\:;"'<>,.?/~`""".toSet()

    fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()
        val atIndex = trimmedEmail.indexOf('@')
        val atCount = trimmedEmail.count { it == '@' }
        val dotAfterAtIndex = trimmedEmail.indexOf('.', startIndex = atIndex + 1)
        val domain = if (atIndex >= 0 && dotAfterAtIndex > atIndex) {
            trimmedEmail.substring(atIndex + 1, dotAfterAtIndex)
        } else {
            ""
        }
        val extension = if (dotAfterAtIndex >= 0 && dotAfterAtIndex < trimmedEmail.lastIndex) {
            trimmedEmail.substring(dotAfterAtIndex + 1)
        } else {
            ""
        }

        return when {
            trimmedEmail.isBlank() -> "Ingresa tu email."
            atIndex <= 0 -> "El email debe incluir un @ válido."
            atCount > 1 -> "El email debe incluir un solo @."
            dotAfterAtIndex < 0 -> "El email debe incluir un punto después del dominio."
            domain.isBlank() -> "El email debe incluir un dominio, como por ejemplo @gmail."
            extension.isBlank() -> {
                "El email debe incluir una extensión de dominio, como por ejemplo .com."
            }
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Ingresa tu contraseña."
            password.none { it.isUpperCase() } -> "La contraseña debe tener al menos una mayúscula."
            password.none { it in specialCharacters } -> {
                "La contraseña debe tener al menos un símbolo especial."
            }
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirma tu contraseña."
            confirmPassword != password -> "Las contraseñas no coinciden."
            else -> null
        }
    }
}
