package com.gabriel.myapplication

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

const val CHANNEL_ID = "channelId"
const val NOTIFICATION_ID = 0
const val PERMISSION_REQUEST_CODE = 1

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Dots Notificações"
        val descriptionText = "Registros de Ponto, Apontamentos"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "pickerChannel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Este trecho de codigo solicita da permissão para enviar notificação
         * isso já é efeito mais abaixo, quando clicado no botão, porém aqui,
         * será solicitado assim que o aplicativo for criado/inicializado.
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1° criar o canal de notificação (notification channel)
        createNotificationChannel(this)

        // Verifica o estado do serviço
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isServiceRunning = sharedPreferences.getBoolean(SERVICE_RUNNING_KEY, false)

        /*
         * Verifica se o serviço NÃO está em execução.
         */
        if (!isServiceRunning) {
            // Inicia o serviço se não estiver em execução
            val startIntent = Intent(this, RunningService::class.java).apply {
                action = RunningService.Actions.START.toString()
            }
            startService(startIntent)
        }
    }

    fun sendNotification(view: View) {
        // Verifique se a permissão foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        } else {
            // Envie a notificação
            showNotification()
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Dots Mobile - Apontamento #25042085")
            .setContentText("Um apontamento com o código #25042085 foi finalizado com sucesso.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
            // showAlert("Sucesso !!", "Sua notificação foi enviada com sucesso, confira na caixa de mensagens.")
        }
    }

    // Método p/Disparar um alerta na tela do usuário
    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    fun runService(view: View) {
        try {
            Intent(this, RunningService::class.java).also { intent ->
                intent.action = RunningService.Actions.START.toString()
                startService(intent)
            }
        } catch (e: Exception) {
            Log.e("Error", "Erro ao iniciar o serviço: ${e.message}")
        }
    }

    fun stopService(view: View) {
        Intent(applicationContext, RunningService::class.java).also {
            it.action = RunningService.Actions.STOP.toString()
            startService(it)
        }
    }

    /*
     * O serviço é pausado e a atividade é pausada
     */
//    override fun onDestroy() {
//        super.onDestroy()
//        // Para o serviço quando a atividade é destruída (opcional)
//        val stopIntent = Intent(this, RunningService::class.java).apply {
//            action = RunningService.Actions.STOP.toString()
//        }
//        startService(stopIntent)
//    }
}