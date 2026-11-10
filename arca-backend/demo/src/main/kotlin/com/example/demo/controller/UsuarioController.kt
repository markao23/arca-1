package com.example.demo.controller

import com.example.demo.model.CadastroUsuarioRequest
import com.example.demo.model.LoginRequest
import com.example.demo.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*

/**
 * Expoe os endpoints HTTP relacionados ao cadastro de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(private val usuarioService: UsuarioService) {

    /**
     * Cadastra um usuario no Firestore.
     *
     * O frontend Astro envia o JSON para `/api/usuarios`. Em desenvolvimento,
     * o proxy do Astro encaminha essa requisicao para a porta 8080.
     *
     * @param usuario payload desserializado do corpo da requisicao.
     * @return resposta HTTP 200 com a confirmacao da persistencia.
     */
    @PostMapping
    fun criarUsuario(@RequestBody request: CadastroUsuarioRequest): ResponseEntity<Map<String, String>> {
        return try {
            val resposta = usuarioService.salvarUsuario(request)
            ResponseEntity.ok(mapOf("message" to resposta))
        } catch (error: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to (error.message ?: "Dados invalidos.")))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        if (!usuarioService.autenticar(request)) {
            return ResponseEntity.status(401)
                .body(mapOf("message" to "E-mail ou senha invalidos."))
        }

        return ResponseEntity.ok(mapOf("message" to "Login realizado com sucesso."))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun corpoDaRequisicaoInvalido(
        error: HttpMessageNotReadableException
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(
            mapOf(
                "message" to "JSON invalido. Envie email e password como texto.",
                "detail" to (error.mostSpecificCause.message ?: "Corpo da requisicao nao pode ser lido.")
            )
        )
    }
}