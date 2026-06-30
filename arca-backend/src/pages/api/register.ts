import type { APIRoute } from "astro";
import { createClient } from "@libsql/client";
import bcrypt from "bcrypt";

export const prerender = false;
const corsHeaders = {
    "Access-Control-Allow-Origin": "*", // A porta do seu Frontend
    "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type",
};

export const OPTIONS: APIRoute = async () => {
    return new Response(null, {
        status: 204,
        headers: corsHeaders
    });
};

export const POST : APIRoute = async ({ request }) => {
	try {
		let body;
		try {
			body = await request.json();
		} catch (error) {
			return new Response(
				JSON.stringify({ error: "Corpo da requisição inválido ou vazio." }),
                { status: 400, headers: { "Content-Type": "application/json" }}
			)
		}
		const { nome, email, password } = body;

		if (!nome || !email || !password) {
			return new Response(
				JSON.stringify({ error: "Todos os campos são obrigatorios a serem escritos" }),
				{ status: 400, headers: { "Content-Type": "application/json" }}
			);
		}

		const db = createClient({
			url: import.meta.env.LIBSQL_URL,
			authToken: import.meta.env.LIBSQL_AUTH_TOKEN,
		})
		const ExistinUser = await db.execute({
			sql: "SELECT id FROM users WHERE email = ?",
			args: [email]
		})

		if (ExistinUser.rows.length > 0) {
			return new Response(
				JSON.stringify({ error: "Este email ja ta em uso por uma pessoa" }),
				{ status: 409, headers:{ "Content-Type": "application/json" }}
			)
		}

		const salt = await bcrypt.genSalt(10);
		const hashPassword = await bcrypt.hash(password, salt);

		await db.execute({
			sql: "INSERT INTO users (nome, email, password) VALUES (?, ?, ?)",
			args: [nome, email, hashPassword]
		})

		return new Response(
			JSON.stringify({ message: "Conta criada com sucesso"}),
			{ status: 201, headers: { ...corsHeaders, "Content-Type": "application/json" }}
		)
	} catch (error) {
		console.error("Erro interno no servidor:", error);
        return new Response(
            JSON.stringify({ error: "Erro interno no servidor." }),
            { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
	}
}