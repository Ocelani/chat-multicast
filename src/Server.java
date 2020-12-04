import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
  // Um uniqueId para cada conexão
  private static int uniqueId;
  // ArrayList que armazena os clientes na sala de chat
  private ArrayList<ClientThread> chatRoom;
  // Hora de cada mensagem
  private SimpleDateFormat dateTime;
  // Porta de conexão
  private int port;
  // Para a estabilidade do server
  private boolean keepServerOnline;
  // Notificação
  private String notify = " *** ";

  // Construtor que recebe a porta como parâmetro de conexão
  public Server(int port) {
    this.port = port;
    dateTime = new SimpleDateFormat("HH:mm:ss");
    chatRoom = new ArrayList<ClientThread>();
  }

  public void start() {
    keepServerOnline = true;
    try {
      // Cria o socket server
      ServerSocket serverSocket = new ServerSocket(port);

      // Loop infinito que aguarda conexões até que o server fique ativo
      while (keepServerOnline) {
        consoleLog("Servidor aguardando conexões na porta: " + port + ".");

        Socket socket = serverSocket.accept();

        if (!keepServerOnline)
          break;

        ClientThread clientThread = new ClientThread(socket);
        // Adiciona o cliente na lista e inicia a thread
        clientThread.add(clientThread);
        clientThread.start();
      }
      try {
        // Ao encerrar o socket server, desconecta os clientes na thread
        serverSocket.close();
        for (int i = 0; i < chatRoom.size(); ++i) {
          ClientThread client = chatRoom.get(i);

          try {
            client.sInput.close();
            client.sOutput.close();
            client.clientSocket.close();
          } catch (IOException ioE) {
          }
        }
      } catch (Exception e) {
        consoleLog("Exceção ao encerrar o servidor e demais clients: " + e);
      }
    } catch (IOException e) {
      String msg = dateTime.format(new Date()) + " Exceção na instanciação de novo ServerSocket: " + e + "\n";
      consoleLog(msg);
    }
  }

  // Método de parar o server
  protected void stop() {
    keepServerOnline = false;
    try {
      new Socket("localhost", port);
    } catch (Exception e) {
    }
  }

  // Mostra as mensagens no console
  private void consoleLog(String msg) {
    String time = dateTime.format(new Date()) + " " + msg;
    System.out.println(time);
  }

  // Método dedicado ao broadcast na sala de chat
  private synchronized boolean broadcast(String message) {
    // Timestamp
    String time = dateTime.format(new Date());

    String[] msg = message.split(" ", 3);

    // Confirma se a mensagem é para grupo ou privada
    boolean isPrivate = false;
    if (msg[1].charAt(0) == '@')
      isPrivate = true;

    // Se a mensagem é privada, envia somente para o usuário destinatário
    if (isPrivate == true) {
      String tocheck = msg[1].substring(1, msg[1].length());

      message = msg[0] + msg[2];
      String messageLf = time + " " + message + "\n";
      boolean found = false;

      // Faz a busca do usuário destinatário na lista
      for (int y = chatRoom.size(); --y >= 0;) {
        ClientThread client = chatRoom.get(y);
        String check = client.getUsername();

        if (check.equals(tocheck)) {
          if (!client.writeMsg(messageLf)) {
            chatRoom.remove(y);
            consoleLog("Client desconectado: " + client.username + " saiu do chat.");
          }
          found = true;
          break;
        }

      }
      // Caso não encontre o usuário destinatário
      if (found != true) {
        return false;
      }
    }
    // Se é uma mensagem para a sala em grupo
    else {
      String messageLf = time + " " + message + "\n";
      System.out.print(messageLf);

      // Para a remoção de conexões
      for (int i = chatRoom.size(); --i >= 0;) {
        ClientThread client = chatRoom.get(i);

        if (!client.writeMsg(messageLf)) {
          chatRoom.remove(i);
          consoleLog("Client desconectado: " + client.username + " saiu do chat.");
        }
      }
    }
    return true;

  }

  // Remoção de clientes
  synchronized void remove(int id) {
    String disconnectedClient = "";
    // Busca pelo id do client
    for (int i = 0; i < chatRoom.size(); ++i) {
      ClientThread client = chatRoom.get(i);

      if (client.id == id) {
        disconnectedClient = client.getUsername();
        chatRoom.remove(i);
        break;
      }
    }
    broadcast(notify + disconnectedClient + " saiu do chat." + notify);
  }

  /*
   * Para rodar como um console application > java Server > java Server portNumber
   * Se a porta não é especificada, é usada a 1500
   */
  public static void main(String[] args) {
    int portNumber = 1500;

    switch (args.length) {
      case 1:
        try {
          portNumber = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Porta inválida.");
          System.out.println("> java Server [portNumber]");
          return;
        }
      case 0:
        break;
      default:
        System.out.println("> java Server [portNumber]");
        return;

    }
    // Inicia o objeto do servidor
    Server server = new Server(portNumber);
    server.start();
  }

  // Instâncias da thread para cada servidor
  class ClientThread extends Thread {
    // O socket do client a ser recebido
    Socket clientSocket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    // Id único
    int id;
    // O Username do Client
    String username;
    // objeto da mensagem
    ChatMessage chatMsg;
    // timestamp
    String date;

    // Construtor
    ClientThread(Socket socket) {
      id = ++uniqueId;
      this.clientSocket = socket;
      System.out.println("Conexão do socket e Object Input/Output Streams");
      // Criação do data stream
      try {
        sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        // Leitura do username
        username = (String) sInput.readObject();
        broadcast(notify + username + " entrou no chat." + notify);
      } catch (IOException e) {
        consoleLog("Exceção ao criar novo Input/output Streams: " + e);
        return;
      } catch (ClassNotFoundException e) {
      }
      date = new Date().toString() + "\n";
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    // Para leitura de mensagens e rodar a aplicação continuamente
    public void run() {
      // Loop até realizar LOGOUT
      boolean keepGoing = true;
      while (keepGoing) {
        // Leitura de Strings
        try {
          chatMsg = (ChatMessage) sInput.readObject();
        } catch (IOException e) {
          consoleLog(username + " Exceção na leitura de Streams: " + e);
          break;
        } catch (ClassNotFoundException e2) {
          break;
        }
        // Receber mensagens
        String message = chatMsg.getMessage();

        // Outras ações de acordo com o tipo da mensagem enviada
        switch (chatMsg.getType()) {

          case ChatMessage.MESSAGE:
            boolean confirmation = broadcast(username + ": " + message);
            if (confirmation == false) {
              String msg = notify + "Usuário inexistente" + notify;
              writeMsg(msg);
            }
            break;
          case ChatMessage.LOGOUT:
            consoleLog(username + " Desconectado através do comando LOGOUT");
            keepGoing = false;
            break;
          case ChatMessage.WHOISIN:
            writeMsg("Lista de usuários conectados: " + dateTime.format(new Date()) + "\n");
            // Lista de usuários ativos
            for (int i = 0; i < chatRoom.size(); ++i) {
              ClientThread ct = chatRoom.get(i);
              writeMsg((i + 1) + ") " + ct.username + " desde " + ct.date);
            }
            break;
        }
      }
      // Remover usuários
      remove(id);
      close();
    }

    // Encerramento da aplicação
    private void close() {
      try {
        if (sOutput != null)
          sOutput.close();
      } catch (Exception e) {
      }
      try {
        if (sInput != null)
          sInput.close();
      } catch (Exception e) {
      }
      ;
      try {
        if (clientSocket != null)
          clientSocket.close();
      } catch (Exception e) {
      }
    }

    // Escrever mensagens
    private boolean writeMsg(String msg) {
      // Enviar mensagem se client está conectado
      if (!clientSocket.isConnected()) {
        close();
        return false;
      }
      // Escreve a mensagem na stream
      try {
        sOutput.writeObject(msg);
      }
      // Informa o usuário sobre eventuais erros sem abortar a aplicação
      catch (IOException e) {
        consoleLog(notify + "Erro ao enviar a mensagem para: " + username + notify);
        consoleLog(e.toString());
      }
      return true;
    }
  }
}
