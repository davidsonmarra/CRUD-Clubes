import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LZW {
  /** Compress a string to a list of output symbols. */
  public static ArrayList<Integer> compress(String s1) {
    ArrayList<Integer> lista = new ArrayList<>(); // gera a sequência de dados comprimidos
    Map<String,Integer> dicionario = new HashMap<String,Integer>();
    for (int i = 0; i <= 255; i++) // cria o dicionário com as 256 símbolos
      dicionario.put("" + (char)i, i);
    String p = "", c = "";
    p += s1.charAt(0);
    int code = 256;
    for (int i = 0; i < s1.length(); i++) { // comprime a palavra original e vai modificando o dicionário conforme a necessidade
      if (i != s1.length() - 1)
          c += s1.charAt(i + 1);
      if (dicionario.get(p + c) != dicionario.get(dicionario.size())) {
          p = p + c;
      }
      else {
        lista.add(dicionario.get(p));
        dicionario.put(p + c, code);
        code++;
        p = c;
      }
      c = "";
    }
    lista.add(dicionario.get(p)); // adiciona o último valor na lista
    System.out.print(lista.toString());
    decoding(lista);
    return lista;
  }

  public static String decoding(ArrayList<Integer> op) {
    String descomprimida = "";
    Map<Integer, String> dicionario = new HashMap<Integer,String>();
    for (int i = 0; i <= 122; i++)  // cria o dicionário
      dicionario.put(i, "" + (char)i);
    int old = op.get(0), n;
    String s = dicionario.get(old);
    String c = "";
    c += s.charAt(0); // primeiro valor da palavra descomprimida
    System.out.print(s);
    descomprimida += s;
    int count = 256;
    for (int i = 0; i < op.size() - 1; i++) {
      n = op.get(i + 1);
      if (dicionario.get(n) == dicionario.get(dicionario.size())) {
          s = dicionario.get(old);
          s = s + c; 
      }
      else {
          s = dicionario.get(n);
      }
      c = "";
      descomprimida += s;
      System.out.print(s); // s é a letra atual da palavra que está sendo descomprimida
      c += s.charAt(0);
      dicionario.put(count, dicionario.get(old) + c);
      count++;
      old = n;
    }
    System.out.println();
    return descomprimida;
  }

  public static byte[] criaByte(ArrayList<Integer> list) {
    byte s[] = new byte[list.size()];
    for(int i = 0; i < list.size(); i++) {
      s[i] = list.get(i).byteValue();
    }
    return s;
  }

  public void comprime(String versao) {
    try {
      CRUD<Clube> crud = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      RandomAccessFile arq1 = new RandomAccessFile("dados/Clube.db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("dados/ClubeCompressão" + versao + ".db", "rw");
      ArrayList<Integer> listaComprimida = new ArrayList<>();
      byte idMax = arq1.readByte(); // le o id max do arquivo de dados original
      arq2.writeByte(idMax);
      while(arq1.getFilePointer() < arq1.length()) {
        arq2.writeByte(arq1.readByte()); // escreve a lápide
        arq2.writeInt(arq1.readInt()); // escreve o tamanho total
        arq2.writeByte(arq1.readByte()); // le e escreve o id
        String nome = arq1.readUTF(); // le o nome
        listaComprimida.clear(); // limpa a lista
        nome = crud.Descriptografa(nome); // descriptografa o nome para escrever
        listaComprimida = compress(nome); // comprime o nome
        for(int i = 0; i < listaComprimida.size(); i++) // escreve 
          arq2.writeByte(listaComprimida.get(i).byteValue()); // escreve o nome comprimido
        arq2.writeByte(';'); // escreve uma divisória
        String cnpj = arq1.readUTF(); // le o cnpj
        listaComprimida.clear();
        listaComprimida = compress(cnpj);
        for(int i = 0; i < listaComprimida.size(); i++) 
          arq2.writeByte(listaComprimida.get(i).byteValue()); // escreve o cnpj comprimido
        arq2.writeByte(';'); // escreve uma divisória
        String cidade = arq1.readUTF(); // le o cnpj
        listaComprimida.clear();
        listaComprimida = compress(cidade);
        for(int i = 0; i < listaComprimida.size(); i++) 
          arq2.writeByte(listaComprimida.get(i).byteValue()); // escreve a cidade comprimida
        arq2.writeByte(';'); // escreve uma divisória
        arq2.writeByte(arq1.readByte()); // le e escreve o numero de partidas
        arq2.writeByte(arq1.readByte()); // le e escreve o numero de pontos
      }
      double ganho = arq1.length()*1.0 / arq2.length();
      System.out.println(arq1.length() + "|" + arq2.length() + "|" + ganho);

      if(ganho == 1) 
        System.out.println("Não houve ganho nem perda.");
      else if (ganho > 1) 
        System.out.printf("Houve ganho de %.2f%%\n", ((ganho - 1) * 100));
      else 
        System.out.printf("Houve uma perda de %.2f%%\n", ganho * 100);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void descomprime(String versao) {
    try {
      CRUD<Clube> crud = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      ArrayList<Integer> lista = new ArrayList<>();
      RandomAccessFile arq1 = new RandomAccessFile("dados/ClubeCompressão" + versao + ".db", "rw");
      RandomAccessFile arq2 = new RandomAccessFile("dados/Clube.db", "rw");
      byte c = ' ';
      arq1.seek(0);
      arq2.seek(0);
      byte idMax = arq1.readByte(); // le o id max do arquivo de dados original
      arq2.writeByte(idMax);
      while(arq1.getFilePointer() < arq1.length()) {
        arq2.writeByte(arq1.readByte()); // escreve a lápide
        arq2.writeInt(arq1.readInt()); // escreve o tamanho total
        arq2.writeByte(arq1.readByte()); // le e escreve o id
        lista.clear(); // limpa a lista
        c = arq1.readByte(); // inicia o c
        while(c != ';') { // enquanto não achar a divisória
          lista.add((int)c); // vai inicializando a lista com os valores do nome comprimido
          System.out.println(c);
          c = arq1.readByte();
        }
        arq2.writeUTF(crud.Criptografa(decoding(lista))); // descomprime o nome
        lista.clear(); // limpa a lista
        c = arq1.readByte();
        while(c != ';') { // enquanto não achar a divisória escreve o cnpj
          lista.add((int)c); // vai inicializando a lista com os valores do cnpj comprimido
          System.out.println(c);
          c = arq1.readByte();
        }
        arq2.writeUTF(decoding(lista)); // descomprime o nome
        lista.clear(); // limpa a lista
        c = arq1.readByte();
        while(c != ';') { // enquanto não achar a divisória escreve o cidade
          lista.add((int)c); // vai inicializando a lista com os valores do cidade comprimido
          System.out.println(c);
          c = arq1.readByte();
        }
        arq2.writeUTF(decoding(lista)); // descomprime o nome
        arq2.writeByte(arq1.readByte()); // le e escreve o numero de partidas
        arq2.writeByte(arq1.readByte()); // le e escreve o numero de pontos
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
