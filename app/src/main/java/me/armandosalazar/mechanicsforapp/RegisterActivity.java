package me.armandosalazar.mechanicsforapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import me.armandosalazar.mechanicsforapp.models.Mechanic;
import me.armandosalazar.mechanicsforapp.models.User;

public class RegisterActivity extends AppCompatActivity {
    // Share Preferences instance
    private SharedPreferences sharedPreferences;

    private TextInputLayout txtLayoutName, txtLayoutLastName, txtLayoutPass, txtLayoutRepeatPass, txtLayoutEmail, txtLayoutRfc;
    private TextInputEditText txtName, txtUserName, txtPass, txtRepeatPass, txtEmail, txtRfc;
    private CheckBox cbMechanic;
    private Spinner spMechanicType;
    private String previousUsers;
    private int currentId;
    private final String[] typeOfMechanic = {"Seleccione un tipo", "Eléctrico", "General", "Hojalatería y pintura",
            "Mecánico Diesel", "Frenos y transmisión"};
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // init share preferences
        sharedPreferences = getSharedPreferences("mechanics.dat", MODE_PRIVATE);

        txtLayoutEmail = findViewById(R.id.txtInputNewEmail);
        txtLayoutLastName = findViewById(R.id.txtInputLastName);
        txtLayoutName = findViewById(R.id.txtInputName);
        txtLayoutPass = findViewById(R.id.txtInputNewPass);
        txtLayoutRepeatPass = findViewById(R.id.txtInputRepeatPass);
        txtLayoutRfc = findViewById(R.id.txtInputRfc);

        txtName = findViewById(R.id.txtName);
        txtUserName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtNewEmail);
        txtPass = findViewById(R.id.txtNewPass);
        txtRepeatPass = findViewById(R.id.txtRepeatPass);
        txtRfc = findViewById(R.id.txtRfc);

        spMechanicType = findViewById(R.id.spTypeOfMechanic);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, typeOfMechanic);

        spMechanicType.setAdapter(adapter);
        cbMechanic = findViewById(R.id.cbMechanic);
        cbMechanic.setOnClickListener(view -> {
            if (isAMechanic()) {
                txtRfc.setVisibility(View.VISIBLE);
                txtLayoutRfc.setVisibility(View.VISIBLE);
                spMechanicType.setVisibility(View.VISIBLE);
            } else {
                txtRfc.setVisibility(View.GONE);
                txtLayoutRfc.setVisibility(View.GONE);
                spMechanicType.setVisibility(View.GONE);
            }
        });

        abrirArchivo();
    }

    private boolean isAMechanic() {
        return cbMechanic.isChecked();
    }

    private void guardarArchivo() {
        try {
            //Objeto que asocia al archivo especificado, para escritura
            OutputStreamWriter archivoInterno = new OutputStreamWriter(
                    openFileOutput("users.txt", Activity.MODE_PRIVATE));
            archivoInterno.write(registerUserOnFile(previousUsers));
            archivoInterno.flush();
            archivoInterno.close();

        } catch (IOException e) {
            Toast.makeText(this, "Error al escribir en el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    public void registerUser(View view) {
        if (allFieldsFilled()) {
            database = FirebaseDatabase.getInstance();

            if (isAMechanic()) {
                databaseReference = database.getReference("mechanics");
                int indexSelected = spMechanicType.getSelectedItemPosition();
                String email = String.valueOf(txtEmail.getText());
                String password = String.valueOf(txtPass.getText());
                String username = String.valueOf(txtUserName.getText());
                Mechanic mechanic = new Mechanic();
                mechanic.setName(String.valueOf(txtName.getText()));
                mechanic.setUsername(String.valueOf(txtUserName.getText()));
                mechanic.setEmail(email);
                mechanic.setPassword(password);
                mechanic.setRegistered(true);
                mechanic.setRfc(String.valueOf(txtRfc));
                mechanic.setTypeOfMechanic(typeOfMechanic[indexSelected]);
                databaseReference.child(username).setValue(mechanic);
                Toast.makeText(this, "Registro exitoso!!", Toast.LENGTH_SHORT).show();
                //change this
//                DAO.getInstance(sharedPreferences).createMechanic(mechanic);
//                ArrayList<Mechanic> mechanics = DAO.getInstance(sharedPreferences).getMechanics();
//                for (Mechanic m :
//                        mechanics) {
//                    Log.d("REGISTER", mechanic.getEmail());
//
//                }

                finish();
            }
            databaseReference = database.getReference("users");
            String email = String.valueOf(txtEmail.getText());
            String username = String.valueOf(txtUserName.getText());
            User user = new User();
            user.setName(String.valueOf(txtName.getText()));
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(String.valueOf(txtPass.getText()));

            databaseReference.child(username).setValue(user);
            Toast.makeText(this, "Registro exitoso!!", Toast.LENGTH_SHORT).show();
            //Change this
//            DAO.getInstance(sharedPreferences).createUser(user);

            finish();
        } else {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allFieldsFilled() {
        if (isAMechanic()) {
            return !txtName.getText().toString().equals("") && !txtUserName.getText().toString().equals("") &&
                    !txtEmail.getText().toString().equals("") && !txtPass.getText().toString().equals("") &&
                    !txtRepeatPass.getText().toString().equals("") && !txtRfc.getText().toString().equals("")
                    && spMechanicType.getSelectedItemPosition() != 0;
        } else {
            return !txtName.getText().toString().equals("") && !txtUserName.getText().toString().equals("") &&
                    !txtEmail.getText().toString().equals("") && !txtPass.getText().toString().equals("") &&
                    !txtRepeatPass.getText().toString().equals("");
        }

    }

    private String registerUserOnFile(String currentContentOfTheFile) {
        int indexSpinner = spMechanicType.getSelectedItemPosition();
        StringBuilder stringBuilder = new StringBuilder();
        if (currentContentOfTheFile != null) {
            stringBuilder.append(currentContentOfTheFile);
            stringBuilder.append("\n");
            stringBuilder.append("Nombre: ");
            stringBuilder.append(txtName.getText().toString());
            stringBuilder.append("Apellido(s): ");
            stringBuilder.append(txtUserName.getText().toString());
            stringBuilder.append("Correo: ");
            stringBuilder.append(txtEmail.getText().toString());
            stringBuilder.append("Password: ");
            stringBuilder.append(txtPass.getText().toString());
            if (isAMechanic()) {
                stringBuilder.append("RFC: ");
                stringBuilder.append(txtRfc.getText().toString());
                stringBuilder.append("Tipo de mecanico: ");
                stringBuilder.append(spMechanicType.getItemAtPosition(indexSpinner));
            }

        } else {
            stringBuilder.append("\n");
            stringBuilder.append("Nombre: ");
            stringBuilder.append(txtName.getText().toString());
            stringBuilder.append("Apellido(s): ");
            stringBuilder.append(txtUserName.getText().toString());
            stringBuilder.append("Correo: ");
            stringBuilder.append(txtEmail.getText().toString());
            stringBuilder.append("Password: ");
            stringBuilder.append(txtPass.getText().toString());
            if (isAMechanic()) {
                stringBuilder.append("RFC: ");
                stringBuilder.append(txtRfc.getText().toString());
                stringBuilder.append("Tipo de mecanico: ");
                stringBuilder.append(spMechanicType.getItemAtPosition(indexSpinner));
            }
        }
        return stringBuilder.toString();
    }

    private void abrirArchivo() {
        String[] archivos = fileList();

        if (existeArchivo(archivos, "users.txt")) {
            try {
                //Objeto que asocia al archivo especificado, para lectura
                InputStreamReader archivoInterno = new
                        InputStreamReader(openFileInput("users.txt"));
                //Objeto que relaciona el arhicov con el flujo de entrada (lectura)
                BufferedReader leerArchivo = new BufferedReader(archivoInterno);
                String linea = leerArchivo.readLine();

                String textoLeido = "";

                while (linea != null) {
                    textoLeido += linea + '\n';
                    linea = leerArchivo.readLine();
                }

                leerArchivo.close();
                archivoInterno.close();
                previousUsers = textoLeido;
            } catch (IOException e) {
                Toast.makeText(this, "Error al leer el archivo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean existeArchivo(String[] archivos, String s) {
        for (String archivo : archivos) {
            if (s.equals(archivo)) {
                return true;
            }
        }
        return false;
    }


    public void back(View view) {
        finish();
    }
}