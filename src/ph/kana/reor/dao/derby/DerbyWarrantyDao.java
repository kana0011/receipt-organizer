package ph.kana.reor.dao.derby;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import ph.kana.reor.dao.WarrantyDao;
import ph.kana.reor.dao.common.AbstractDao;
import ph.kana.reor.exception.DataAccessException;
import ph.kana.reor.model.Document;
import ph.kana.reor.model.Warranty;

public class DerbyWarrantyDao extends AbstractDao<Warranty> implements WarrantyDao {

	@Override
	public Warranty findByIdAndDocument(Long id, Document document) throws DataAccessException {
		if (document ==  null || document.getId() == null) {
			return null;
		} else {
			String sql = "SELECT id, expiration FROM warranty WHERE id = ? AND document_id = ?";
			List<Warranty> results = executeQuery(connection -> {
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setLong(1, id);
				statement.setLong(2, document.getId());
				return statement.executeQuery();
			});
			return results.isEmpty()? null : results.get(0);
		}
	}

	@Override
	public Warranty save(Warranty warranty) throws DataAccessException { // add Connection param?
		String sql = "INSERT INTO warranty(expiration, document_id) VALUES (?, ?)";
		return execute(warranty, connection -> {
			PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			LocalDate expiration = warranty.getExpiration();

			statement.setDate(1, (expiration == null)? null : Date.valueOf(expiration));
			statement.setLong(2, warranty.getDocument().getId());
			statement.executeUpdate();

			ResultSet idResultSet = statement.getGeneratedKeys();
			return idResultSet.next()? idResultSet.getLong(1) : null;
		}, false);
	}

	@Override
	protected Warranty map(ResultSet resultSet) throws DataAccessException {
		try {
			Warranty warranty = new Warranty();
			warranty.setId(resultSet.getLong("id"));
			warranty.setExpiration(resultSet.getDate("expiration").toLocalDate());

			return warranty;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

}
