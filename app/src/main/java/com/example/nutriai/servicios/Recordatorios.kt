package com.example.nutriai.servicios

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.nutriai.MainActivity
import java.util.Calendar

data class ConfigRecordatorio(val activo: Boolean, val hora: Int, val minuto: Int)

/** Guarda la configuración del recordatorio diario (RF09) y programa/cancela la alarma. */
class GestorRecordatorios(private val contexto: Context) {

    private val prefs = contexto.getSharedPreferences("recordatorio", Context.MODE_PRIVATE)

    fun configuracion(): ConfigRecordatorio =
        ConfigRecordatorio(
            activo = prefs.getBoolean(CLAVE_ACTIVO, false),
            hora = prefs.getInt(CLAVE_HORA, 20),
            minuto = prefs.getInt(CLAVE_MINUTO, 0)
        )

    fun activar(hora: Int, minuto: Int) {
        prefs.edit().putBoolean(CLAVE_ACTIVO, true).putInt(CLAVE_HORA, hora).putInt(CLAVE_MINUTO, minuto).apply()
        val proximo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val alarmas = contexto.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Repetición diaria inexacta: no requiere el permiso de alarmas exactas.
        alarmas.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, proximo.timeInMillis, AlarmManager.INTERVAL_DAY, intentoAlarma()
        )
    }

    fun desactivar() {
        prefs.edit().putBoolean(CLAVE_ACTIVO, false).apply()
        val alarmas = contexto.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmas.cancel(intentoAlarma())
    }

    private fun intentoAlarma(): PendingIntent =
        PendingIntent.getBroadcast(
            contexto, CODIGO_ALARMA, Intent(contexto, ReceptorRecordatorio::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    companion object {
        private const val CLAVE_ACTIVO = "activo"
        private const val CLAVE_HORA = "hora"
        private const val CLAVE_MINUTO = "minuto"
        private const val CODIGO_ALARMA = 1001
        private const val CANAL = "recordatorio_diario"
        private const val ID_NOTIFICACION = 2001

        fun tienePermisoNotificaciones(contexto: Context): Boolean =
            Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        @SuppressLint("MissingPermission")
        fun mostrarNotificacion(contexto: Context) {
            if (!tienePermisoNotificaciones(contexto)) return
            if (Build.VERSION.SDK_INT >= 26) {
                val canal = NotificationChannel(CANAL, "Recordatorio diario", NotificationManager.IMPORTANCE_DEFAULT)
                contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
            }
            val abrirApp = PendingIntent.getActivity(
                contexto, 0, Intent(contexto, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
            )
            val notificacion = NotificationCompat.Builder(contexto, CANAL)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("BitácoraVita")
                .setContentText("¿Ya registraste tu día? Anota tu alimentación, sueño y ánimo.")
                .setContentIntent(abrirApp)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(contexto).notify(ID_NOTIFICACION, notificacion)
        }
    }
}

/** Recibe la alarma diaria y muestra la notificación. */
class ReceptorRecordatorio : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        GestorRecordatorios.mostrarNotificacion(context)
    }
}
