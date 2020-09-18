package com.trkj.sangfor.vpn;

import java.net.URL;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import android.os.AsyncTask;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import com.sangfor.ssl.IVpnDelegate;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuthManager;
import com.sangfor.ssl.common.ErrorCode;
import com.sangfor.ssl.BaseMessage;
import com.sangfor.ssl.IConstants.VPNMode;
import com.sangfor.ssl.LoginResultListener;

/**
 * Created by m2mbob on 16/5/6.
 */
public class VpnModule extends ReactContextBaseJavaModule implements LifecycleEventListener, LoginResultListener {

    private static Promise PROMISE;
    private boolean inited;
    private String mode;
    private String host;
    private int port;

    public VpnModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @ReactMethod
    public void init(final String mode, final String host, final int port, final Promise promise) {
        PROMISE = promise;
        AsyncTask task = new AsyncTask<Object, Object, Boolean>() {

          @Override
      		protected Boolean doInBackground(Object... params) {
      			return true;
      		}

          @Override
      		protected void onPostExecute(Boolean result) {
            SangforAuthManager sfAuth = SangforAuthManager.getInstance();
            try {
                if (!inited) {
                    sfAuth.setLoginResultListener(VpnModule.this);
                    // sfAuth.init(getCurrentActivity().getApplication(),
                    //     getCurrentActivity(),
                    //     VpnModule.this,
                    //     mode.equals("EasyApp") ? SangforAuthManager.AUTH_MODULE_EASYAPP : SangforAuthManager.AUTH_MODULE_L3VPN);
                    inited = true;
                }
                String ip = host.replaceAll("(?i)https://", "").replaceAll("(?i)http://", "");
                VpnModule.this.host = host;
                VpnModule.this.mode = mode;
                VpnModule.this.port = port;
                WritableMap map = Arguments.createMap();
                map.putString("success", "1");
                promise.resolve(map);
                // InetAddress iaddr = InetAddress.getByName(ip);
                // long lhost = VpnCommon.ipToLong(ip);
                // sfAuth.vpnInit(lhost, port);
            } catch (Exception e) {
                promise.reject(e.getMessage(), e);
            }
          }
        };
        task.execute();
    }

    @ReactMethod
    public void init(final String host, final int port, final Promise promise) {
        this.init("EasyApp", host, port, promise);
    }

    @ReactMethod
    public void login(final String username, final String password,
                    final Promise promise) {
        PROMISE = promise;
        AsyncTask task = new AsyncTask<Object, Object, Boolean>() {

            @Override
        		protected Boolean doInBackground(Object... params) {
        			   return true;
        		}

            @Override
        		protected void onPostExecute(Boolean result) {
              try{
                SangforAuthManager sfAuth = SangforAuthManager.getInstance();
                sfAuth.startPasswordAuthLogin(getCurrentActivity().
                  getApplication(), getCurrentActivity(), VPNMode.L3VPN,
                  new URL(VpnModule.this.host + ":" + VpnModule.this.port), username, password);
                } catch(Exception e) {
                  PROMISE.reject("failed", e.getMessage());
                }
            }
        };
        task.execute();
    }

    @ReactMethod
    public void logout(final Promise promise) {
        if (promise != null) {
            PROMISE = promise;
        }
        AsyncTask task = new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected Boolean doInBackground(Object... params) {
                 return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                SangforAuthManager.getInstance().vpnLogout();
            }
        };
        task.execute();
    }

    @Override
    public String getName() {
        return "RNSangforVpn";
    }

    @Override
  	public void onLoginProcess(int nextAuthType,  BaseMessage message) {
      if (message != null && message.getErrorStr() != null && message.getErrorStr().length() > 0) {
        this.onLoginFailed(message.getErrorCode(), message.getErrorStr());
      } else {
        this.resolve(nextAuthType + "");
      }
    }

    @Override
  	public void onLoginSuccess() {
      this.resolve("1");
    }

    private void resolve(String message) {
      WritableMap map = Arguments.createMap();
      map.putString("success", message);
      if (PROMISE != null) {
        PROMISE.resolve(map);
        PROMISE = null;
      }
    }

    @Override
  	public void onLoginFailed(ErrorCode errorCode, String errorStr) {
      if (PROMISE != null) {
        PROMISE.reject("failed", errorStr);
        PROMISE = null;
      }
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
      this.logout(null);
    }

}
