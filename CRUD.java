import java.io.RandomAccessFile;

import java.lang.reflect.Constructor;

public class CRUD<T extends Entidade> { // método genérico que gerencia as operações em arquivo
  Constructor<T> construtor;

  public CRUD(Constructor<T> construtor) {
    this.construtor = construtor;
  }
  
  public String Criptografa(String nome) {
    String nomeCriptografado = "";
    int cifra = 128;

    for (int i=0; i < nome.length(); i++) {
      nomeCriptografado += (char)(nome.charAt(i) + cifra);
    }
    return nomeCriptografado;
  }

  public String Descriptografa(String nome) {
    String nomeDescriptografado = "";
    int cifra = 128;

    for (int i=0; i < nome.length(); i++) {
      nomeDescriptografado += (char)(nome.charAt(i) - cifra);
    }
    return nomeDescriptografado;
  }
  
  public void Create(Clube novoClube){ // método para armazenar o registro no arquivo
    
    byte [] b;
    
    try{
      Indice index = new Indice();
      ListaInvertida listaInvertida = new ListaInvertida();
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw"); // abre o arquivo ou cria se ele não existir
      byte id = 1;
      String nomeDescriptografado = novoClube.nome;
      novoClube.nome = Criptografa(novoClube.nome); // criptografa o atributo nome
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
      index.insert(id, posAtual); // adiciona no index
      
      String[] nomes = nomeDescriptografado.split(" "); // divide por espaço
      for (int i = 0; i < nomes.length; i++) {
        listaInvertida.insertClube(id, nomes[i]);
      }

      String[] cidades = novoClube.cidade.split(" "); // divide por espaço
      for (int i = 0; i < cidades.length; i++) {
        listaInvertida.insertCidade(id, cidades[i]);
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public Clube Read(byte id) { // método para procurar e retornar um clube pelo id
    byte [] b;
    Clube clubeProcurado = new Clube();
    int tam;
    Indice index = new Indice();
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      long pos = index.search(id);
      if(pos == -1) {
        arq.close();
        clubeProcurado.id = -1;
        return clubeProcurado;
      }
      arq.seek(pos); // vamos para posição que está o registro
      if(arq.readByte() == ' ') { // verifica se a lápide é válida
        tam = arq.readInt(); // lê o tamanho do registro
        b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
        arq.read(b); // lê o tamanho exato do registro e armazena em b
        clubeProcurado.fromByteArray(b); // cria um objeto com as informações armazenadas em b
        if(clubeProcurado.id == id) { // encontrou o clube
          arq.close(); // fecha o arquivo
          clubeProcurado.nome = Descriptografa(clubeProcurado.nome);
          return clubeProcurado; // retorna o clube procurado
        }
      } else { // se a lápide indicar que é um arquivo excluído
        arq.close();
        clubeProcurado.id = -1;
        return clubeProcurado;
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
    Indice index = new Indice();

    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0);
      arq.readByte();
      long pos = index.search(clube.id);
      System.out.println(pos);
      if(pos == -1) {
        arq.close();
        clubeProcurado.id = -1;
      } else { // se encontrar no índice
        arq.seek(pos);
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
              pLap = arq.getFilePointer();
              arq.writeByte(' '); // escreve a lápide
              arq.writeInt(b2.length); // escreve o tamanho do arquivo
              arq.writeByte(clube.id); // escreve o ID
              arq.write(b2); // escreve o registro
              index.atualiza(clube.id, pLap);
            }
          }
        }
      }


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
    Indice index = new Indice();
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/"+construtor.getName()+".db", "rw");
      arq.seek(0); // vai para posição inicial
      if(arq.readByte() < id) { // se o id do cabeçalho for menor que o id lido já significa que esse clube não existe no arquivo
        arq.close();
        throw new Exception();
      }
      long pos = index.search(id);
      System.out.println(pos);
      if(pos == -1) {
        arq.close();
        clubeProcurado.id = -1;
        return clubeProcurado;
      }
      arq.seek(pos); // vamos para posição que está o registro
      p = arq.getFilePointer();
      if(arq.readByte() == ' ') { // verifica se a lápide é válida
        tam = arq.readInt(); // lê o tamanho do arquivo
        b = new byte[tam+1];
        arq.read(b); // armazena em b o registro no arquivo
        clubeProcurado.fromByteArray(b); // cria o objeto com os dados de b
        System.out.println(clubeProcurado.id);
        if(clubeProcurado.id == id) { // verifica se o registro é o que o usuário está querendo deletar pelo id
          arq.seek(p); // vai para a pos da lápide
          arq.writeByte('*'); // marca como excluído o registro
          arq.close(); // fecha o arquivo
          index.deleta(id);
          return clubeProcurado; // retorna clube procurado
        }
      } else { // se a lápide indicar que é um arquivo excluído
        arq.close();
        clubeProcurado.id = -1;
        return clubeProcurado;
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