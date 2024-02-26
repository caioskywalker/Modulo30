package test;

import exceptions.ExceptionDao;
import exceptions.ExceptionTipoChaveNaoEncontrada;
import services.ClienteService;
import services.IClienteService;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dao.ClienteDAO;
import dao.ClienteDaoMock;
import domain.Cliente;
import dao.IClienteDAO;

public class ClienteServiceTest {
	
private IClienteService clienteService;
	
	private Cliente cliente;
	
	public ClienteServiceTest() {
		IClienteDAO dao = new ClienteDaoMock();
		clienteService = new ClienteService(dao);
	}
	
	@Before
	public void init() {
		cliente = new Cliente();
		cliente.setCpf(12312312312L);
		cliente.setNome("Rodrigo");
		cliente.setCidade("São Paulo");
		cliente.setEndereco("End");
		cliente.setEstado("SP");
		cliente.setNumero(10);
		cliente.setTelefone(1199999999L);
		
	}
	
	@Test
	public void pesquisarCliente() throws ExceptionDao {
		Cliente clienteConsultado = clienteService.buscarPorCPF(cliente.getCpf());
		Assert.assertNotNull(clienteConsultado);
	}
	
	@Test
	public void salvarCliente() throws ExceptionTipoChaveNaoEncontrada, ExceptionDao {
		Boolean retorno = clienteService.cadastrar(cliente);
		
		Assert.assertTrue(retorno);
	}
	
	@Test
	public void excluirCliente() throws ExceptionDao {
		clienteService.excluir(cliente.getCpf());
	}
	
	@Test
	public void alterarCliente() throws ExceptionTipoChaveNaoEncontrada, ExceptionDao {
		cliente.setNome("Rodrigo Pires");
		clienteService.alterar(cliente);
		
		Assert.assertEquals("Rodrigo Pires", cliente.getNome());
	}

}
