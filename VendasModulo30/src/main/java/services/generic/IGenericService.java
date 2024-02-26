package services.generic;

import java.io.Serializable;
import java.util.Collection;

import dao.Persistente;
import exceptions.ExceptionDao;
import exceptions.ExceptionTipoChaveNaoEncontrada;

public interface IGenericService<T extends Persistente, E extends Serializable> {
	
	/**
     * Método para cadastrar novos registro no banco de dados
     *
     * @param entity a ser cadastrado
     * @return retorna verdadeiro para cadastrado e falso para não cadastrado
	 * @throws DAOException 
     */
    public Boolean cadastrar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;
    
    public void alterar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;
    
    public void excluir(E valor) throws ExceptionDao;
    
    public T consultar(E valor) throws ExceptionDao;
    
    public Collection<T> buscarTodos() throws ExceptionDao;
	
	
	
	
}
