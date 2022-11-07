package com.example.myappsegundo;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myappsegundo.Json.Digest;
import com.example.myappsegundo.Json.MyInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    TextView bienvenidoLabel, continuarLabel, SelectDate, nuevoUsuario, sexoLabel;
    ImageView logoImageMedical;
    TextInputLayout usuarioTextField, nameTextField, emailTextField, contraseñaTextField, fec, num;
    TextInputEditText nombreedit,usuarioedit,contrasenaedit,correoedit,fechaedit;
    Button registrocuenta;
    RadioGroup radioGroup;
    RadioButton Masculino, Femenino;

    public static final String archivo ="registro.json";
    private String json2;

    private File getFile(){
        return new File(getDataDir(), archivo);
    }

    private boolean writeFile(String text){
        File file = null;
        FileOutputStream fileOutputStream = null;
        try {
            file = getFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        List<MyInfo> list = new ArrayList<MyInfo>();

        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById(R.id.continuarLabel);
        SelectDate = findViewById(R.id.SelectDate);
        sexoLabel = findViewById(R.id.sexoLabel);
        logoImageMedical = findViewById(R.id.logoImageMedical);

        usuarioedit = findViewById(R.id.usuarioEditText);
        nombreedit = findViewById(R.id.nameEditText);
        correoedit = findViewById(R.id.emailEditText);
        contrasenaedit = findViewById(R.id.contraEditText);
        fechaedit = findViewById(R.id.fec);
        num = findViewById(R.id.num);
        nuevoUsuario = findViewById(R.id.inicioSesion);
        radioGroup = findViewById(R.id.radioGroup);
        Masculino = findViewById(R.id.Masculino);
        Femenino = findViewById(R.id.Femenino);

        registrocuenta = findViewById(R.id.Registrarte);

        registrocuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombreedit.getText().length()==0||usuarioedit.getText().length()==0 || contrasenaedit.getText().length()==0){
                    Toast.makeText(getApplicationContext(), "Rellena los primeros 3 campos", Toast.LENGTH_LONG).show();
                    return;
                }

                MyInfo info = new MyInfo();

                info.setNombre(nombreedit.getText().toString());
                info.setUsuario(usuarioedit.getText().toString());
                info.setContra(contrasenaedit.getText().toString());
                info.setCorreo(correoedit.getText().toString());
                info.setFecha(fechaedit.getText().toString());

                List2Json(info, list);


                String Nombre = nombreedit.getText().toString();
                String Usuario = usuarioedit.getText().toString();
                String Contra = contrasenaedit.getText().toString();
                String Correo = correoedit.getText().toString();
                String Fecha = fechaedit.getText().toString();
                String SexoI = "";

                if(Masculino.isChecked()){
                    SexoI += "Masculino";
                }
                if(Femenino.isChecked()){
                    SexoI += "Femenino";
                }
                if(!Femenino.isChecked() && !Masculino.isChecked()){
                    SexoI = "No especificado";
                }

                Digest sha1 = new Digest();
                byte[] txtByte = sha1.createSha1(Nombre + Contra);
                String pswCifr = sha1.bytesToHex(txtByte);

                json2 = lista2Json(Nombre, Usuario,Contra,  pswCifr, Correo, Fecha, SexoI);

                try {
                    if (writeFile(json2)) {
                        vaciar();
                        Toast.makeText(getApplicationContext(), "Usuario Registrado", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
            }

        });

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionBack();
            }
        });
    }

    private void vaciar(){
        nombreedit.setText("");
        usuarioedit.setText("");
        contrasenaedit.setText("");
        correoedit.setText("");
        fechaedit.setText("");
        Femenino.setSelected(false);
        Masculino.setSelected(false);
    }

    public String lista2Json(String nom, String us, String contra,String cifra, String cor, String fecha, String sex) {
        MyInfo myInfo = null;
        Gson gson = null;
        String json = null;
        String mnsj = null;
        ArrayList list;

        myInfo = new MyInfo();
        myInfo.setNombre(nom);
        myInfo.setUsuario(us);
        myInfo.setContra(contra);
        myInfo.setCifra(cifra);
        myInfo.setCorreo(cor);
        myInfo.setFecha(fecha);
        myInfo.setSexo(sex);

        Log.d(TAG, "test");

        gson = new Gson();
        list = new ArrayList<MyInfo>();
        list.add(myInfo);
        json = gson.toJson(list, ArrayList.class);

        if (json != null) {
            Log.d(TAG, json);
            mnsj = "Archivo Creado";
        } else {
            mnsj = "Usuario no Registrado";
        }
        Toast.makeText(getApplicationContext(), mnsj, Toast.LENGTH_LONG).show();
        return json;
    }

    public void List2Json(MyInfo info, List<MyInfo> list){
        Gson gson = null;
        String json = null;
        gson = new Gson();
        list.add(info);
        json = gson.toJson(list, ArrayList.class);
        if(json == null){
            Log.d(TAG, "Error json");
        }
        else{
            Log.d(TAG, json);
            writeFile(json);
        }
        Toast.makeText(getApplicationContext(), "Registro completado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        transitionBack();
    }

    public void transitionBack() {
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);

        Pair [] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(logoImageMedical, "logoImageTrans");
        pairs[1] = new Pair<View, String>(bienvenidoLabel, "textTrans");
        pairs[2] = new Pair<View, String>(continuarLabel, "iniciaSesionTextTrans");
        pairs[3] = new Pair<View, String>(usuarioTextField, "emailInputTextTrans");
        pairs[4] = new Pair<View, String>(contraseñaTextField, "passwordInputTextTrans");
        pairs[5] = new Pair<View, String>(nuevoUsuario, "buttonSignInTrans");
        pairs[6] = new Pair<View, String>(registrocuenta, "newUserTrans");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistroActivity.this, pairs);
            startActivity(intent, options.toBundle());
        }
        else{
            startActivity(intent);
            finish();
        }
    }

}