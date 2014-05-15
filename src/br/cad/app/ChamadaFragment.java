package br.cad.app;

import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.cad.dao.sqlite.DatabaseHelper;
import br.cad.dao.system.ResourceDao;
import br.cad.dao.system.sqlite.ResourceDaoSqLite;
import br.cad.model.system.Resource;

import com.j256.ormlite.dao.Dao;

@EFragment()
public class ChamadaFragment extends Fragment {

	/*
	 * *****************************************************************************************************************
	 * ************************************************** Atributos ****************************************************
	 * *****************************************************************************************************************
	 */

	private final String LOG_NAME = getClass().getName();

	@OrmLiteDao(helper = DatabaseHelper.class, model = Resource.class)
	Dao<Resource, Long> resourceDao;

	ProgressDialog progress;

	/*
	 * *****************************************************************************************************************
	 * ************************************************** Construtor ***************************************************
	 * *****************************************************************************************************************
	 */

	public ChamadaFragment() {

	}

	/*
	 * *****************************************************************************************************************
	 * ************************************************* Gets e Sets ***************************************************
	 * *****************************************************************************************************************
	 */

	private ResourceDao getResourceDao() {
		return new ResourceDaoSqLite(resourceDao.getConnectionSource());
	}

	/*
	 * *****************************************************************************************************************
	 * *************************************************** Metodos *****************************************************
	 * *****************************************************************************************************************
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chamada, container, false);

		return rootView;
	}

	@Click(R.id.button1)
	void onClick() {
		boolean conectado;
		ConnectivityManager conectivtyManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conectivtyManager.getActiveNetworkInfo() != null
				&& conectivtyManager.getActiveNetworkInfo().isAvailable()
				&& conectivtyManager.getActiveNetworkInfo().isConnected()) {
			conectado = true;
		} else {
			conectado = false;
		}
		
		Log.i(LOG_NAME, String.valueOf(conectado));
		
		progress = ProgressDialog.show(getActivity(), "dialog title", String.valueOf(conectado), true);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				progress.dismiss();
			}
		}, 5 * 1000);
	}
}
