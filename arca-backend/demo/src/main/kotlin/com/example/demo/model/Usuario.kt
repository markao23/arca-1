package com.example.demo.model

/**
 * Dados persistidos de um usuario no Firestore.
 *
 * A senha nunca e armazenada neste objeto em texto puro. O campo persistido
 * `senhaHash` contem um hash BCrypt, que nao pode ser revertido para a senha.
 *
 * @property nome nome completo do usuario.
 * @property email endereco de e-mail do usuario.
 * @property idade idade informada pelo usuario.
 */
data class Usuario(
    /** Nome completo do usuario. */
    val nome: String = "",
    /** Endereco de e-mail normalizado do usuario. */
    val email: String = "",
    /** Hash BCrypt da senha do usuario. */
    val senhaHash: String = "",
    /** Idade do usuario em anos, quando informada. */
    val idade: Int? = null
)