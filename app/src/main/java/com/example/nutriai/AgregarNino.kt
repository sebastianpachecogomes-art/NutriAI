package com.example.nutriai

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AgregarNino : AppCompatActivity() {

    private lateinit var usuario: String
    private lateinit var avatarImage: ImageView
    private lateinit var nombreInput: EditText
    private lateinit var edadInput: EditText
    private lateinit var pesoInput: EditText
    private lateinit var alturaInput: EditText
    private var fotoSeleccionadaPath: String? = null

    // "Contrato" que abre el selector de fotos del propio sistema (no pide permisos de galería completos)
    private val selectorImagen = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarImage.scaleType = ImageView.ScaleType.CENTER_CROP
            avatarImage.setPadding(0, 0, 0, 0)
            avatarImage.setImageURI(uri)

            lifecycleScope.launch(Dispatchers.IO) {
                val path = copiarImagenAInterno(uri)
                withContext(Dispatchers.Main) {
                    fotoSeleccionadaPath = path
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.agregar_nino_layout)

        usuario = intent.getStringExtra("usuario") ?: "invitado"

        avatarImage = findViewById(R.id.avatar_image)
        nombreInput = findViewById(R.id.nombre_input)
        edadInput = findViewById(R.id.edad_input)
        pesoInput = findViewById(R.id.peso_input)
        alturaInput = findViewById(R.id.altura_input)

        avatarImage.setOnClickListener {
            selectorImagen.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        findViewById<Button>(R.id.register_child_btn).setOnClickListener {
            registrarNino()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registrarNino() {
        val nombre = nombreInput.text.toString().trim()
        val edadTexto = edadInput.text.toString().trim()
        val pesoTexto = pesoInput.text.toString().trim()
        val alturaTexto = alturaInput.text.toString().trim()

        if (nombre.isBlank() || edadTexto.isBlank() || pesoTexto.isBlank() || alturaTexto.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val edad = edadTexto.toIntOrNull()
        val peso = pesoTexto.toDoubleOrNull()
        val altura = alturaTexto.toDoubleOrNull()

        if (edad == null || peso == null || altura == null) {
            Toast.makeText(this, "Edad, peso y altura deben ser números", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            DatosApplication.database.ninoDao().addNino(
                Nino(
                    nombre = nombre,
                    edad = edad,
                    peso = peso,
                    altura = altura,
                    fotoPath = fotoSeleccionadaPath,
                    usuarioPropietario = usuario
                )
            )

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AgregarNino, "$nombre registrado", Toast.LENGTH_SHORT).show()
                finish() // vuelve a "Mis niños", que se refresca solo en su onResume()
            }
        }
    }

    // Copia la imagen elegida al almacenamiento interno de la app: el URI que entrega
    // el selector de fotos no es confiable para guardarlo tal cual a largo plazo.
    private fun copiarImagenAInterno(uri: Uri): String? {
        return try {
            val archivoDestino = File(filesDir, "nino_${UUID.randomUUID()}.jpg")
            contentResolver.openInputStream(uri)?.use { entrada ->
                archivoDestino.outputStream().use { salida ->
                    entrada.copyTo(salida)
                }
            }
            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
