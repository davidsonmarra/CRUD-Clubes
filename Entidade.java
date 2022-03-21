public interface Entidade {
  // métodos que nossa entidade gerenciada pelo CRUD deve possuir
  public void criarObjeto(String nome, String cnpj, String cidade);
  public String toString();
  public byte[] toByteArray();
  public void fromByteArray(byte[] b);    
}