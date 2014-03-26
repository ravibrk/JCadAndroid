package br.cad.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			Button btnEnter = (Button) rootView.findViewById(R.id.btnEnter);
			
			btnEnter.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					msg(getActivity(), ((EditText) rootView.findViewById(R.id.txtUser)).getText().toString());
				}
			});
			
			return rootView;
		}
		
		
	}
	
	public static void msg(Activity activity, String msg) {
		Toast.makeText(activity, "Ol� " + msg, Toast.LENGTH_SHORT).show();
	}
}
