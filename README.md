# senhas-back
Backend do Gerenciador de Senhas.

## Passo a passo

Este projeto está disponível para vizualização em https://jjj-senhas-front.herokuapp.com, não é necessário baixá-lo para executar.
__Aguarde 30 segundos caso a página não carregar, o Heroku pode estar inicializando o aplicativo.__

### `Executar manualmente`

Se você quiser rodar o app na sua máquina manualmente, você deve primeiro baixar o código e extrair pra uma pasta.
Ou, se preferir, importe o código pela linha de comando Git com __git clone https://github.com/JoeyJohnJo/senhas-back.git__.
Após ter todos os arquivos em uma pasta, basta rodar através da sua IDE de escolha ou com __mvn install__. É necessário ter 
o Maven para baixar todas as dependências do projeto. Você também precisará do código daqui __https://github.com/JoeyJohnJo/senhas-back__
para rodar o backend.

### `Funcionalidade`
Este projeto expõe REST endpoints para gerar novas senhas e servi-las quando for solicitado.
As senhas devem ser servidas dando prioridade às senhas preferenciais. Para isso, a classe __Password__
implementa __Comparator__ para colocar as senhas em uma fila, retirando da fila primeiro as preferenciais, em ordem numérica.
Foi criado um usuário inMemory para separar o gerente dos outros usuários comuns que apenas acompanharão a chamada das senhas.
Não há segurança para o papel de gerente, mas caso queira adicionar, é necessário somente modificar o nome de usuário e senha na
classe __AccessConfig__, que é utilizada para ditar quais endpoints são bloqueados e quais são de livre acesso.

Para alertar os clientes comuns e atualizar a última senha chamada, foi utilizado __Spring Webflux__ para a emissão de SSEs.
Os SSEs são gerados a partir da classe __PasswordManager__ que emite junto com a última senha, a senha anterior para ser vizualizada também.
