# SimulacaoElevador

## Descrição

O projeto **SimulacaoElevador** é uma simulação completa do funcionamento de um sistema de elevadores em um prédio. O objetivo é modelar, de forma programática, o comportamento de elevadores, pessoas, andares e um sistema central de controle, permitindo a análise de desempenho (tempo de espera, consumo de energia, etc.) em diferentes cenários e configurações.

## Funcionalidades Principais

- Simulação do fluxo de pessoas em um prédio com múltiplos andares e elevadores.
- Criação dinâmica de andares, elevadores e pessoas por hora.
- Sistema central de controle para distribuição dinâmica das chamadas de elevadores.
- Coleta de estatísticas como tempo de espera, tempo de viagem, energia gasta e número de pessoas atendidas.
- Algoritmo para atribuição eficiente das chamadas aos elevadores, considerando fatores como direção, ocupação e estado do elevador.
- Relatórios periódicos (por minuto e hora simulada) das estatísticas do sistema.

## Arquitetura

O sistema é orientado a objetos e composto pelos seguintes principais componentes:

- **Predio**: Responsável por criar os andares e elevadores, gerar pessoas, armazenar dados globais e atualizar o estado do sistema a cada minuto simulado.
- **CentralDeControle**: Gerencia a lógica principal de distribuição de chamadas de elevador, inicialização dos elevadores, verificação de horários de pico e coleta de estatísticas.
- **Elevador**: Modelo de elevador individual, com controle de passageiros embarcados/desembarcados, energia consumida, direção, etc.
- **Andar**: Representa um andar do prédio, incluindo a fila de pessoas aguardando.
- **Listas e Nodes**: Estruturas de dados auxiliares para listas de elevadores, andares, chamadas, passageiros, etc.

## Fluxo de Simulação

1. O prédio é inicializado com um número definido de andares e elevadores.
2. A cada minuto simulado:
   - Novas pessoas podem ser geradas para entrar no prédio.
   - O sistema central atualiza os elevadores, distribui chamadas, coleta e atualiza estatísticas.
   - Elevadores atendem chamadas, embarcam/desembarcam passageiros e registram consumo de energia.
3. Ao final de cada hora, as estatísticas são registradas e listas temporárias (como passageiros desembarcados) são limpas.

## Principais Classes

- `Predio`: Cria e gerencia os andares, elevadores e estatísticas globais.
- `CentralDeControle`: Implementa a lógica de distribuição de chamadas e coleta de estatísticas.
- `Elevador`: Representa um elevador e suas operações.
- `Andar`: Gerencia as pessoas que aguardam o elevador em cada andar.

## Exemplo de Uso

```java
// Inicializa um prédio com 10 andares e 3 elevadores
Predio predio = new Predio(10, 3);

// Simula 8 horas (480 minutos)
for (int minuto = 0; minuto < 480; minuto++) {
    predio.atualizar(minuto);
}
```

## Possíveis Expansões

- Implementação de diferentes heuristicas para os elevadores.
- Interface gráfica para visualização da simulação.
- ## Requisitos

- Java 8 ou superior

## Autores

- Philip Dantas & Marco Melo
