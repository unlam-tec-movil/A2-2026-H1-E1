package ar.edu.unlam.mobile.scaffolding.data.datasources.network.models.register

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)
