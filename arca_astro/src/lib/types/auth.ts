/** Payload enviado pelo formulário de cadastro para POST /api/auth/register */
export interface RegisterPayload {
	nome: string;
	email: string;
	password: string;
}

/** Resposta de sucesso do backend após criar a conta */
export interface RegisterSuccessResponse {
	sucesso: true;
	mensagem: string;
	usuario?: {
		id: string;
		nome: string;
		email: string;
	};
}

/** Resposta de erro do backend (validação ou conflito) */
export interface RegisterErrorResponse {
	sucesso: false;
	mensagem: string;
	erros?: Partial<Record<keyof RegisterPayload, string>>;
}

export type RegisterResponse = RegisterSuccessResponse | RegisterErrorResponse;
