import java.io.RandomAccessFile;

import java.lang.reflect.Constructor;

public class CRUD<T extends Entidade> { // método genérico que gerencia as operações em arquivo
  Constructor<T> construtor;

  public CRUD(Constructor<T> construtor) {
    this.construtor = construtor;
  }

  public void Create(Clube novoClube){ // método para armazenar o registro no arquivo
      
    byte [] b;
    
    try{
      Indice index = new Indice();
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw"); // abre o arquivo ou cria se ele não existir
      byte id = 1;
      arq.seek(0);
      if(arq.length() == 0) { // se o arquivo não tiver nenhuma posição significa que ele está vazio
        arq.writeByte(id); // logo podemos escrever o id 1 no cabeçalho
      } else { // se o arquivo já estiver preenchido
        id = arq.readByte();  // devemos ler qual o último id criado (estará na primeira posição do arquivo)
        id++; // o id do nosso registro deve ser maior em 1 unidade do que o último id criado
        arq.seek(0); // devemos voltar para a primeira posição
        arq.writeByte(id); // e sobrescrever o id que está lá armazenado
      }
      arq.seek(arq.length()); // vamos para última posição criar nosso registro
      long posAtual = arq.getFilePointer();
      arq.writeByte(' '); // escreve a lápide
      b = novoClube.toByteArray(); // retorna um array de bytes que será o registro
      arq.writeInt(b.length); // escreve o tamanho do arquivo
      arq.writeByte(id); // escreve o ID
      arq.write(b); // escreve o registro
      arq.close(); // fecha o arquivo
      index.insert(id, posAtual);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public Clube Read(byte id) { // método para procurar e retornar um clube pelo id
    byte [] b;
    Clube clubeProcurado = new Clube();
    int tam;
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0); // vamos para posição 0
      if(arq.readByte() < id) { // se o id do cabeçalho for menor que o id lido já significa que esse clube não existe no arquivo
        arq.close();
        throw new Exception();
      }
      while(arq.getFilePointer() < arq.length()) { // enquanto não chegar no fim do arquivo
        if(arq.readByte() == ' ') { // verifica se a lápide é válida
          tam = arq.readInt(); // lê o tamanho do registro
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arq.read(b); // lê o tamanho exato do registro e armazena em b
          clubeProcurado.fromByteArray(b); // cria um objeto com as informações armazenadas em b
          if(clubeProcurado.id == id) { // encontrou o clube
            arq.close(); // fecha o arquivo
            return clubeProcurado; // retorna o clube procurado
          }
        } else { // se a lápide indicar que é um arquivo excluído
          tam = arq.readInt(); // lê o tamanho do arquivo
          b = new byte[tam+1];
          arq.read(b); // e simplesmente lê exatamente o tamanho do arquivo para o ponteiro estar na lápide do próximo registro
        }
      }
      // se chegar aqui quer dizer que não encontrou, então retornaremos um clube com id = -1
      arq.close();
      clubeProcurado.id = -1;
      return clubeProcurado;
    }
    catch(Exception e){
      e.printStackTrace();
      Clube lixo = new Clube();
      lixo.id = -1;
      return lixo; // em caso de erro retorna lixo
    }
  }

  public void Update(Clube clube) { // método para atualizar e retornar um clube
    byte [] b; // vetor de bytes do registro no arquivo
    byte [] b2; // vetor de bytes do novo registro atualizado
    Clube clubeProcurado = new Clube();
    int tam;
    long p; // ponteiro que aponta pra posição inicial de um registro
    long pLap; // ponteiro que aponta para lápide de um registro
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0);
      arq.readByte();
      while(arq.getFilePointer() < arq.length()) { // enquanto não chegar final do arquivo
        pLap = arq.getFilePointer(); // armazena a posição da lápide
        if(arq.readByte() == ' ') {
          tam = arq.readInt(); // armazena o tamanho do registro
          p = arq.getFilePointer(); // armazena a posição inicial do registro (depois do tamanho)
          b = new byte[tam+1];
          arq.read(b);
          clubeProcurado.fromByteArray(b);
          if(clubeProcurado.id == clube.id) { // se achar o registro
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
        } else { // se a lápide indicar que é um arquivo excluído
          tam = arq.readInt(); // lê o tamanho do arquivo
          b = new byte[tam+1];
          arq.read(b); // e simplesmente lê exatamente o tamanho do arquivo para o ponteiro estar na lápide do próximo registro
        }
      }
      arq.close();
      if(clubeProcurado.id == clube.id) {
        System.out.println("TIME ALTERADO");
      }
      else 
        System.out.println("TIME NÃO ENCONTRADO");
    }
    catch(Exception e){
      e.printStackTrace();
      System.out.println("TIME NÃO ENCONTRADO");
    }
  }

  public Clube Delete(byte id) { // método para deletar um clube
    byte [] b;
    Clube clubeProcurado = new Clube();
    int tam;
    long p; // ponteiro que aponta para lápide
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0); // vai para posição inicial
      if(arq.readByte() < id) { // se o id do cabeçalho for menor que o id lido já significa que esse clube não existe no arquivo
        arq.close();
        throw new Exception();
      }
      while(arq.getFilePointer() < arq.length()) { // enquanto não chegar no final do arquivo
        p = arq.getFilePointer(); // armazena a posição da lápide
        if(arq.readByte() == ' ') { // verifica se é um registro válido
          tam = arq.readInt(); // lê o tamanho do arquivo
          b = new byte[tam+1];
          arq.read(b); // armazena em b o registro no arquivo
          clubeProcurado.fromByteArray(b); // cria o objeto com os dados de b
          System.out.println(clubeProcurado.id);
          if(clubeProcurado.id == id) { // verifica se o registro é o que o usuário está querendo deletar pelo id
            arq.seek(p); // vei para a pos da lápide
            arq.writeByte('*'); // marca como excluído o registro
            arq.close(); // fecha o arquivo
            return clubeProcurado; // retorna clube procurado
          }
        } else { // se a lápide indicar que é um arquivo excluído
          tam = arq.readInt(); // lê o tamanho do arquivo
          b = new byte[tam+1];
          arq.read(b); // e simplesmente lê exatamente o tamanho do arquivo para o ponteiro estar na lápide do próximo registro
        }
      }
      arq.close();
      clubeProcurado.id = -1;
      return clubeProcurado; //
    }
    catch(Exception e){
      e.printStackTrace();
      Clube lixo = new Clube();
      lixo.id = -1;
      return lixo;
    }
  }

}