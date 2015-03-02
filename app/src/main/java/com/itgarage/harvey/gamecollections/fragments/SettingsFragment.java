package com.itgarage.harvey.gamecollections.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.UiLifecycleHelper;
import com.itgarage.harvey.gamecollections.R;
import com.itgarage.harvey.gamecollections.activities.LoginTestActivity;
import com.itgarage.harvey.gamecollections.activities.NaviDrawerActivity;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    /*private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };*/

    private UiLifecycleHelper uiHelper;
    private NaviDrawerActivity activity;

    Button btnLogin;
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container,
                false);

        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getContext(), LoginTestActivity.class);
                startActivity(intent);
            }
        });
        /*uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);
        authButton.setFragment(this);
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }*/


        //Log.d("LogTag", "my ID is " + activity.credentialsProvider.getIdentityId());

        //uiHelper.onResume();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NaviDrawerActivity) activity).onSectionAttached(4);
        this.activity = ((NaviDrawerActivity) activity);
    }


    /*private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("graph.facebook.com", Session.getActiveSession().getAccessToken());
            activity.credentialsProvider.withLogins(logins);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }*/
}
