public class App {
  public static void main(String[] args) throws Exception {
      try { 

        CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());

        Menu.mostrar();
        











        //   arquivoDeClientes.CriarObjeto(4);
        //   System.out.println(arquivoDeClientes);


      } catch (Exception e) {
          System.out.println("Erro");

      }
  }
}
