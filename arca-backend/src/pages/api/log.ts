import type { APIRoute } from "astro";
import fs from 'fs';
import path from 'path';

export const prerender = false;

export const GET: APIRoute = async () => {
    return new Response(
        JSON.stringify({ status: "sucesso", mensagem: "A rota API esta VIVA e funcionando!" }), 
        { status: 200, headers: { 'Content-Type': 'application/json' } }
    );
}

export const POST: APIRoute = async ({ request }) => {
    try {
        const body = await request.json();
        const timestamp = new Date().toISOString();
        const log = `[${timestamp}] TIPO: ${body.type || 'N/A'} | ERRO: ${body.message || 'N/A'} | URL: ${body.url || 'N/A'} | LINHA: ${body.line || 'N/A'}\n`;

        const logFile = path.join(process.cwd(), 'error.log');
        fs.appendFileSync(logFile, log, 'utf-8');

        return new Response(
            JSON.stringify({ success: true, message: 'Log salvo com sucesso' }),
            {
                status: 200,
                headers: { 'Content-Type': 'application/json' }
            }
        );
    } catch (error) {
        console.error('Falha interna ao escrever o log:', error);
        return new Response(
            JSON.stringify({ success: false, error: 'Erro interno no servidor' }), 
            { 
                status: 500,
                headers: { 'Content-Type': 'application/json' }
            }
        );        
    }
}