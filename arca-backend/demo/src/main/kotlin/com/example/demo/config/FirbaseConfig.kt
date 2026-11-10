package com.example.demo.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

/**
 * Inicializa o Firebase Admin SDK antes dos controllers receberem requisicoes.
 *
 * A autenticacao usa Application Default Credentials (ADC), adequada para
 * desenvolvimento local com `gcloud auth application-default login` e para
 * ambientes de producao com a identidade gerenciada do provedor.
 */
@Configuration
class FirebaseConfig {
    /**
     * Cria a instancia padrao do Firebase uma unica vez.
     *
     * O arquivo `google-services.json` nao e lido aqui porque ele pertence ao
     * aplicativo Android e nao possui credenciais administrativas do servidor.
     *
     * @throws IllegalStateException quando nenhuma credencial ADC esta disponivel.
     */
    @PostConstruct
    fun initFirebase() {
        if (FirebaseApp.getApps().isNotEmpty()) {
            return
        }

        val credentials = try {
            GoogleCredentials.getApplicationDefault()
        } catch (error: Exception) {
            throw IllegalStateException(
                "Credenciais do Google nao encontradas. Execute " +
                    "'gcloud auth application-default login' e tente novamente. " +
                    "Nao use google-services.json: ele e uma configuracao Android.",
                error
            )
        }

        val projectId = System.getenv("GOOGLE_CLOUD_PROJECT")
            ?: System.getenv("FIREBASE_PROJECT_ID")
            ?: "arca-catanduva"

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .setProjectId(projectId)
            .build()
        FirebaseApp.initializeApp(options)
    }
}