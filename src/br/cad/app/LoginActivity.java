package br.cad.app;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import br.cad.util.Utils;

/**
 * 
 * @author William Rodrigues
 * 
 */
@EActivity()
public class LoginActivity extends Activity {
	
	@ViewById(R.id.webview_login)
	WebView webLogin;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		webLogin.getSettings().setJavaScriptEnabled(true);
		webLogin.addJavascriptInterface(new LoginInterface(this), "login");

		webLogin.loadUrl("file:///android_asset/www/login.html");
	}
	
	void onClose() {
		finish();
	}

	public class LoginInterface {
		Context mContext;
		
		private String user;
		private String password;
		
		LoginInterface(Context c) {
			mContext = c;
		}

		@JavascriptInterface
		public String getUser() {
			return user;
		}

		@JavascriptInterface
		public void setUser(String user) {
			this.user = user;
		}
		
		@JavascriptInterface
		public String getPassword() {
			return password;
		}

		@JavascriptInterface
		public void setPassword(String password) {
			this.password = password;
		}
		
		@JavascriptInterface
		public void loggin() {
			// FIXME: implementar logica do login
//			if(user.equals("williamrodrigues") && password.equals("123456")) {
				onClose();
			
				Utils.startActivity(LoginActivity.this, TesteActivity_.class);
//			}
//			else {
//				Utils.msg(LoginActivity.this, "Usuário ou senha invalidos!");
//			}
		}
	}
}