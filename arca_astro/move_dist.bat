@echo off
setlocal

:: ============================================================================
:: Configurações -  MODIFIQUE ESTAS VARIÁVEIS DE ACORDO COM SUAS NECESSIDADES
:: ============================================================================

:: Caminho completo do arquivo de origem (o arquivo que você quer mover).
:: Use aspas se o caminho tiver espaços.  Exemplo: "C:\Minha Pasta\meu arquivo.txt"
set "ARQUIVO_ORIGEM=C:\Users\Markus\OneDrive\Desktop\site_arca\dist"

:: Caminho completo da pasta de destino (para onde você quer mover o arquivo).
:: Use aspas se o caminho tiver espaços.  Exemplo: "D:\Meus Documentos\Arquivos Movidos"
set "PASTA_DESTINO=C:\Users\Markus\OneDrive\Desktop\site_arca\beckend"

:: (Opcional) Novo nome para o arquivo no destino.  Se deixar em branco, o nome original será mantido.
:: Use aspas se o nome tiver espaços.  Exemplo: "novo nome.txt"
set "NOVO_NOME="

:: (Opcional) Sobrescrever o arquivo de destino se ele já existir?
::   - 1: Sobrescrever SEM perguntar (CUIDADO!)
::   - 0: NÃO sobrescrever (o script irá falhar se o arquivo existir)
::   - Qualquer outra coisa: Perguntar ao usuário se deve sobrescrever (recomendado)
set "SOBRES_SE_EXISTIR=2"

:: (Opcional) Criar a pasta de destino se ela não existir?
::   - 1: Criar a pasta de destino
::   - 0: NÃO criar a pasta (o script irá falhar se a pasta não existir)
set "CRIAR_PASTA_DESTINO=1"

:: (Opcional) Exibir mensagens de erro detalhadas?
::  - 1: Sim (Recomendado para Debug)
::  - 0: Não
set "EXIBIR_ERROS=1"

:: (Opcional) Adicionar log?
::   - 1: Sim
::   - 0: Não.
set "LOGGING=1"

::(Opcional) Pasta de log
set "LOG_DIR=C:\Users\%USERNAME%\logs"

:: ============================================================================
::  FIM DAS CONFIGURAÇÕES - NÃO MODIFIQUE O CÓDIGO ABAIXO (a menos que saiba o que está fazendo)
:: ============================================================================

:: Verifica se o arquivo de origem existe
if not exist "%ARQUIVO_ORIGEM%" (
    if "%EXIBIR_ERROS%"=="1" echo ERRO: Arquivo de origem nao encontrado: "%ARQUIVO_ORIGEM%"
	if "%LOGGING%"=="1" (
	   if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
	   echo %DATE% %TIME% - ERRO: Arquivo de origem nao encontrado: "%ARQUIVO_ORIGEM%" >> "%LOG_DIR%\move_arquivo_log.txt"
	)
    goto :fim
)

:: Cria a pasta de destino, se necessário e se a opção estiver habilitada
if not exist "%PASTA_DESTINO%" (
    if "%CRIAR_PASTA_DESTINO%"=="1" (
        mkdir "%PASTA_DESTINO%"
        if errorlevel 1 (  :: errorlevel verifica o código de saída do comando anterior (mkdir)
            if "%EXIBIR_ERROS%"=="1" echo ERRO: Nao foi possivel criar a pasta de destino: "%PASTA_DESTINO%"
			if "%LOGGING%"=="1" echo %DATE% %TIME% - ERRO: Nao foi possivel criar a pasta de destino:  "%PASTA_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"
            goto :fim
        )
    ) else (
        if "%EXIBIR_ERROS%"=="1" echo ERRO: Pasta de destino nao encontrada: "%PASTA_DESTINO%"
		if "%LOGGING%"=="1" echo %DATE% %TIME% - ERRO: Pasta de destino nao encontrada: "%PASTA_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"
        goto :fim
    )
)

:: Define o caminho completo do arquivo de destino
if "%NOVO_NOME%"=="" (
    set "ARQUIVO_DESTINO=%PASTA_DESTINO%\%~nx1"  :: %~nx1 extrai o nome e a extensão do arquivo de origem
) else (
    set "ARQUIVO_DESTINO=%PASTA_DESTINO%\%NOVO_NOME%"
)

:: Verifica se o arquivo de destino já existe e age de acordo com a configuração
if exist "%ARQUIVO_DESTINO%" (
    if "%SOBRES_SE_EXISTIR%"=="1" (
        echo Sobrescrevendo arquivo existente: "%ARQUIVO_DESTINO%"
		if "%LOGGING%"=="1" echo %DATE% %TIME% - Sobrescrevendo arquivo existente: "%ARQUIVO_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"
        move /Y "%ARQUIVO_ORIGEM%" "%ARQUIVO_DESTINO%" > nul  :: /Y sobrescreve sem perguntar.  > nul redireciona a saída padrão (para não mostrar a mensagem "1 arquivo(s) movido(s).")
    ) else if "%SOBRES_SE_EXISTIR%"=="0" (
        if "%EXIBIR_ERROS%"=="1" echo ERRO: Arquivo de destino ja existe: "%ARQUIVO_DESTINO%"
		if "%LOGGING%"=="1" echo %DATE% %TIME% - ERRO: Arquivo de destino ja existe: "%ARQUIVO_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"
        goto :fim
    ) else (
        choice /M "O arquivo de destino ja existe. Deseja sobrescrever (S/N)?"  :: /M define a mensagem.
        if errorlevel 2 (  :: errorlevel 2 significa que o usuário pressionou 'N'
            echo Operacao cancelada pelo usuario.
			if "%LOGGING%"=="1" echo %DATE% %TIME% - Operacao cancelada pelo usuario. >> "%LOG_DIR%\move_arquivo_log.txt"
            goto :fim
        ) else if errorlevel 1 ( :: errorlevel 1 significa que o usuário pressionou 'S'
              echo Sobrescrevendo arquivo existente: "%ARQUIVO_DESTINO%"
			  if "%LOGGING%"=="1" echo %DATE% %TIME% - Sobrescrevendo arquivo existente: "%ARQUIVO_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"
              move /Y "%ARQUIVO_ORIGEM%" "%ARQUIVO_DESTINO%" > nul
        ) else (
          if "%EXIBIR_ERROS%"=="1" echo ERRO: Resposta invalida.
		  if "%LOGGING%"=="1" echo %DATE% %TIME% - ERRO: Resposta invalida. >> "%LOG_DIR%\move_arquivo_log.txt"
          goto :fim
        )
    )
) else (
    move "%ARQUIVO_ORIGEM%" "%ARQUIVO_DESTINO%" > nul
)

:: Verifica se o comando 'move' foi bem-sucedido
if errorlevel 1 (
    if "%EXIBIR_ERROS%"=="1" echo ERRO: Falha ao mover o arquivo.
	if "%LOGGING%"=="1" echo %DATE% %TIME% - ERRO: Falha ao mover o arquivo. >> "%LOG_DIR%\move_arquivo_log.txt"
    goto :fim
)

:: Mensagem de sucesso (se chegou até aqui, o arquivo foi movido)
echo Arquivo movido com sucesso para: "%ARQUIVO_DESTINO%"
if "%LOGGING%"=="1" echo %DATE% %TIME% - Arquivo movido com sucesso para: "%ARQUIVO_DESTINO%" >> "%LOG_DIR%\move_arquivo_log.txt"

:fim
endlocal
pause