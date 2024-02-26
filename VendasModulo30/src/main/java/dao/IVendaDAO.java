package dao;


import dao.generic.IGenericDAO;
import domain.Produto;
import domain.Venda;
import exceptions.ExceptionDao;
import exceptions.ExceptionTipoChaveNaoEncontrada;

public interface IVendaDAO extends IGenericDAO<Venda, String> {
	
public void finalizarVenda(Venda venda) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;
	
	public void cancelarVenda(Venda venda) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao;

}
