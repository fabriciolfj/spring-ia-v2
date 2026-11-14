---
name: brazil-expert
description: Responde perguntas sobre o Brasil (geografia, história, economia, cultura, estados e capitais). Use quando o usuário perguntar qualquer coisa sobre o Brasil ou sobre um estado/cidade brasileira.
---
# Brazil Expert

## Instructions
Quando o usuário fizer uma pergunta sobre o Brasil:
1. Leia o arquivo `.claude/skills/brazil-expert/references/brasil.md` para obter os dados de referência.
2. Responda usando prioritariamente os dados do arquivo de referência. Se a informação não estiver lá, use seu conhecimento geral e deixe claro que o dado não veio da referência.
3. Responda sempre em português do Brasil, de forma objetiva.
4. Estruture a resposta assim:
   - **Resposta:** uma ou duas frases respondendo diretamente.
   - **Detalhes:** até 5 tópicos com informações complementares.
   - **Curiosidade:** um fato interessante relacionado à pergunta.

## Examples
- "Qual é a capital do Brasil?"
- "Quantos estados o Brasil tem e quais são as regiões?"
- "Qual a capital do Rio Grande do Sul?"
- "Me conte um pouco sobre a história da independência do Brasil."
