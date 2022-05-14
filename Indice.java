import java.io.RandomAccessFile;

public class Indice {
  protected byte id;
  protected byte pos;

  // insere no arquivo de index
  public void insert(byte id, long pos) {
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/index.db", "rw"); // abre o arquivo ou cria se ele não existir
      long i = (id - 1) * 9;
      arq.seek(i);
      arq.writeByte(id);
      arq.writeLong(pos);
      arq.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  // pesquisa no arquivo de index a posição de um id
  public long search(byte id) {
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/index.db", "rw"); // abre o arquivo ou cria se ele não existir
      long low = 0, high = arq.length() / 9, mid;
      byte idArq;
      // nosso índex sempre estará ordenado, então podemos ir para o meio do arquivo e começar a busca binária
      while(low <= high) {
        mid = (int)((low + high) / 2);
        arq.seek(mid * 9);
        idArq = arq.readByte();
        if(id < idArq)
          high = mid - 1;
        else if(id > idArq)
          low = mid + 1;
        else {
          return arq.readLong();
        }
      }
      arq.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
    long lixo = -1;
    return lixo;
  }

  // deleta um registro do arquivo de índice 
  public void deleta(byte id) {
    long posId;
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/index.db", "rw"); // abre o arquivo ou cria se ele não existir
      while(arq.getFilePointer() < arq.length()) {
        posId = arq.getFilePointer();
        byte idAux = arq.readByte();
        if(idAux == id) {
          arq.seek(posId);
          arq.write(-1);
        }
        arq.readLong();
      }
      arq.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  // atualiza um registro do arquivo de índice 
  public void atualiza(byte id, long pos) {
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/index.db", "rw"); // abre o arquivo ou cria se ele não existir
      while(arq.getFilePointer() < arq.length()) {
        byte idAux = arq.readByte();
        if(id == idAux) {
          arq.writeLong(pos);
          break;
        }
        arq.readLong();
      }
      arq.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}

