package com.example.nutriai

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Pantalla a la que se llega SOLO tras un login exitoso: lista de "Mis niños".
class Dashboard : AppCompatActivity() {

    private lateinit var usuario: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var resultsCount: TextView
    private lateinit var adapter: NinoAdapter
    private var listaCompleta: List<Nino> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.dashboard_layout)

        usuario = intent.getStringExtra("usuario") ?: "invitado"

        recyclerView = findViewById(R.id.ninos_recycler_view)
        searchInput = findViewById(R.id.search_input)
        resultsCount = findViewById(R.id.results_count)

        adapter = NinoAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.add_child_btn).setOnClickListener {
            val intent = Intent(this, AgregarNino::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        searchInput.addTextChangedListener { texto ->
            filtrarLista(texto.toString())
        }

        // Accesos rápidos y el resto de la barra inferior: todavía no construidos.
        val proximamente: (View) -> Unit = { Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show() }
        findViewById<View>(R.id.quick_assistant).setOnClickListener(proximamente)
        findViewById<View>(R.id.quick_reminders).setOnClickListener(proximamente)
        findViewById<View>(R.id.quick_evaluation).setOnClickListener(proximamente)
        findViewById<View>(R.id.nav_inicio).setOnClickListener(proximamente)
        findViewById<View>(R.id.nav_asistente).setOnClickListener(proximamente)
        findViewById<View>(R.id.nav_recordatorios).setOnClickListener(proximamente)
        findViewById<View>(R.id.nav_perfil).setOnClickListener(proximamente)
        // nav_ninos no necesita listener: ya estás en esta pantalla.

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNinos() // recarga cada vez que vuelves (p. ej. después de agregar un niño nuevo)
    }

    private fun cargarNinos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val ninos = DatosApplication.database.ninoDao().getNinosPorUsuario(usuario)
            withContext(Dispatchers.Main) {
                listaCompleta = ninos
                filtrarLista(searchInput.text.toString())
            }
        }
    }

    private fun filtrarLista(texto: String) {
        val filtrada = if (texto.isBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter { it.nombre.contains(texto, ignoreCase = true) }
        }
        adapter.actualizarLista(filtrada)
        resultsCount.text = "${filtrada.size} resultados encontrados"
    }
}
