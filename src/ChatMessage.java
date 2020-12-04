
import java.io.*;
/*
 * Classe que lida com as mensagens entre o client e o server.
 * A mensagem é tratada como um objeto por ser mais prático
 * e menos verboso, sem haver a necessidade de contar os bytes
 */

public class ChatMessage implements Serializable {

  // Os diferentes comandos enviados pelo cliente
  // WHOISIN lista dos usuários conectados
  // MESSAGE envia uma mensagem
  // LOGOUT faz logout
  static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
  private int type;
  private String message;

  // construtor
  ChatMessage(int type, String message) {
    this.type = type;
    this.message = message;
  }

  int getType() {
    return type;
  }

  String getMessage() {
    return message;
  }
}
