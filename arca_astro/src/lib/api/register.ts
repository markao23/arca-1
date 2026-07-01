import type { RegisterPayload, RegisterResponse } from "../types/auth"

export function buildRegisterPayload(from: HTMLFormElement): RegisterPayload {
	const data = new FormData(from);

	return {
		nome: String(data.get('nome') ?? '').trim(),
		email: String(data.get('email') ?? '').trim().toLowerCase(),
		password: String(data.get('password') ?? ''),
	};
}

export function validateRegister(payload: RegisterPayload): Partial<Record<keyof RegisterPayload, string>> {
	const erros: Partial<Record<keyof RegisterPayload, string>> = {};

	const regras = {
		nome: payload.nome.length < 2 ? 'Informe o nome completo da pessoa': null,
		email: !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(payload.email) ? 'informe um email valido': null,
		password: payload.password.length < 6 ? 'A senha tem que ter 6 caracteres': null
	};

	Object.entries(regras).forEach(([campo, mensagem]) => {
		if (mensagem) {
			erros[campo as keyof RegisterPayload] = mensagem;
		}
	});

	return erros;
}

export async function Register(payload: any) {
	try {
		const res = await fetch('http://localhost:4322/api/register', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(payload),
		});

		if (!res.ok) {
			const text = await res.text();
			console.error('O servidor devolveu um erro', text);

			return { 
				sucesso: false, 
				mensagem: "Erro de comunicação com o servidor. Verifique a rota." 
			};
		}

		const data = await res.json();
		return {
            sucesso: true,
            mensagem: data.message || 'Conta criada com sucesso!',
        };
	} catch (error) {
		console.error("Erro na requisição Fetch:", error);
        return {
            sucesso: false,
            mensagem: 'Erro de conexão com o servidor.',
        };
	}
}