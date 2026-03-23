# Roteiro para a avaliação oral

## Como explicar o recorte atual

Você pode dizer que o grupo separou responsabilidades entre frontend e backend e que este repositório representa **o backend oficial da solução**, com foco em API, segurança, banco e regras de negócio.

## Pontos principais para explicar

### 1. Spring Security
- Existem dois perfis: `ROLE_ADMIN` e `ROLE_CONSUMER`.
- O controle de acesso é feito por rota.
- Produtos, usuários, impacto e nutrição ficaram restritos a admin.
- Escaneamento e favoritos podem ser usados por admin e consumer.

### 2. Flyway
- O banco é criado por migration, evitando criação manual.
- Isso facilita reproduzir o ambiente e justificar versionamento do schema.

### 3. Fluxos completos entregues no backend
- fluxo de usuário autenticável;
- fluxo de escaneamento com histórico;
- fluxo de favoritos;
- regras de conflito e validação.

### 4. Integração com frontend separado
- O backend foi mantido desacoplado para atender tanto frontend web quanto possíveis clientes mobile.
- A autenticação e os dados ficam centralizados aqui.

## Perguntas prováveis

### “Por que o frontend não está aqui?”
> Porque o frontend oficial do projeto está em outro lugar. Este repositório foi ajustado para concentrar o backend, mantendo API, segurança, banco e regras de negócio organizados.

### “Como vocês atendem o requisito de segurança?”
> Com Spring Security, autenticação via banco, dois perfis e rotas protegidas por papel.

### “Como o banco é controlado?”
> Com Flyway, usando migrations versionadas e dados seed para demo.

### “Quais fluxos completos podem ser demonstrados?”
> Cadastro/gestão de usuários autenticáveis e o fluxo de escaneamento com histórico e favoritos.
