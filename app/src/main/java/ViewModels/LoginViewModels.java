package ViewModels;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iscsantoscastillo.ventapp.MainActivity;
import com.iscsantoscastillo.ventapp.R;
import com.iscsantoscastillo.ventapp.VerificarPassword;
import com.iscsantoscastillo.ventapp.databinding.ActivityVerificarEmailBinding;
import com.iscsantoscastillo.ventapp.databinding.ActivityVerificarPasswordBinding;

import java.util.HashMap;
import java.util.Map;

import Interface.IonClick;
import Library.MemoryData;
import Library.Networks;
import Library.Validate;
import Models.LoginModels;

public class LoginViewModels extends LoginModels implements IonClick {
    private View _view;
    private Activity _activity;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MemoryData memoryData;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static ActivityVerificarEmailBinding _bindingEmail;
    private static ActivityVerificarPasswordBinding _passBinding;
    private static String emailData;

    private FirebaseFirestore _db;
    private DocumentReference _documentRef;

    public LoginViewModels(Activity activity, ActivityVerificarEmailBinding bindingEmail, ActivityVerificarPasswordBinding passBinding){
        _activity = activity;
        _bindingEmail = bindingEmail;
        _passBinding = passBinding;
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View view){
        _view = view;
        switch (view.getId()){
            case R.id.email_sign_in_button:
                verificarEmail();
                break;
            case R.id.password_sign_in_button:
                login();
                break;
        }
    }
    private void verificarEmail(){
        boolean cancel = false;
        View focusView = null;
        _bindingEmail.email.setError(null);
        if(TextUtils.isEmpty(emailUI.getValue())){
            _bindingEmail.email.setError(_activity.getString(R.string.error_field_required));
            focusView = _bindingEmail.email;
            cancel = true;
        }else if(!isEmailValid(emailUI.getValue())){
            _bindingEmail.email.setError(_activity.getString(R.string.error_invalid_email));
            focusView = _bindingEmail.email;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }else {
            emailData = emailUI.getValue();
            _activity.startActivity(new Intent(_activity, VerificarPassword.class));
            _activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            //Toast.makeText(_activity, emailUI.getValue(), Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isEmailValid(String email){
        return Validate.isMail(email);
    }
    private void login(){
        boolean cancel = false;
        View focusView = null;
        _passBinding.password.setError(null);
        if(TextUtils.isEmpty(passwordUI.getValue())){
            _passBinding.password.setError(_activity.getString(R.string.error_field_required));
            focusView = _passBinding.password;
            cancel = true;
        }else if(!isPasswordValid(passwordUI.getValue())){
            _passBinding.password.setError(_activity.getString(R.string.error_invalid_password));
            focusView = _passBinding.password;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }else {

            if(new Networks(_activity).verificarConexion()) {
                mAuth.signInWithEmailAndPassword(emailData, passwordUI.getValue()).addOnCompleteListener(_activity, (task) -> {
                    if (task.isSuccessful()) {
                        //insertPrueba();
                        memoryData = MemoryData.getInstance(_activity);
                        memoryData.saveData("user", emailData);
                        _activity.startActivity(new Intent(_activity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        _activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        //String email = user.getEmail();
                        //Toast.makeText(_activity, "exito " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(_activity, "fallo de firebase", Toast.LENGTH_SHORT).show();
                        Snackbar.make(_view, R.string.invalid_credentials, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });
            }else{
                Snackbar.make(_view, R.string.networks, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }
    private boolean isPasswordValid(String password){
        return password.length() >= 6;
    }

    private void insertPrueba(){
        _db = FirebaseFirestore.getInstance();
        _documentRef = _db.collection("CollUsuarios").document("docUser1");
        Map<String, Object> user = new HashMap<>();
        user.put("Nombre", "Christian1");
        user.put("ApellidoPaterno", "Santos1");
        user.put("ApellidoMaterno", "Castillo1");
        user.put("Correo", "iscsantoscastillo1@gmail.com");
        //Permite INSERTAR un nuevo registro
        /*_documentRef.set(user).addOnCompleteListener((task) -> {

        });*/
        //Permite un UPDATE a un registro existente
        //_documentRef.update(user);

    }
}
