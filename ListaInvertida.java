import java.io.RandomAccessFile;

public class ListaInvertida {
  protected byte palavra;
  protected byte id1;
  protected byte id2;
  protected byte id3;
  protected byte id4;


  // insere no arquivo de index
  public void insertClube(byte id, String nome) {
    // nossa estrutura é a seguinte:
    // teremos a palavra, seguida de 4 ids e por fim o endereço do próximo
    boolean escreveu = false;
    byte idArq;
    long pos, proxPos;
    int i = 0;
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/listaInvertida.db", "rw"); // abre o arquivo ou cria se ele não existir
      System.out.println(nome);
      System.out.println(arq.length());
      if(arq.length() <= 1) {
        arq.writeUTF(nome); // escreve nome
        arq.writeByte(id); // escreve o id
        arq.writeByte(-1); // inicializa como -1
        arq.writeByte(-1); // inicializa como -1
        arq.writeByte(-1); // inicializa como -1
        arq.writeLong(-1); // inicializa como -1
        escreveu = true;
      }
      arq.seek(0); // vai pra posição 0
      while(arq.getFilePointer() < arq.length() && !escreveu) {
        if(nome.equals(arq.readUTF())) { // o nome já existe
          for(i = 0; i < 4; i++) {
            pos = arq.getFilePointer(); // pega a pos do id
            idArq = arq.readByte(); // le o id
            if(idArq == -1) { // se for -1 pode escrever aqui
              arq.seek(pos);
              arq.writeByte(id);
              escreveu = true;
              break;
            }
          }
          if(i != 4) break; // quer dizer que coube no espaço disponível
          pos = arq.getFilePointer();
          proxPos = arq.readLong();
          if(proxPos != -1) { // quer dizer que há uma próxima posição
            arq.seek(proxPos);
          } else { // se não tiver posição escrita, escrevemos a última
            arq.seek(pos); // voltamos para a posição do long
            arq.writeLong(arq.length()); // escrevemos a pos da última pos do arquivo
            arq.seek(arq.length()); // vamos para última posição
            break; // depois daqui vai para o último if, já que o escreveu é falso
          }
        } else { // lê ate a proxima posição
          arq.readByte();
          arq.readByte();
          arq.readByte();
          arq.readByte();
          arq.readLong();
        }
      }
      if(!escreveu) { // se não achou dentro do arquivo
        arq.seek(arq.length()); // vai pra última posição
        arq.writeUTF(nome); // escreve nome
        arq.writeByte(id); // escreve o id
        arq.writeByte(-1); // inicializa como -1
        arq.writeByte(-1); // inicializa como -1
        arq.writeByte(-1); // inicializa como -1
        arq.writeLong(-1); // inicializa como -1
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
