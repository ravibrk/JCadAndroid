package br.cad.app;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import br.cad.dao.sqlite.DatabaseHelper;
import br.cad.dao.system.ResourceDao;
import br.cad.dao.system.sqlite.ResourceDaoSqLite;
import br.cad.model.system.Resource;

import com.j256.ormlite.dao.Dao;

@EActivity(R.layout.activity_teste)
public class TesteActivity extends Activity {

	@OrmLiteDao(helper = DatabaseHelper.class, model = Resource.class)
	Dao<Resource, Long> resourceDao;

	private ResourceDao getResourceDao() {
		return new ResourceDaoSqLite(resourceDao.getConnectionSource());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@ViewById(R.id.button1)
	Button button1;

	@Click(R.id.button1)
	void onClick() {
		button1.setText("OK, deu certo");

		Log.i(getClass().getSimpleName(), (getResourceDao()).findAll().get(0).getName());
	}
}
