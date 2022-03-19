import java.util.Scanner;

public class Menu {
    
  public static void mostrar(){
    System.out.println("\tCRUD de Clubes de Futebol (Campeonato Brasileiro)");
    System.out.println("0. Fim");
    System.out.println("1. Criar");
    System.out.println("2. Ler");
    System.out.println("3. Atualizar");
    System.out.println("4. Exclui");
    System.out.println("Opcao:");
  }

  public static void criar(){
    System.out.println("Você entrou no método criar.");
    String nome;
    String cnpj;
    String cidade;
    System.out.println("Entre com o nome do Clube:");
    Scanner entrada = new Scanner(System.in);
    nome = entrada.nextLine();
    System.out.println("Entre com o cnpj do Clube (sem máscara):");
    entrada = new Scanner(System.in);
    cnpj = entrada.next();
    System.out.println("Entre com o cidade do Clube (sem máscara):");
    entrada = new Scanner(System.in);
    cidade = entrada.nextLine();
    Clube novoClube = new Clube();
    novoClube.criarObjeto(nome, cnpj, cidade);
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());
      arquivoDeClubes.Create(novoClube);
      System.out.println("TIME CRIADO COM SUCESSO!");
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }

  public static void ler() {
    System.out.println("Você entrou no método pesquisar.");
    System.out.println("Entre com o id do Clube:");
    Scanner entrada = new Scanner(System.in);
    byte id = entrada.nextByte();
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());
      Clube clubeProcurado = arquivoDeClubes.Read(id);
      if(clubeProcurado.id != -1) {
        System.out.println("TIME ENCONTRADO: ");
        System.out.println(clubeProcurado.toString());
      } else {
        System.out.println("TIME NÃO ENCONTRADO!");
      }
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }

  public static void atualizar(){
    String nome;
    String cnpj;
    String cidade;
    System.out.println("Você entrou no método atualizar.");
    System.out.println("Entre com o id do Clube:");
    Scanner entrada = new Scanner(System.in);
    byte id = entrada.nextByte();
    System.out.println("Entre com o nome do Clube:");
    entrada = new Scanner(System.in);
    nome = entrada.nextLine();
    System.out.println("Entre com o cnpj do Clube (sem máscara):");
    entrada = new Scanner(System.in);
    cnpj = entrada.next();
    System.out.println("Entre com o cidade do Clube (sem máscara):");
    entrada = new Scanner(System.in);
    cidade = entrada.nextLine();
    Clube novoClube = new Clube();
    novoClube.criarObjeto(nome, cnpj, cidade);
    novoClube.id = id;
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());
      arquivoDeClubes.Update(novoClube);
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }
  
  public static void exclui(){
    System.out.println("Você entrou no método Exclui.");
    System.out.println("Entre com o id do Clube:");
    Scanner entrada = new Scanner(System.in);
    byte id = entrada.nextByte();
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());
      Clube clubeProcurado = arquivoDeClubes.Delete(id);
      if(clubeProcurado.id != -1) {
        System.out.println("TIME EXCLUIDO: ");
        System.out.println(clubeProcurado.toString());
      } else {
        System.out.println("TIME NÃO ENCONTRADO!");
      }
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }
  
  public static void consulta(){
    System.out.println("Você entrou no método Consulta.");
  }
  
  public static void main(String[] args) {
    int opcao;
    Scanner entrada = new Scanner(System.in);
    
    do{
      mostrar();
      opcao = entrada.nextInt();
      
      switch(opcao){
        case 0:
          System.out.println("FIM.");
          break;

        case 1:
          criar();
          break;
            
        case 2:
          ler();
          break;
            
        case 3:
          atualizar();
          break;
            
        case 4:
          exclui();
          break;
        
        default:
          System.out.println("Opção inválida.");
      }
    } while(opcao != 0);
  }
}
