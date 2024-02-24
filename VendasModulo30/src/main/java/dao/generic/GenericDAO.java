package dao.generic;

import java.io.Serializable;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import dao.generic.jdbc.ConnectionFactory;
import annotation.ColunaTabela;
import annotation.Tabela;
import annotation.TipoChave;
import dao.Persistente;
import exceptions.ExceptionDao;
import exceptions.ExceptionElementoNaoConhecido;
import exceptions.ExceptionMaisDeUmRegistro;
import exceptions.ExceptionTable;
import exceptions.ExceptionTipoChaveNaoEncontrada;

public abstract class GenericDAO<T extends Persistente, E extends Serializable> implements IGenericDAO<T, E> {

	/**
	 * Classe abstrata que implementa métodos do genérico com métodos abstratos que
	 * podem ser aplicados em outras classes Também encapsula a lógica de manuseio
	 * do banco de dados
	 */

	public abstract Class<T> getTipoClasse();
	/*
	 * Será útil para utilizarmos a Reflection e invocar métodos personalizados para
	 * uma certa entidade.
	 */

	public abstract void atualizarDados(T entity, T entityCadastrado);

	protected abstract String getQueryInsercao();

	protected abstract String getQueryExclusao();

	protected abstract String getQuerytAtualizacao();

	/**
	 * Querys que podem ser personalizadas de acordo com a entidade São Strings,
	 * comandos SQL
	 */

	protected abstract void setParametrosQueryInsercao(PreparedStatement stmInsert, T entity) throws SQLException;

	protected abstract void setParametrosQueryExclusao(PreparedStatement stmDelete, E valor) throws SQLException;

	protected abstract void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, T entity) throws SQLException;

	protected abstract void setParametrosQuerySelect(PreparedStatement stmUpdate, E valor) throws SQLException;

	public GenericDAO() {

	}

	protected Connection getConnection() throws ExceptionDao {
		try {
			return ConnectionFactory.getConnection();
		} catch (SQLException e) {
			throw new ExceptionDao("ERRO ABRINDO CONEXAO COM O BANCO DE DADOS ", e);
		}
	}
	/*
	 * Obter Conexão com banco de dados
	 */

	private void closeConnection(Connection connection, PreparedStatement stm, ResultSet rs) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (stm != null && !stm.isClosed()) {
				stm.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public E getChave(T entity) throws ExceptionTipoChaveNaoEncontrada {
		Field[] fields = entity.getClass().getDeclaredFields();
		E returnValue = null; // inicializa a variavel do resultado da chave nula...

		for (Field field : fields) {
			if (field.isAnnotationPresent(TipoChave.class)) {
				TipoChave tipoChave = field.getAnnotation(TipoChave.class);
				String nameMetodo = tipoChave.value();

				try {
					Method method = entity.getClass().getMethod(nameMetodo);
					returnValue = (E) method.invoke(entity);
					return returnValue;

				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					// Criar exception de negócio ExceptionTipoChaveNaoEncontrada
					e.printStackTrace();
					throw new ExceptionTipoChaveNaoEncontrada(
							"Chave principal do objeto " + entity.getClass() + " não encontrada", e);

				}
			}

		}

		if (returnValue == null) {
			String msg = "Chave principal do objeto " + entity.getClass() + " não encontrada";
			System.out.println("*** ERRO ***" + msg);
			throw new ExceptionTipoChaveNaoEncontrada(msg);

		}
		return returnValue;

	}

	public Boolean cadastrar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao {
		Connection connection = null;
		PreparedStatement stm = null;

		try {
			connection = getConnection();
			stm = connection.prepareStatement(getQueryInsercao(), Statement.RETURN_GENERATED_KEYS);
			setParametrosQueryInsercao(stm, entity);
			int linhaAfetada = stm.executeUpdate();

			if (linhaAfetada > 0) {
				try (ResultSet rs = stm.getGeneratedKeys()) {
					if (rs.next()) {
						Persistente per = (Persistente) entity;
						per.setId(rs.getLong(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			throw new ExceptionDao("Erro ao cadastrar objeto", e);
		} finally {
			closeConnection(connection, stm, null);

		}

		return false;
	}

	public void excluir(E valor) throws ExceptionDao {
		Connection connection = getConnection();
		PreparedStatement stm = null;
		try {
			stm = connection.prepareStatement(getQueryExclusao());
			setParametrosQueryExclusao(stm, valor);
			int linhasAfetadas = stm.executeUpdate();
		} catch (SQLException e) {
			throw new ExceptionDao("Erro ao excluir Objeto", e);
		} finally {
			closeConnection(connection, stm, null);
		}
	}

	public void alterar(T entity) throws ExceptionTipoChaveNaoEncontrada, ExceptionDao {
		Connection connection = getConnection();
		PreparedStatement stm = null;
		try {
			stm = connection.prepareStatement(getQuerytAtualizacao());
			setParametrosQueryAtualizacao(stm, entity);
			int linhasAfetadas = stm.executeUpdate();
		} catch (SQLException e) {
			throw new ExceptionDao("Erro ao alterar objeto", e);
		} finally {
			closeConnection(connection, stm, null);
		}
	}
	/*
	 * public T consultar(E valor) throws ExceptionMaisDeUmRegistro, ExceptionTable,
	 * ExceptionDao { try { validarMaisDeUmRegistro(valor); } return null; }
	 */

	public Collection<T> buscarTodos() throws ExceptionDao {

		return null;
	}

	/*
	 * private void validarMaisDeUmRegistro(E valor) throws ExceptionDao {
	 * Connection connection = getConnection(); PreparedStatement stm = null;
	 * ResultSet rs = null; Long count = null; try { //stm =
	 * connection.prepareStatement("SELECT count(*) FROM" + getTableName() + ) }
	 * 
	 * }
	 */

	private String getTableName() throws ExceptionTable {
		if (getTipoClasse().isAnnotationPresent(Tabela.class)) {
			Tabela table = getTipoClasse().getAnnotation(Tabela.class);
			return table.value();
		} else {
			throw new ExceptionTable("Tabela no Tipo " + getTipoClasse().getName() + " não encontrada");
		}

	}

	private String getNomeCampoChave(Class clazz) throws ExceptionTipoChaveNaoEncontrada {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(TipoChave.class) && field.isAnnotationPresent(ColunaTabela.class)) {
				ColunaTabela coluna = field.getAnnotation(ColunaTabela.class);
				return coluna.dbName();
			}
		}
		return null;
	}
	
	private void setValueByType(T entity, Method method, Class<?> classField, ResultSet rs, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, ExceptionTipoChaveNaoEncontrada, ExceptionElementoNaoConhecido{
		
		if(classField.equals(Integer.class)) {
			Integer val = rs.getInt(fieldName);
			method.invoke(entity, val);
		}
		else if(classField.equals(Long.class)) {
			Long val = rs.getLong(fieldName);
			method.invoke(entity,val);
		}
		else if(classField.equals(Double.class)) {
			Double val = rs.getDouble(fieldName);
			method.invoke(entity,val);
		}
		else if(classField.equals(Short.class)) {
			Short val = rs.getShort(fieldName);
			method.invoke(entity,val);
		}
		else if(classField.equals(BigDecimal.class)) {
			BigDecimal val = rs.getBigDecimal(fieldName);
			method.invoke(entity,val);
		}
		else if(classField.equals(String.class)) {
			String val = rs.getString(fieldName);
			method.invoke(entity,val);
		}
		
		else {
			throw new ExceptionElementoNaoConhecido("TIPO DE CLASSE NÃO CONHECIDO: " + classField);
		}
	}
	
	
}
