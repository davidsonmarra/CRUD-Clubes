import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class Clube implements Entidade { // classe que será nossa entidade/registro

  protected byte id;
  protected String nome;
  protected String cnpj;
  protected String cidade;
  protected byte partidasJogadas;
  protected byte pontos;

  // cria instância da classe clube
  public void criarObjeto(String nome, String cnpj, String cidade){
    this.id = 0;
    this.nome = nome;
    this.cnpj = cnpj;
    this.cidade = cidade;
    this.partidasJogadas = 0;
    this.pontos = 0;
  }

  // printa a classe clube
  public String toString(){
    return "\nNome: " + nome +
      "\nCNPJ: " + cnpj +
      "\nCidade: " + cidade +
      "\nPartidas Jogadas: " + partidasJogadas +
      "\nPontos: " + pontos;
  }


  public byte[] toByteArray(){ // retorna um array de bytes para escrever no arquivo
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try {
      dos.writeUTF(this.nome);
      dos.writeUTF(this.cnpj);
      dos.writeUTF(this.cidade);
      dos.writeByte(this.partidasJogadas);
      dos.writeByte(this.pontos);
      return baos.toByteArray();
    } catch(IOException e) {
      e.printStackTrace();
      byte[] b = new byte[0];
      return b;
    }
  }

  public void fromByteArray(byte[] b){ // recebe um array de bytes vindo do arquivo e lê salvando cada propriedade devidamente
    ByteArrayInputStream bais = new ByteArrayInputStream(b);
    DataInputStream dis = new DataInputStream(bais);
    try {
      this.id = dis.readByte();
      this.nome = dis.readUTF();
      this.cnpj = dis.readUTF();
      this.cidade = dis.readUTF();
      this.partidasJogadas = dis.readByte();
      this.pontos = dis.readByte();
    } catch(IOException e) {
      e.printStackTrace();
    }  
  }
}
