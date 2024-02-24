package dao.generic;

import java.io.Serializable;
import java.util.Collection;

import dao.Persistente;
import exceptions.ExceptionDao;
import exceptions.ExceptionMaisDeUmRegistro;
import exceptions.ExceptionTable;
import exceptions.ExceptionTipoChaveNaoEncontrada;

public interface IGenericDAO <T extends Persistente, E extends Serializable> {
	/* Se trata de uma interface para servir métodos CRUDS
	 * para outras classes, a fim de reaproveitar códigos
	 */

	public Boolean cadastrar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;
	/*Cadastro de novo registro do banco de dados
	 * O T entity é um génerico para expressar uma entidade a ser cadastrada desde que herde Persistente
	 * ex: Cliente, Produto, Venda...
	 * Retorna um valor True caso o cadastro concluido e falso para nao cadastrado
	 */
	
	public void excluir(E valor) throws ExceptionDao;
	/*Método para excluir no SGBD
	 * E valor deve ser Serializable, ou seja os valores como Long implementam o Serializable
	 * pode ser uma chave Long, um código de cliente, de produto...
	 */
	
	public void alterar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;
	
	
	public T consultar (E valor) throws ExceptionMaisDeUmRegistro, ExceptionTable, ExceptionDao;
	/*Método para consultar uma tabela a partir de uma chave no banco de dados.
	 * 
	 */
	
	public Collection<T> buscarTodos() throws ExceptionDao;
	/* Mostra todos... 
	 * 
	 */
	
	
	
}
