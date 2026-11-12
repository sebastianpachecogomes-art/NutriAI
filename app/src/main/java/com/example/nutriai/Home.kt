package com.example.nutriai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Pantalla "Crear cuenta": nombre, contraseña y confirmación. Guarda en Room.
class Home : AppCompatActivity() {

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var confirmPasswordInput: EditText
    lateinit var createAccountBtn: Button
    lateinit var loginLinkBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.confirm_password_input)
        createAccountBtn = findViewById(R.id.create_account_btn)
        loginLinkBtn = findViewById(R.id.login_link_btn)

        createAccountBtn.setOnClickListener { crearCuenta() }

        // "¿Ya tienes una cuenta?": cierra esta pantalla y deja ver el login
        // (MainActivity sigue en la pila, justo debajo).
        loginLinkBtn.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun crearCuenta() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmacion = confirmPasswordInput.text.toString()

        if (username.isBlank() || password.isBlank() || confirmacion.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmacion) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Room no deja tocar la base de datos en el hilo principal: va en una corrutina (IO).
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = DatosApplication.database.datosDao()
            // Misma comparación exacta que usa el login, para que ambos coincidan.
            val yaExiste = dao.getAllDatos().any { it.usuario == username }

            if (!yaExiste) {
                dao.addDatos(Datos(usuario = username, contrasena = password))
            }

            withContext(Dispatchers.Main) {
                if (yaExiste) {
                    Toast.makeText(
                        this@Home,
                        "Ese nombre de usuario ya está registrado",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@Home,
                        "Cuenta creada. Ahora inicia sesión.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // vuelve al login
                }
            }
        }
    }
}