import java.io.RandomAccessFile;

public class Indice {
  protected byte id;
  protected byte pos;

  public void iniciliza() {
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/index.db", "rw"); // abre o arquivo ou cria se ele não existir
      for(int i = 0; i < 100; i++) {
        arq.writeByte(0);
        arq.writeLong(0);
      }
    }  catch(Exception e) {
      e.printStackTrace();
    }
  }

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
}

