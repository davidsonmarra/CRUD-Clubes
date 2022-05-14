import java.util.Scanner;

public class Menu {
    
  public static void mostrar(){
    // método que mostra na tela as opções do menu
    System.out.println("\tCRUD de Clubes de Futebol (Campeonato Brasileiro)");
    System.out.println("1. Criar");
    System.out.println("2. Ler");
    System.out.println("3. Atualizar");
    System.out.println("4. Exclui");
    System.out.println("5. Jogar partida");
    System.out.println("0. Fim");

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
    Clube novoClube = new Clube(); // passamos os valores digitados pelo usuário para o objetos que criamos acima
    novoClube.criarObjeto(nome, cnpj, cidade); // instanciamos um objeto da classe Clube
    // não precisamos passar id, pontos e partidasJogadas porque elas automaticamente
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      arquivoDeClubes.Create(novoClube); // método que cria um novo Clube no arquivo
      System.out.println("TIME CRIADO COM SUCESSO!");
    } catch (Exception e) {
      System.out.println("Erro");
    }
    
  }

  public static void ler() {
    System.out.println("Você entrou no método pesquisar.");
    System.out.println("Entre com o id do Clube:");
    Scanner entrada = new Scanner(System.in); // lê o id
    byte id = entrada.nextByte(); // armazena o id
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      Clube clubeProcurado = arquivoDeClubes.Read(id); // método que busca e retorna o Clube que possui o id igual ao id passado pelo usuário
      if(clubeProcurado.id != -1) { // se o clube for encontrado
        System.out.println("TIME ENCONTRADO: ");
        System.out.println(clubeProcurado.toString()); // mostra o clube encontrado
      } else { // se não for encontrado
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
    Clube novoClube = new Clube(); // instanciamos um objeto da classe Clube
    novoClube.criarObjeto(nome, cnpj, cidade); // passamos os valores digitados pelo usuário para o objetos que criamos acima
    novoClube.id = id; // igualamos o id do objeto ao id adicionado pelo usuário
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      arquivoDeClubes.Update(novoClube); // chama o método que atualiza o clube que possui o id igual ao clube que o usuário passou
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }
  
  public static void exclui(){
    System.out.println("Você entrou no método Exclui.");
    System.out.println("Entre com o id do Clube:");
    Scanner entrada = new Scanner(System.in); // lê o id
    byte id = entrada.nextByte(); // armazena o id
    try{ 
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor()); // cria a instância da classe CRUD que cuida das operações no arquivo
      Clube clubeProcurado = arquivoDeClubes.Delete(id); // chama o método que apaga o Clube que possui o id passado pelo usuário
      if(clubeProcurado.id != -1) { // se o time for excluído com sucesso
        System.out.println("TIME EXCLUIDO: ");
        System.out.println(clubeProcurado.toString());
      } else { // se acontecer algum erro do time não ser encontrado
        System.out.println("TIME NÃO ENCONTRADO!");
      }
    } catch (Exception e) { // em caso de algum erro
      System.out.println("Erro");
    }
  }
  
  public static void partida(){
    System.out.println("Aqui realizamos partidas entre dois times.");
    System.out.println("Entre com o id do primeiro clube:");
    byte golsClube1, golsClube2; // quantidade de gols dos clubes
    Scanner entrada = new Scanner(System.in);
    byte id1 = entrada.nextByte(); // armazena o id do primeiro time
    System.out.println("Entre com o id do segundo clube:");
    entrada = new Scanner(System.in);
    byte id2 = entrada.nextByte(); // armazena o id do segundo time
    try {
      CRUD<Clube> arquivoDeClubes = new CRUD<>(Clube.class.getConstructor());
      Clube clube1 = arquivoDeClubes.Read(id1);
      Clube clube2 = arquivoDeClubes.Read(id2);
      if(clube1.id == clube2.id) {
        System.out.println("Os IDs são iguais e um time não pode jogar contra outro!");
      }
      else if(clube1.id != -1 && clube2.id != -1) { // verifica se os dois times foram encontrados
        System.out.println("PLACAR:");
        System.out.println(clube1.nome + ": ");
        golsClube1 = entrada.nextByte();
        System.out.println(clube2.nome + ": ");
        golsClube2 = entrada.nextByte();
        if(golsClube1 == golsClube2) {
          System.out.println("O jogo foi empate! Cada time fez 1 ponto!");
          
          // incrementa em 1 unidade as partidas jogadas
          clube1.partidasJogadas++;
          clube2.partidasJogadas++;

          // incrementa em 1 unidade os pontos feitos
          clube1.pontos++;
          clube2.pontos++;
        } else if(golsClube1 > golsClube2) {
          System.out.println("O clube " + clube1.nome + " ganhou! Ele faz 3 pontos e o clube " + clube2.nome + " 0 pontos!");

          // incrementa em 1 unidade as partidas jogadas
          clube1.partidasJogadas += 1;
          clube2.partidasJogadas += 1;

          // incrementa em 3 unidades os pontos feitos do time vencedor
          clube1.pontos += 3;
        } else {
          System.out.println("O clube " + clube2.nome + " ganhou! Ele faz 3 pontos e o clube " + clube1.nome + " 0 pontos!");

          // incrementa em 1 unidade as partidas jogadas
          clube1.partidasJogadas += 1;
          clube2.partidasJogadas += 1;

          // incrementa em 3 unidades os pontos feitos do time vencedor
          clube2.pontos += 3;
        }

        // atualiza os clubes
        arquivoDeClubes.Update(clube1);
        arquivoDeClubes.Update(clube2);
      } else {
        System.out.println("Um dos times não foi encontrado.");
      }
    } catch (Exception e) {
      System.out.println("Erro");
    }
  }


  public static void pesquisaNome(){
    System.out.println("Você entrou no método de pesquisar por nome do clube.");
    ListaInvertida li = new ListaInvertida();
    System.out.println("Entre com o nome do Clube:");
    Scanner entrada = new Scanner(System.in); // lê o nome
    String nome = entrada.nextLine(); // armazena o id
    try{ 
      li.insertClube((byte)1, nome);
      li.insertClube((byte)1, "America");
      li.insertClube((byte)2, "Cruzeiro");
    } catch (Exception e) { // em caso de algum erro
      System.out.println("Erro");
    }
  }
  
  public static void main(String[] args) { // método principal que controla o menu
    byte opcao;
    Scanner entrada = new Scanner(System.in); // usuário digita a opção desejada
    do{
      mostrar();
      opcao = entrada.nextByte(); // armazenamos a opção desejada na variável opção
      
      switch(opcao){ // switch case que lida com a opção escolhida pelo usuário

        case 0:
          Ordenacao o = new Ordenacao();
          o.ordenaArquivoDados();
          o.reset();
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

        case 5:
          partida();
          break;
        case 6:
          pesquisaNome();
          break;
        
        default:
          System.out.println("Opção inválida.");
      }
    } while(opcao != 0); // fica em looping enquanto o usuário não escolher a opção de sair
  }
}
