package krsto.zaric.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HttpHelper httpHelper;
    public static String LOGIN_URL = "http://172.20.10.14:3000/login";

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        httpHelper = new HttpHelper();

        Button btnLog = v.findViewById(R.id.btnLogin);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etUsername = (EditText) v.findViewById(R.id.loginUsername);
                String username = etUsername.getText().toString();
                EditText etPassword = (EditText) v.findViewById(R.id.loginPassword);
                String password = etPassword.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Sva polja moraju biti popunjena!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                DbHelper dbHelper = new DbHelper(getActivity());
//
//                if(dbHelper.readUser(username, password)){
//                    Toast.makeText(getActivity(), "Uspesno logovanje!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
//                    intent.putExtra("username", username);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "Neuspesno logovanje!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonobject = new JSONObject();

                            try {
                                jsonobject.put("username", username);
                                jsonobject.put("password", password);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String str;
                            Intent intent;

                            if(httpHelper.postJSONObjectFromURL(LOGIN_URL, jsonobject)) {
                                str = "Uspesno logovanje";
                                intent = new Intent(getActivity(), WelcomeActivity.class);
                                intent.putExtra("username", username);
                            } else {
                                str = "Neuspesno logovanje";
                                intent = new Intent(getActivity(), LoginActivity.class);
                            }

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        return v;
    }
}