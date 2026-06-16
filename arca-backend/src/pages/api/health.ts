import type { APIRoute } from 'astro';

export const GET: APIRoute = async () => {
    return new Response(
        JSON.stringify({
            status: 'online',
            mensagem: 'Backend da Arca rodando perfeito com astro',
            timestamp: new Date().toISOString()
        }),
        {
            status: 200,
            headers: {
                "Content-Type": "application/json"
            }
        }
    )
}