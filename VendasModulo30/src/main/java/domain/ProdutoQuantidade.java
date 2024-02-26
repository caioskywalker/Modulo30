package domain;

import java.math.BigDecimal;

import annotation.ColunaTabela;
import annotation.Tabela;

@Tabela("tb_produto_quantidade")
public class ProdutoQuantidade {
	
	@ColunaTabela(dbName = "id" , setJavaName = "setId")
	private Long idQuantidadeProduto;
	
	private Produto produto;
	
	@ColunaTabela(dbName = "quantidade", setJavaName = "setQuantidade")
	private Integer quantidade;
	
	@ColunaTabela(dbName = "valor_total", setJavaName = "setValorTotal")
	private BigDecimal valorTotal;

	public Long getIdQuantidadeProduto() {
		return idQuantidadeProduto;
	}

	public void setIdQuantidadeProduto(Long idQuantidadeProduto) {
		this.idQuantidadeProduto = idQuantidadeProduto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	
	public ProdutoQuantidade() { //construtor desta classe
		this.quantidade = 0; //inicializa a quantidade como 0
		this.valorTotal = BigDecimal.ZERO; // inicializa o valor total como 0
	}
	
	public void adicionar(Integer quantidade) {
		this.quantidade += quantidade; //soma a quantidade
		BigDecimal novoValor = this.produto.getValor().multiply(BigDecimal.valueOf(quantidade));
		//Faz o get do valor do produto e multiplica pela quantidade do mesmo
		BigDecimal novoTotal = this.valorTotal.add(novoValor);
		//
		this.valorTotal = novoTotal ;
		
	}
	
	public void remover (Integer quantidade) {
		this.quantidade -= quantidade; //decresce a quantidade
		BigDecimal novoValor = this.produto.getValor().multiply(BigDecimal.valueOf(quantidade));
		this.valorTotal = this.valorTotal.subtract(novoValor);
	}
	
	
	
	

}
