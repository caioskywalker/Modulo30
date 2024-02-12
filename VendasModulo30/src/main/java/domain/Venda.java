package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import annotation.ColunaTabela;
import annotation.TipoChave;
import dao.Persistente;

public class Venda implements Persistente{

	public enum Status{
		INICIADA, CONCLUIDA, CANCELADA;
		
		public static Status getByName(String value) {
			for (Status status : Status.values()) {
				if(status.name().equals(value)) {
					return status;
				}
			}
			return null;
		}
	}
	
	@ColunaTabela(dbName = "id_venda", setJavaName = "setId")
	private Long idVenda;
	
	@TipoChave("getCodigo")
	@ColunaTabela(dbName = "codigo", setJavaName = "setCodigo")
	private String codigo;
	
	@ColunaTabela(dbName = "id_cliente_fk", setJavaName = "setIdClienteFk")
	private Cliente cliente;
	//Chave Estrangeira de Cliente
	
	//@ColunaTabela(dbName = "id", setJavaName = "setId")
	private Set<ProdutoQuantidade> produtos;
	
	@ColunaTabela(dbName = "valor_total", setJavaName = "setValorTotal")
	private BigDecimal valorTotal;
	
	@ColunaTabela(dbName = "data_venda", setJavaName = "setDataVenda")
	private Instant dataVenda; //Instant é a Classe de Data
	
	@ColunaTabela(dbName = "status_venda", setJavaName = "setStatus")
	private Status status;
	
	public Venda() {//Construtor da Classe
		produtos = new HashSet<ProdutoQuantidade>();
	}
	
	public void adicionarProduto(Produto produto, Integer quantidade) {
	
		validarStatus();
		Optional <ProdutoQuantidade> op = //Verifica se há um produto setado no metodo
				produtos.stream().filter(f -> f.getProduto().getCodigo().equals(produto.getCodigo())).findAny();
		//	Procure qualquer produto com mesmo codigo do que o HashSet de ProdutoQuantidade
		/* A Classe Optional foi introduzida a partir do Java 8
		 * para tratar o problema dos valores nulos de forma mais
		 * segura. A ideia é evitar o uso de verificações de nulos
		 * e NullPointerExecptions
		 * Indica explicitamente que um valor pode estar ausente 		
		 */
		
			if(op.isPresent()) { //Se já tiver, adiciona apenas a quantidade
				ProdutoQuantidade produtoQtd = op.get();
				produtoQtd.adicionar(quantidade);
				//Adicione a quantidade do produto
				
			}
			
			else { //Se não tiver o produto, seta o Produto e adiciona a quantidade
				//Criar fabrica para criar ProdutoQuantidade
				ProdutoQuantidade prod = new ProdutoQuantidade();
				prod.setProduto(produto);
				prod.adicionar(quantidade);
				produtos.add(prod);
			}
			recalcularValorTotalVenda(); //Recalcula o valor total da venda
	}
	
	private void recalcularValorTotalVenda() {
		//validarStatus();
		BigDecimal valorTotal = BigDecimal.ZERO; //Zera o valor total local
		for(ProdutoQuantidade prod : this.produtos) {// o prod percorre o HashSet produtos
			valorTotal = valorTotal.add(prod.getValorTotal()); //pega o ValorTotal de Prod e adiciona localmente
		}
		this.valorTotal = valorTotal; //iguala o valorTotal local e o valorTotal da Venda
		
	}

	private void validarStatus() {
		if (this.status == Status.CONCLUIDA) { //Se o status da venda estiver Concluido, cria um novo exception
			throw new UnsupportedOperationException("Impossível alterar venda finalizada.");
		}
		
	}
	
	public void removerProduto(Produto produto , Integer quantidade) {
		validarStatus();
		Optional<ProdutoQuantidade> op = //tem produto em hashset produtos com codigo igual ao codigo do produto setado?
				produtos.stream().filter(f -> f.getProduto().getCodigo().equals(produto.getCodigo())).findAny();
		
		if(op.isPresent()) {
			ProdutoQuantidade produtoQtd = op.get();
				if(produtoQtd.getQuantidade() > quantidade) {
					produtoQtd.remover(quantidade);//se a quantidade do HashSet for maior que a quantidade removida ,remova
					//recalcularValorTotalVenda();
				}
				else {
					produtos.remove(op.get());//senão, remove o produto inteiro dp HashSet
					//recalcularValorTotalVenda();
				}
		}
		recalcularValorTotalVenda();
	}
	
	public void removerTodosProdutos() {
		validarStatus();
		produtos.clear();
		valorTotal = BigDecimal.ZERO;
	}

	
	
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<ProdutoQuantidade> getProdutos() {
		return produtos;
	}

	public void setProdutos(Set<ProdutoQuantidade> produtos) {
		this.produtos = produtos;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public Instant getDataVenda() {
		return dataVenda;
	}
	
	public void setDataVenda(Instant dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Long getId() {
		// TODO Auto-generated method stub
		return idVenda;
	}

	
	public void setId(Long id) {
		this.idVenda = id;
		
	}
	
	
	

}
