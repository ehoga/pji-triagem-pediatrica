# Guia de Contribuição

Regras para todos os colaboradores do projeto.

## Branches

| Branch | Propósito |
|---|---|
| `main` | Código estável, pronto para release. Protegida — só recebe merge de `dev` via PR. |
| `dev` | Branch de integração. Recebe PRs das branches de trabalho. |
| `feature/<nome>` | Nova funcionalidade. Ex: `feature/login-tela` |
| `fix/<nome>` | Correção de bug. Ex: `fix/calculo-idade` |
| `chore/<nome>` | Tarefas de manutenção, configs, docs. Ex: `chore/atualiza-readme` |
| `refactor/<nome>` | Refatorações sem mudança de comportamento. |

## Fluxo padrão

1. **Atualize sua `dev` local:**
   ```bash
   git checkout dev
   git pull origin dev
   ```
2. **Crie sua branch de trabalho a partir de `dev`:**
   ```bash
   git checkout -b feature/minha-task
   ```
3. **Trabalhe, commitando com frequência** (commits pequenos e descritivos).
4. **Suba sua branch:**
   ```bash
   git push -u origin feature/minha-task
   ```
5. **Abra um Pull Request** com base em `dev`.
6. **Marque um revisor.** O PR só pode ser mergeado após **1 aprovação**.
7. Após o merge, **delete a branch** (botão no GitHub ou `git push origin --delete feature/minha-task`).

`main` só recebe PRs vindos de `dev` (release/sync).

## Padrão de commits — Conventional Commits

Formato: `<tipo>(<escopo opcional>): <descrição curta>`

Tipos mais comuns:
- `feat`: nova funcionalidade
- `fix`: correção de bug
- `docs`: documentação
- `style`: formatação (sem mudar lógica)
- `refactor`: refatoração
- `test`: testes
- `chore`: manutenção, deps, config

Exemplos:
```
feat(triagem): adiciona tela de classificação de risco
fix(api): corrige cálculo de IMC para crianças
docs: atualiza README com instruções de setup
chore(backend): atualiza Spring Boot para 3.4.2
```

## Padrão de Pull Request

Título do PR usa o mesmo formato dos commits.

Inclua na descrição:
- **O que** foi feito
- **Por que** foi feito (motivação / issue relacionada)
- **Como testar** (passos para o revisor)
- **Screenshots** se houver mudança visual
- Issues relacionadas: `Closes #123`

## Regras de revisão

- Revise código de outros antes de pedir revisão no seu (cultura de time).
- Seja objetivo e respeitoso nos comentários.
- Sugira, não imponha — exceto em problemas claros (bug, vulnerabilidade).
- Aprove apenas quando entender o que foi feito.

## Antes de abrir o PR — checklist

- [ ] Código compila/roda localmente
- [ ] Sem `console.log`, `System.out.println`, ou TODOs esquecidos
- [ ] Sem credenciais/secrets commitados
- [ ] Branch atualizada com `dev` (rebase ou merge)
