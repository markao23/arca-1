package com.example.demo.service

import com.example.demo.model.CadastroUsuarioRequest
import com.example.demo.model.LoginRequest
import com.example.demo.model.Usuario
import com.google.firebase.cloud.FirestoreClient
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Contem as regras de persistencia dos usuarios no Firestore.
 */
@Service
class UsuarioService(private val passwordEncoder: PasswordEncoder) {
    /**
     * Cria um identificador para o usuario e salva seus dados na colecao
     * `usuarios`.
     *
     * A operacao e sincrona: o metodo retorna somente depois que o Firestore
     * confirma a escrita.
     *
     * @param usuario dados que serao persistidos.
     * @return mensagem contendo o identificador gerado e o horario da escrita.
     * @throws IllegalStateException se o Firebase ainda nao estiver inicializado.
     */
    fun salvarUsuario(request: CadastroUsuarioRequest): String {
        val nome = request.nome?.trim().orEmpty()
        val email = request.email?.trim()?.lowercase().orEmpty()
        val password = request.password.orEmpty()

        require(nome.length >= 2) { "O nome deve ter pelo menos 2 caracteres." }
        require(EMAIL_REGEX.matches(email)) { "Informe um e-mail valido." }
        require(password.length >= 8) {
            "A senha deve ter pelo menos 8 caracteres."
        }

        val db = FirestoreClient.getFirestore()
        val emailJaCadastrado = db.collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .get()
            .documents
            .isNotEmpty()

        require(!emailJaCadastrado) { "Este e-mail ja esta cadastrado." }

        val idUser = UUID.randomUUID().toString()
        val docRef = db.collection("usuarios").document(idUser)
        val usuario = Usuario(
            nome = nome,
            email = email,
            senhaHash = passwordEncoder.encode(password)
                ?: error("Nao foi possivel gerar o hash da senha."),
            idade = request.idade
        )
        val res = docRef.set(usuario).get()

        return "Usuario salvo com sucesso no Id: $idUser em ${res.updateTime}"
    }

    /**
     * Verifica a senha contra o hash armazenado. Hashes BCrypt nao sao
     * descriptografados: a senha informada e comparada pelo encoder.
     */
    fun autenticar(request: LoginRequest): Boolean {
        val email = request.email?.trim()?.lowercase().orEmpty()
        val password = request.password.orEmpty()
        if (!EMAIL_REGEX.matches(email) || password.isEmpty()) {
            return false
        }

        val usuario = FirestoreClient.getFirestore()
            .collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .get()
            .documents
            .firstOrNull()
            ?.toObject(Usuario::class.java)

        return usuario != null &&
            usuario.senhaHash.isNotBlank() &&
            passwordEncoder.matches(password, usuario.senhaHash)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}