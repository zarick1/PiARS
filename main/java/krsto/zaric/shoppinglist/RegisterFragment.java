package krsto.zaric.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HttpHelper httpHelper;
    public static String USERS_URL = "http://172.20.10.14:3000/users";

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        httpHelper = new HttpHelper();

        Button btnReg = v.findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);

                EditText etUsername = (EditText) v.findViewById(R.id.registerUsername);
                String username = etUsername.getText().toString();
                EditText etEmail = (EditText) v.findViewById(R.id.registerEmail);
                String email = etEmail.getText().toString();
                EditText etPassword = (EditText) v.findViewById(R.id.registerPassword);
                String password = etPassword.getText().toString();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Sva polja moraju biti popunjena!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DbHelper dbHelper = new DbHelper(getActivity());
                long rowInserted = dbHelper.insertUser(username, email, password);
//
//                if(rowInserted != -1)
//                    Toast.makeText(getActivity(), "Uspesna registracija!", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(getActivity(), "Neuspesna registracija!", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonobject = new JSONObject();

                            try {
                                jsonobject.put("username", username);
                                jsonobject.put("password", password);
                                jsonobject.put("email", email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String str;

                            if(httpHelper.postJSONObjectFromURL(USERS_URL, jsonobject)) {
                                str = "Uspesna registracija";
                            } else {
                                str = "Neuspesna registracija";
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