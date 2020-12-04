
import java.net.*;
import java.io.*;
import java.util.*;

// Classe do client
public class Client {

  // Notificação
  private String notif = " *** ";

  // Entrada e saída para o socket
  private ObjectInputStream sInput;
  private ObjectOutputStream sOutput;
  private Socket socket;

  private String server, username;
  private int port;

  // Username do client
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  // Construtor
  Client(String server, int port, String username) {
    this.server = server;
    this.port = port;
    this.username = username;
  }

  // Início do chat
  public boolean start() {
    // Conexão ao socket
    try {
      socket = new Socket(server, port);
    } catch (Exception ec) {
      consoleLog("Erro ao conectar ao servidor:" + ec);
      return false;
    }

    String msg = "Servidor conectado com sucesso! " + socket.getInetAddress() + ":" + socket.getPort();
    consoleLog(msg);

    // Data Stream
    try {
      sInput = new ObjectInputStream(socket.getInputStream());
      sOutput = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException eIO) {
      consoleLog("Exceção ao criar novos inputs streams: " + eIO);
      return false;
    }

    // Inicia a thread do servidor
    new ListenFromServer().start();

    try {
      // Envia o username para o servidor
      sOutput.writeObject(username);
    } catch (IOException eIO) {
      consoleLog("Exceção ao realizar login: " + eIO);
      disconnect();
      return false;
    }
    // Feedback de operação bem suscedida
    return true;
  }

  // Enviar mensagem para o log do console
  private void consoleLog(String msg) {
    System.out.println(msg);
  }

  // Envia mensagem para o server
  void sendMessage(ChatMessage msg) {
    try {
      sOutput.writeObject(msg);
    } catch (IOException e) {
      consoleLog("Exceção ao enviar para o servidor: " + e);
    }
  }

  // Desconecta
  private void disconnect() {
    try {
      if (sInput != null)
        sInput.close();
    } catch (Exception e) {
    }
    try {
      if (sOutput != null)
        sOutput.close();
    } catch (Exception e) {
    }
    try {
      if (socket != null)
        socket.close();
    } catch (Exception e) {
    }

  }

  /*
   * Comandos para iniciar o app no modo console: > java Client > java Client
   * username > java Client username portNumber > java Client username portNumber
   * serverAddress Se a porta não é especificada, é usado 1500 Se o servidor não é
   * especificado "localHost" é usado Se o username não é especificado
   * "DefaultUser" é usado
   */
  public static void main(String[] args) {
    // Valores default
    int portNumber = 1500;
    String serverAddress = "localhost";
    String userName = "DefaultUser";
    Scanner scan = new Scanner(System.in);

    System.out.println("Insira seu username: ");
    userName = scan.nextLine();

    // Casos de acordo com o length recebido de args
    switch (args.length) {
      case 3:
        serverAddress = args[2];
      case 2:
        try {
          portNumber = Integer.parseInt(args[1]);
        } catch (Exception e) {
          System.out.println("Porta inválida.");
          System.out.println("> java Client [username] [portNumber] [serverAddress]");
          return;
        }
      case 1:
        userName = args[0];
      case 0:
        break;
      default:
        System.out.println("> java Client [username] [portNumber] [serverAddress]");
        return;
    }

    // Cria o objeto do client
    Client client = new Client(serverAddress, portNumber, userName);
    // Conexão
    if (!client.start())
      return;

    System.out.println("\nOlá! Seja bem vindo!.");
    System.out.println("Instruções:");
    System.out.println("1. Envia a mensagem a todos os usuários no seu terminal!");
    System.out.println("2. Digite '@username<sepaço>mensagem' sem aspas para enviar a mensagem ao cliente desejado.");
    System.out.println("3. Digite 'WHOISIN' sem aspas para ver a lista de clientes online.");
    System.out.println("4. Digite 'LOGOUT' sem aspas para realizar LogOut.");

    // Recebe o input do usuário
    while (true) {
      System.out.print("> ");
      // Lê a mensagem
      String msg = scan.nextLine();

      if (msg.equalsIgnoreCase("LOGOUT")) {
        client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        break;
      }

      else if (msg.equalsIgnoreCase("WHOISIN")) {
        client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
      }

      else {
        client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
      }
    }
    // Encerra
    scan.close();
    client.disconnect();
  }

  // Aguarda mensagem do servidor
  class ListenFromServer extends Thread {
    // Roda o app
    public void run() {
      while (true) {
        try {
          // Lê a mensagem
          String msg = (String) sInput.readObject();
          // Imprime a mensagem
          System.out.println(msg);
          System.out.print("> ");
        } catch (IOException e) {
          consoleLog(notif + "Servidor desconectado: " + e + notif);
          break;
        } catch (ClassNotFoundException e2) {
        }
      }
    }
  }
}
