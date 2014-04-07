package br.cad.dao.system.sqlite;

import br.cad.dao.sqlite.AbstractDaoSqLite;
import br.cad.dao.system.ResourceDao;
import br.cad.model.system.Resource;

import com.j256.ormlite.support.ConnectionSource;

/**
 * 
 * @author WilliamRodrigues
 *
 */
public class ResourceDaoSqLite extends AbstractDaoSqLite<Resource> implements ResourceDao {

	public ResourceDaoSqLite(ConnectionSource connectionSource) {
		super(connectionSource, "br.cad.model.system");
	}
	
}
