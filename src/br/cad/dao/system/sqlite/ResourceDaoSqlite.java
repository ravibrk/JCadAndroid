package br.cad.dao.system.sqlite;

import java.sql.SQLException;

import br.cad.dao.sqlite.AbstractDaoSqLite;
import br.cad.dao.system.ResourceDao;
import br.cad.model.system.Resource;

/**
 * 
 * @author WilliamRodrigues
 *
 */
public class ResourceDaoSqlite extends AbstractDaoSqLite<Resource> implements ResourceDao {

	protected ResourceDaoSqlite(Class<Resource> dataClass) throws SQLException {
		super(dataClass);
	}
}
