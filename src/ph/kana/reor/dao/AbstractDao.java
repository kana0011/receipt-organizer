package ph.kana.reor.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import ph.kana.reor.exception.DataAccessException;
import ph.kana.reor.model.Model;
import ph.kana.reor.util.ConnectionManager;
import ph.kana.reor.util.function.CheckedFunction;
import ph.kana.reor.util.function.CheckedRunnable;

abstract class AbstractDao<T extends Model> {

	protected abstract T map(ResultSet resultSet) throws DataAccessException;

	public List<T> executeQuery(CheckedFunction<Connection, ResultSet> action) throws DataAccessException {
		return executeSqlStatement(connection -> {
			ResultSet resultSet = action.apply(connection);
			return mapToEntityList(resultSet);
		});
	}

	public T execute(T model, CheckedFunction<Connection, Long> action) throws DataAccessException {
		return executeSqlStatement(connection -> {
			Long id = action.apply(connection);
			model.setId(id);
			return model;
		});
	}

	public int executeCount(CheckedFunction<Connection, ResultSet> action) throws DataAccessException {
		return executeSqlStatement(connection -> {
			ResultSet resultSet = action.apply(connection);
			return resultSet.next()?
				resultSet.getInt(1) : 0;
		});
	}

	protected Long fetchInsertId(Statement statement) throws SQLException {
		ResultSet idResultSet = statement.getGeneratedKeys();
		return idResultSet.next()? idResultSet.getLong(1) : null;
	}

	private <R> R executeSqlStatement(CheckedFunction<Connection, R> sqlFunction) throws DataAccessException {
		Connection connection = null;
		try {
			connection = ConnectionManager.openConnection();
			if (connection != null) {
				R rv = sqlFunction.apply(connection);

				connection.commit();
				return rv;
			} else {
				throw new DataAccessException("Unable to open connection");
			}
		} catch (Exception e) {
			safeExecute(connection, connection::rollback);
			throw new DataAccessException(e);
		} finally {
			safeExecute(connection, connection::close);
		}
	}

	private void safeExecute(Connection connection, CheckedRunnable runnable) throws DataAccessException {
		try {
			if (connection != null) {
				runnable.run();
			}
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}

	private List<T> mapToEntityList(ResultSet resultSet) throws DataAccessException {
		List<T> mappedResult = new ArrayList();

		try {
			while (resultSet.next()) {
				mappedResult.add(map(resultSet));
			}

			return mappedResult;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
