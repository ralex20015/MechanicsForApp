package me.armandosalazar.mechanicsforapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import me.armandosalazar.mechanicsforapp.dao.DAO;
import me.armandosalazar.mechanicsforapp.models.User;

public class LoginActivity extends AppCompatActivity {

    // Share Preferences instance
    private SharedPreferences sharedPreferences;

    private TextInputLayout emailContainer, passContainer;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init share preferences
        sharedPreferences = getSharedPreferences("users.dat", MODE_PRIVATE);

        // init components
        emailContainer = findViewById(R.id.txtInputEmail);
        passContainer = findViewById(R.id.txtInputPassword);

        remember = findViewById(R.id.cbRemember);

        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // clear all preferences
        // sharedPreferences.edit().clear().apply();

        // get all preferences
        Log.e("PREFERENCES", sharedPreferences.getAll().toString());

        // get users
//        ArrayList<User> users = DAO.getInstance(sharedPreferences).getUsers();
//        if (users != null) {
//            for (User user : users) {
//                Log.e("USERS", user.toString());
//            }
//        } else {
//            Log.e("USERS", "No hay usuarios");
//        }

        // check if user is logged
        if (sharedPreferences.getBoolean("isLogged", false)) {
            Intent intent = new Intent(this, MenuActivity.class);
            User user = getDataOfUserLogged();
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }

    }
    public void login(View view) {
//        User user;
//        user = DAO.getInstance(sharedPreferences).userExist(email, password);

        checkUser();
//        if (user != null) {
//            if (remember.isChecked()) {
//                saveSession();
//                // save user information
//                DAO.getInstance(sharedPreferences).saveUser(user);
//            }
//            Log.e("LOGIN", user.getEmail() + " - " + user.getPassword());
//            Intent intent = new Intent(this, MenuActivity.class);
//            intent.putExtra("user", user);
//            startActivity(intent);
//            finish();
//        } else {
//            showAlertDialog("Usuario o contraseña incorrecta!");
//        }

    }


    public void back(View view) {
        finish();
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder cuadroAlert = new AlertDialog.Builder(LoginActivity.this);
        cuadroAlert.setTitle("Verifique los campos");

        cuadroAlert.setMessage(message).setPositiveButton("OK", (dialogInterface, i) -> {
        }).show();
    }

    public void checkUser(){
        String username = String.valueOf(Objects.requireNonNull(emailContainer.getEditText()).getText());
        String password = String.valueOf(Objects.requireNonNull(passContainer.getEditText()).getText());

        DatabaseReference mechanicReference = FirebaseDatabase.getInstance().getReference("mechanics");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = userReference.orderByChild("username").equalTo(username);
        User user  = new User();
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String passwordFromBd = snapshot.child(username).child("password").getValue(String.class);
                    if (Objects.equals(passwordFromBd,password)){
                        String name = snapshot.child(username).child("name").getValue(String.class);
                        String email = snapshot.child(username).child("email").getValue(String.class);
                        String userName = snapshot.child(username).child("username").getValue(String.class);
                        user.setName(name);
                        user.setEmail(email);
                        user.setUsername(userName);
                        user.setPassword(password);
                        user.setRegistered(true);
                        Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
                        intent.putExtra("user",user);
                        startActivity(intent);
                        if (remember.isChecked()){
                            saveSession(user);
                        }
                        finish();
                    }else{
                        showAlertDialog("Verifique su contraseña");
                    }
                }else{
                    showAlertDialog("El usuario no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveSession(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogged", true);
        editor.putString("name", user.getName());
        editor.putString("username",user.getUsername());
        editor.putString("password",user.getPassword());
        editor.putString("email",user.getEmail());
        editor.apply();
    }

    private User getDataOfUserLogged(){
        return new User(
                sharedPreferences.getString("name",""),
                sharedPreferences.getString("username",""),
                sharedPreferences.getString("email",""),
                sharedPreferences.getString("password",""),
                sharedPreferences.getBoolean("isLogged",true));
    }
}