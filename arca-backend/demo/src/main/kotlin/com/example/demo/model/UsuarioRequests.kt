package com.example.demo.model

/**
 * Dados recebidos no cadastro. `password` existe somente durante a requisicao
 * e nunca deve ser persistido ou incluido em uma resposta.
 */
data class CadastroUsuarioRequest(
    val nome: String? = null,
    val email: String? = null,
    val password: String? = null,
    val idade: Int? = null
)

/** Credenciais recebidas no login. */
data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)
