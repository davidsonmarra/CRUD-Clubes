public interface Entidade {
  public void criarObjeto(String nome, String cnpj, String cidade);
  public String toString();
  public byte[] toByteArray();
  public void fromByteArray(byte[] b);    
}