[![Work in Repl.it](https://classroom.github.com/assets/work-in-replit-14baed9a392b3a25080506f3b7b6d57f295ec2978f6f33ec97e36a161684cbe9.svg)](https://classroom.github.com/online_ide?assignment_repo_id=2973293&assignment_repo_type=AssignmentRepo)
# Chat Multicast - Java

### Objetivo

Praticar programação em redes utilizando sockets.

### Desafio

Desenvolver uma aplicação de Chat em Java com o protocolo Multicast.

### Requisitos

Os requisitos básicos são:

1 - O servidor deve gerenciar apenas uma sala de bate papo.

2 - O cliente deve ser capaz de solicitar acesso à sala de bate papo.

3 - O servidor deve manter uma lista dos membros da sala.

4 - O cliente deve ser capaz de enviar mensagens para a sala.

5 - O cliente deve ser capaz de sair da sala de bate papo.

### Passo a Passo

Para realizar o trabalho, serão seguidos os seguintes passos:

1 - Criar um repositório individual para a sua aplicação através do Github Classroom, no link https://classroom.github.com/a/VhstWTYC (Links para um site externo.).

2 - Definir um protocolo de comunicação entre os clientes e o servidor. Por protocolo de comunicação entende-se, definir os formatos das mensagens de comando (lista de membros, envio de mensagem, acesso e saída da sala, etc.).

3 - Desenvolver a aplicação.

4 - Escrever no README.md do projeto a documentação do trabalho.

## Sobre

Servidor multithread baseado em console que usa programação Java Socket. Um servidor escuta solicitações de conexão de clientes na rede ou até mesmo na mesma máquina. Os clientes sabem como se conectar ao servidor por meio de um endereço IP e número de porta. Após conectar-se ao servidor, o cliente pode escolher seu nome de usuário na sala de chat. O cliente envia uma mensagem, a mensagem é enviada ao servidor usando ObjectOutputStream em java. Depois de receber a mensagem do cliente, o servidor a transmite se não for uma mensagem privada. E se for uma mensagem privada detectada usando '@' seguido por um nome de usuário válido, envie a mensagem apenas para esse usuário. Serialização de objetos Java para transferir as mensagens.

##  Instruções

### Cliente

##### Para iniciar o client no modo console, use um dos seguintes comandos:

1.  java Client
2.  java Client username
3.  java Client username portNumber
4.  java Client username portNumber serverAddress

##### No prompt do console:

- Se o portNumber não for especificado, 1500 será usado.
- Se o serverAddress não for especificado, "localHost" será usado.
- Se o username não for especificado, "UsuárioDefault" será usado.

### **Servidor**

##### Para executar como um aplicativo no console:

1.  java Server
2.  java Server portNumber
    Se o número da porta não for especificado, 1500 é usado.

##### Bate-papo

Enquanto estiver no console do cliente:

1. Basta digitar a mensagem para enviar broadcast a todos os clientes ativos.
2. Digite '@username <space> suaMensagem' sem aspas para enviar a mensagem ao cliente desejado.
3. Digite 'WHOISIN' sem aspas para ver a lista de clientes ativos.
4. Digite 'LOGOUT' sem aspas para fazer logoff do servidor.
