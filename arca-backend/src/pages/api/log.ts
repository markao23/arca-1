import type { APIRoute } from "astro";
import fs from 'fs';
import path from 'path';

export const POST: APIRoute = async ({ request }) => {
    try {
        const body = await request.json();
        const timestamp = new Date().toISOString();
        const log = `[${timestamp}] TIPO: ${body.type} | ERRO: ${body.message} | URL: ${body.url} | LINHA: ${body.line}\n`;
        const logFile = path.join(process.cwd(), 'error.log');

        fs.appendFileSync(logFile, log, 'utf-8');

        return new Response(
            JSON.stringify({ success: true, message: 'Log salvo'}),
            { status: 200 })
    } catch (error) {
        console.error('Falha ao escrever o log:', error);
        return new Response(JSON.stringify({ success: false, error: 'Erro no servidor' }), { status: 500 });
    }
}