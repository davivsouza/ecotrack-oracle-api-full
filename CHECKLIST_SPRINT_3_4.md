# Checklist — Sprint 3 e Sprint 4 (Java Advanced)

Este arquivo resume, de forma objetiva, **o que já foi feito**, **o que está parcial** e **o que ainda falta** no backend do projeto EcoTrack.

---

## Sprint 3 — Java Advanced

### Frontend (30 pontos)
- [ ] Frontend neste repositório.
  - **Status:** não se aplica aqui, porque o frontend oficial ficou em outro projeto/repositório.
- [x] Backend preparado para integração com frontend externo.
  - Endpoints REST organizados para produtos, usuários, impacto, nutrição, histórico e favoritos.

### Flyway (20 pontos)
- [x] Flyway configurado no projeto.
- [x] Migration inicial de criação do schema.
- [x] Migration de carga de dados seed para demonstração.
- [x] Estrutura reproduzível para ambiente local com H2.

### Spring Security (30 pontos)
- [x] Spring Security adicionado ao backend.
- [x] Autenticação carregando usuários do banco.
- [x] Dois tipos de usuário com permissões diferentes.
  - `ROLE_ADMIN`
  - `ROLE_CONSUMER`
- [x] Proteção de rotas com base no perfil do usuário.
- [x] Regras de acesso definidas para endpoints administrativos e de escaneamento.

### Funcionalidades completas (20 pontos)
- [x] Fluxo completo de usuário autenticável no backend.
  - cadastro/gestão de usuário com role e validações.
- [x] Fluxo completo de escaneamento.
  - registro de scan no histórico.
- [x] Fluxo completo de favoritos.
  - adicionar, listar e remover favoritos.
- [x] Validações básicas em entidades, serviços e requests.
- [~] Fluxos completos integrados ao frontend final.
  - **Status:** dependem da integração com o frontend separado.

### Penalidades — situação atual
- [x] Código sem evidência atual de frontend duplicado neste repositório.
- [x] Separação razoável entre camadas Controller / Service / Repository.
- [~] Ainda pode melhorar em DTOs e encapsulamento de respostas.
- [~] Ainda pode melhorar em cobertura de testes automatizados.

---

## Sprint 4 — Java Advanced

### Demonstração técnica da solução (40 pontos)
- [~] Aplicação preparada para rodar localmente.
- [~] Aplicação preparada para profile Oracle.
- [ ] Aplicação rodando online em deploy público.
  - **Status:** ainda falta publicar.
- [x] Backend pronto para navegação dos principais fluxos via API.
- [~] Boa UI/UX neste repositório.
  - **Status:** não se aplica diretamente aqui, pois o frontend está em outro projeto.

### Narrativa da solução (20 pontos)
- [x] README atualizado com escopo do backend.
- [x] Explicação do papel do backend na solução.
- [x] Documentação para apresentação oral.
- [x] Justificativas técnicas para segurança, Flyway e integração.

### Integração multidisciplinar (20 pontos)
- [~] Backend preparado para sustentar a solução integrada do grupo.
- [~] Evidências multidisciplinares dependem de materiais complementares do grupo.
  - ex.: vídeo, documentação geral, protótipos, canvas e frontend final.

### Apresentação oral e comunicação em equipe (10 pontos)
- [x] Arquivo de apoio para fala na avaliação oral.
- [~] Participação de todos no vídeo depende da organização do grupo.

### Organização da entrega e documentação (10 pontos)
- [x] README atualizado.
- [x] Checklist de status criado.
- [x] Arquivo de pendências e próximos passos criado.
- [~] Link público da aplicação ainda depende de deploy.

---

## Resumo executivo

### Já feito
- [x] Backend REST estruturado.
- [x] Spring Security com roles.
- [x] Flyway com migrations.
- [x] Dados seed para demo.
- [x] H2 local + profile Oracle.
- [x] Fluxos de scan, histórico e favoritos.
- [x] Documentação para README e avaliação oral.

### Parcial
- [~] Integração completa com frontend final.
- [~] Evidências completas da entrega final multidisciplinar.
- [~] Testes automatizados mais amplos.

### Falta concluir
- [ ] Deploy público da aplicação.
- [ ] Consolidação final com frontend do grupo.
- [ ] Vídeo final com apresentação completa da solução.
