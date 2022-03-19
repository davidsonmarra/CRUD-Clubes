import java.io.RandomAccessFile;

import java.lang.reflect.Constructor;

public class CRUD<T extends Entidade> {
  Constructor<T> construtor;

  public CRUD(Constructor<T> construtor) {
    this.construtor = construtor;
  }

  public void Create(Clube novoClube){ // método para criar o objeto
      
    byte [] b;
    
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      byte id = 1;
      arq.seek(0);
      if(arq.length() == 0) {
        arq.writeByte(id);
      } else {
        id = arq.readByte();
        id++;
        arq.seek(0);
        arq.writeByte(id);
      }
      arq.seek(arq.length());
      arq.writeByte(' '); // escreve a lápide
      b = novoClube.toByteArray(); // retorna um array de bytes que será o registro
      arq.writeInt(b.length); // escreve o tamanho do arquivo
      arq.writeByte(id); // escreve o ID
      arq.write(b); // escreve o registro
      arq.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public Clube Read(byte id) { 
    byte [] b;
    Clube clubeProcurado = new Clube();
    int tam;
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0);
      if(arq.readByte() < id) {
        throw new Exception();
      }
      while(arq.getFilePointer() < arq.length()) {
        if(arq.readByte() == ' ') {
          tam = arq.readInt();
          b = new byte[tam+1];
          arq.read(b);
          clubeProcurado.fromByteArray(b);
          if(clubeProcurado.id == id) { // encontrou o clube
            arq.close();
            return clubeProcurado;
          }
        } else {
          tam = arq.readInt();
          b = new byte[tam+1];
          arq.read(b);
        }
      }
      arq.close();
      clubeProcurado.id = -1;
      return clubeProcurado;
    }
    catch(Exception e){
      e.printStackTrace();
      Clube lixo = new Clube();
      lixo.id = -1;
      return lixo;
    }
  }

  public void Update(Clube clube) {
    byte [] b; // vetor de bytes do registro no arquivo
    byte [] b2; // vetor de bytes do novo registro atualizadp
    Clube clubeProcurado = new Clube();
    int tam;
    long p; // ponteiro que aponta pra posição inicial de um registro
    long pLap; // ponteiro que aponta para lápide de um registro
    try{
      System.out.println(clube.toString());
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0);
      while(arq.getFilePointer() < arq.length()) {
        pLap = arq.getFilePointer();
        if(arq.readByte() == ' ') {
          tam = arq.readInt();
          p = arq.getFilePointer();
          b = new byte[tam+1];
          arq.read(b);
          clubeProcurado.fromByteArray(b);
          if(clubeProcurado.id == clube.id) {
            b2 = clube.toByteArray();
            if(tam >= b2.length) { // se o novo registro for menor 
              arq.seek(p);
              arq.writeByte(clube.id);
              arq.write(b2);
            } else { // se o novo registro for maior
              arq.seek(pLap);
              arq.writeByte('*'); // marca o registro como excluído
              arq.seek(arq.length()); // vai para posição final
              arq.writeByte(' '); // escreve a lápide
              arq.writeInt(b2.length); // escreve o tamanho do arquivo
              arq.writeByte(clube.id); // escreve o ID
              arq.write(b2); // escreve o registro
            }
            break;
          }
        }
      }
      arq.close();
      System.out.println("TIME ALTERADO");
    }
    catch(Exception e){
      e.printStackTrace();
      System.out.println("TIME NÃO ENCONTRADO");
    }
  }

  public Clube Delete(byte id) {
    byte [] b;
    Clube clubeProcurado = new Clube();
    int tam;
    long p; // ponteiro que aponta para lápide
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0);
      if(arq.readByte() < id) {
        throw new Exception();
      }
      while(arq.getFilePointer() < arq.length()) {
        p = arq.getFilePointer();
        if(arq.readByte() == ' ') {
          tam = arq.readInt();
          b = new byte[tam+1];
          arq.read(b);
          clubeProcurado.fromByteArray(b);
          if(clubeProcurado.id == id) {
            arq.seek(p);
            arq.writeByte('*');
            arq.close();
            return clubeProcurado;
          }
        }
      }
      arq.close();
      clubeProcurado.id = -1;
      return clubeProcurado;
    }
    catch(Exception e){
      e.printStackTrace();
      Clube lixo = new Clube();
      lixo.id = -1;
      return lixo;
    }
  }

}