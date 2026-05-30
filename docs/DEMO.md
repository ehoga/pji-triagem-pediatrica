# Roteiro de demonstração — PediTriagem

Tempo alvo: 10-15 minutos.

## Pré-check antes da banca

- Backend rodando em `http://localhost:8080` ou IP local da máquina de apresentação.
- Frontend iniciado com `npm start` dentro de `frontend/`.
- Expo Go instalado no celular de teste.
- Celular e notebook na mesma rede Wi-Fi.
- Se for usar build TestFlight, gerar com `EXPO_PUBLIC_API_URL=http://<IP-LAN>:8080`.
- Abrir o app uma vez antes da apresentação para validar fonte, splash e login.

## Dados de teste

Usuário demo:

- Email: `camila.demo@peditriagem.app`
- Senha: `peditriagem123`

Crianças pré-cadastradas no mock:

- Maria, 4 anos, 17 kg
- Lucas, 8 meses, 8.2 kg

## Roteiro principal

1. Abertura e contexto (1 min)
   - Mostrar splash/ícone do app.
   - Explicar que o PediTriagem apoia decisão inicial e não substitui consulta médica.

2. Login e persistência de sessão (1-2 min)
   - Entrar com o usuário demo.
   - Mostrar que o app cai direto nas abas autenticadas.
   - Comentar que o `AuthContext` valida sessão no boot e limpa token inválido.

3. Tela inicial e criança selecionada (1 min)
   - Mostrar Maria selecionada.
   - Alternar para Lucas.
   - Mostrar botão de cadastro de criança, sem gastar tempo preenchendo se não for necessário.

4. Cenário verde: baixo risco (2 min)
   - Abrir `Avaliar sintomas`.
   - Escolher `Febre`.
   - Respostas sugeridas:
     - Abaixo de 37,8°C
     - Menos de 24 horas
     - Não está prostrada
     - Não há dificuldade para respirar
     - Está bebendo líquidos normalmente
     - Não há manchas que não somem ao apertar
   - Resultado esperado: `Baixo risco`.
   - Mostrar orientações de cuidado em casa.

5. Cenário amarelo: risco moderado (2 min)
   - Repetir triagem para Maria ou Lucas.
   - Respostas sugeridas:
     - Entre 38,5 e 39,5°C
     - 1 a 3 dias
     - Não está prostrada
     - Não há dificuldade para respirar
     - Não está bebendo líquidos normalmente
     - Não há manchas que não somem ao apertar
   - Resultado esperado: `Risco moderado`.
   - Destacar orientação de procurar avaliação em até 24h.

6. Cenário vermelho com red flag (2 min)
   - Repetir triagem para `Febre`.
   - Respostas sugeridas:
     - Acima de 39,5°C
     - Mais de 3 dias
     - Sim, está prostrada
     - Sim, há dificuldade para respirar
     - Não está bebendo líquidos normalmente
     - Sim, manchas não somem ao apertar
   - Resultado esperado: `Alto risco`.
   - Destacar CTA de emergência e ligação para SAMU 192.

7. Histórico, orientações e perfil (2 min)
   - Abrir aba `Histórico` e filtrar por criança.
   - Abrir aba `Orientações`.
   - Abrir `Perfil`, mostrar crianças, `Sobre o aplicativo` e `Storybook visual`.

8. Encerramento técnico (1-2 min)
   - Mostrar organização em `src/theme`, `src/components`, `src/services`, `src/mocks`, `src/navigation` e `src/screens`.
   - Mostrar `docs/openapi.yaml` como contrato do MVP.
   - Comentar que mocks estão atrás de services e podem ser trocados pela API real.

## Plano B

- Gravar um vídeo curto do fluxo principal antes da apresentação.
- Salvar capturas das telas de login, home, baixo risco, moderado, alto risco, histórico e perfil.
- Ter o backend validado via Postman/cURL para `/health`.
- Se a rede local falhar, demonstrar o frontend com mock (`EXPO_PUBLIC_USE_MOCK_AUTH=true`) e explicar a troca por API via `EXPO_PUBLIC_API_URL`.
- Se TestFlight falhar, usar Expo Go no celular ou web com `npm run web`.

## Checklist de ensaio

- [ ] Um integrante executou o roteiro completo em 10-15 minutos.
- [ ] Os três cenários de risco foram conferidos.
- [ ] O vídeo de backup foi gravado.
- [ ] O IP local do backend foi confirmado na rede da apresentação.
- [ ] O QR do Expo Go ou link TestFlight está acessível.
