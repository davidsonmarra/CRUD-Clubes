import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class Ordenacao {
  static String arqNumero;

  // com base no mod ,de 3 podemos receber até 4 valores, e cada um deles significa um arquivo
  public RandomAccessFile qualArquivo(int i, RandomAccessFile arq1, RandomAccessFile arq2, RandomAccessFile arq3, RandomAccessFile arq4) {
    switch (i % 3) {
      case 0:
        return arq1;   
      case 1:
      return arq2;
      case 2:
        return arq3; 
      default:
        return arq4;
    }
  }

  public void qualArquivoString(int i) {
    switch (i % 3) {
      case 0:
        arqNumero = "arqTemp1.db";   
        break;
      case 1:
        arqNumero = "arqTemp2.db"; 
        break;
      case 2:
        arqNumero = "arqTemp3.db";  
        break;
      default:
        arqNumero = "arqTemp4.db";  
        break;
    }
  }

  public void ordenaArquivoDados() {
    byte maxId;
    int contador = 0;
    int tam;
    byte [] b;
    Clube c = new Clube();
    Boolean aux = true;
    ArrayList<Clube> list = new ArrayList();
    try{
      RandomAccessFile arq = new RandomAccessFile("dados/Clube.db", "rw"); // abre o arquivo ou cria se ele não existir
      RandomAccessFile arq1 = new RandomAccessFile("arqTemp1.db", "rw"); // abre o arquivo ou cria se ele não existir
      arq1.setLength(0); // reinicia o arquivo 1
      RandomAccessFile arq2 = new RandomAccessFile("arqTemp2.db", "rw"); // abre o arquivo ou cria se ele não existir
      arq2.setLength(0); // reinicia o arquivo 2
      RandomAccessFile arq3 = new RandomAccessFile("arqTemp3.db", "rw"); // abre o arquivo ou cria se ele não existir
      arq3.setLength(0); // reinicia o arquivo 3
      RandomAccessFile arq4 = new RandomAccessFile("arqTemp4.db", "rw"); // abre o arquivo ou cria se ele não existir
      arq4.setLength(0); // reinicia o arquivo 4
      // 2 caminhos e 10 registros por vez
      
      //distribuição
      arq.seek(0);
      maxId = arq.readByte();
      while(arq.getFilePointer() < arq.length()) {
        Clube cAux = new Clube();
        byte lap = arq.readByte();
        if(lap == ' ') {
          tam = arq.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arq.read(b); // lê o tamanho exato do registro e armazena em b
          cAux.fromByteArray(b);
          cAux.tam = tam;
          list.add(cAux);
          contador++;

          if(contador >= 10 || cAux.id == maxId) {
            contador = 0;
            list.sort((o1, o2)
            -> o1.id == o2.id ? 0 : (
              o1.id > o2.id ? 1 : -1
              ));
            if(aux) { // escreve no arquivo 1
              for(int j = 0; j < list.size(); j++) {
                c = list.get(j);
                arq1.writeInt(c.tam);
                arq1.writeByte(c.id);
                arq1.write(c.toByteArray());
              }
            } else { // escreve no arquivo 2
              for(int j = 0; j < list.size(); j++) {
                c = list.get(j);
                arq2.writeInt(c.tam);
                arq2.writeByte(c.id);
                arq2.write(c.toByteArray());
              }
            }
            aux = !aux;
            list.clear();
          }
        } else {
          tam = arq.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arq.read(b); // lê o tamanho exato do registro e armazena em b
        }
      }
      list.clear(); // limpa a lista
      contador = 1;
      int numDoArq = 0;
      arq1.seek(0); // vai para a posição 0 do arquivo 1
      arq2.seek(0);
      //intercalação
      while(true) {
        int i = 0;
        RandomAccessFile arqAux = qualArquivo(numDoArq, arq1, arq2, arq3, arq4);
        // carrega e ordena em memória os 10 primeiros registros de um arquivo temporário de entrada
        while(arqAux.getFilePointer() < arqAux.length() && i < 10 * contador) {
          Clube cAux = new Clube();
          tam = arqAux.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arqAux.read(b); // lê o tamanho exato do registro e armazena em b
          cAux.fromByteArray(b);
          cAux.tam = tam;
          list.add(cAux);
          i++;
        }
        i = 0;

        RandomAccessFile arqAux2 = qualArquivo(numDoArq + 1, arq1, arq2, arq3, arq4);
        while(arqAux2.getFilePointer() < arqAux2.length() && i < 10 * contador) {
          Clube cAux = new Clube();
          tam = arqAux2.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arqAux2.read(b); // lê o tamanho exato do registro e armazena em b
          cAux.fromByteArray(b);
          cAux.tam = tam;
          list.add(cAux);
          i++;
        }
        
        // ordena lista
        list.sort((o1, o2)
          -> o1.id == o2.id ? 0 : (
          o1.id > o2.id ? 1 : -1
        ));

        // escreve no arquivo de saída
        RandomAccessFile arqAux3 = qualArquivo(numDoArq + 2, arq1, arq2, arq3, arq4);
        for(i = 0; i < 10 * contador * 2 && i < list.size(); i++) {
          c = list.get(i);
          arqAux3.writeInt(c.tam);
          arqAux3.writeByte(c.id);
          arqAux3.write(c.toByteArray());
        }
        
        // coube tudo no arquivo 3, então acabou
        if(arqAux.getFilePointer() >= arqAux.length() && arqAux2.getFilePointer() >= arqAux2.length()) {
          qualArquivoString(numDoArq + 2);
          break;
        }

        // se não couber ele continua e vai pegar mais dos arquivos de entrada
        list.clear();
        while(arqAux.getFilePointer() < arqAux.length() && i < 10 * contador) {
          Clube cAux = new Clube();
          tam = arqAux.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arqAux.read(b); // lê o tamanho exato do registro e armazena em b
          cAux.fromByteArray(b);
          cAux.tam = tam;
          list.add(cAux);
          i++;
        }
        i = 0;

        while(arqAux2.getFilePointer() < arqAux2.length() && i < 10) {
          Clube cAux = new Clube();
          tam = arqAux2.readInt();
          b = new byte[tam+1]; // cria um array de bytes para ler o arquivo
          arqAux2.read(b); // lê o tamanho exato do registro e armazena em b
          cAux.fromByteArray(b);
          cAux.tam = tam;
          list.add(cAux);
          i++;
        }

        // ordena lista
        list.sort((o1, o2)
          -> o1.id == o2.id ? 0 : (
          o1.id > o2.id ? 1 : -1
        ));

        // escreve no arquivo 4 o restante
        RandomAccessFile arqAux4 = qualArquivo(numDoArq + 3, arq1, arq2, arq3, arq4);
        for(i = 0; i < 10 * contador * 2 && i < list.size(); i++) {
          c = list.get(i);
          arqAux4.writeInt(c.tam);
          arqAux4.writeByte(c.id);
          arqAux4.write(c.toByteArray());
        }

        // coube tudo no arquivo 4, então acabou
        if(arqAux.getFilePointer() >= arqAux.length() && arqAux2.getFilePointer() >= arqAux2.length()) {
          qualArquivoString(numDoArq + 3);
          break;
        }
        // se não couber tudo ele vai pra mais uma roda e agora são trocados os arquivos de leitura e escrita
        numDoArq += 2;
      }

      // fecha todos os arquivos
      arq.close();
      arq1.close();
      arq2.close();
      arq3.close();
      arq4.close();
    }
    catch(Exception e){
      e.printStackTrace();
   
    }
  }

  public void reset() {
    byte ultimoId = 0;
    byte [] b;
    Indice index = new Indice();
    try {
      RandomAccessFile arq = new RandomAccessFile("dados/Clube.db", "rw");
      arq.setLength(0);
      arq.seek(0);
      RandomAccessFile arqAux = new RandomAccessFile(this.arqNumero, "rw"); // abre o arquivo ou cria se ele não existir
      RandomAccessFile arqIndex = new RandomAccessFile("dados/index.db", "rw");
      RandomAccessFile arqListaClube = new RandomAccessFile("dados/listaInvertidaClube.db", "rw");
      RandomAccessFile arqListaCidade = new RandomAccessFile("dados/listaInvertidaCidade.db", "rw");
      ListaInvertida listaInvertida = new ListaInvertida();

      arqIndex.setLength(0);
      arqListaClube.setLength(0);
      arqListaCidade.setLength(0);
      arqIndex.seek(0);
      arqAux.seek(0);
      arq.writeByte(0);
      // carrega
      while(arqAux.getFilePointer() < arqAux.length()) {
        Clube c = new Clube();
        c.tam = arqAux.readInt();
        b = new byte[c.tam+1]; // cria um array de bytes para ler o arquivo
        arqAux.read(b); // lê o tamanho exato do registro e armazena em b
        c.fromByteArray(b);
        ultimoId = c.id; // salva o último id lido

        // insere no index
        index.insert(c.id, arq.getFilePointer());

        // reescreve o arquivo de dados original já ordenado
        arq.writeByte(' '); // escreve a lapide
        arq.writeInt(c.tam); // escreve o tamanho
        arq.writeByte(c.id); // escreve o id
        arq.write(c.toByteArray()); // escreve o restante do clube

        // insere na lista
        String[] nomes = c.nome.split(" "); // divide por espaço
        for (int i = 0; i < nomes.length; i++) {
          listaInvertida.insertClube(c.id, nomes[i]);
        }

        String[] cidades = c.cidade.split(" "); // divide por espaço
        for (int i = 0; i < nomes.length; i++) {
          listaInvertida.insertCidade(c.id, cidades[i]);
        }
      }
      arq.seek(0);
      arq.writeByte(ultimoId);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
