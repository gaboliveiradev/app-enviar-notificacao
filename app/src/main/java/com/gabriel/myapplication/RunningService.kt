package com.gabriel.myapplication

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.os.Handler
import androidx.core.app.NotificationCompat

const val RUNNING_CHANNEL_ID = "running_channel"
const val PREFS_NAME = "RunningServicePrefs"
const val SERVICE_RUNNING_KEY = "service_running"

class RunningService: Service() {
    /*
     * Vamos usar essa váriavel para armazenar (tru) para quando o serviço for iniciado e (false) para quando
     *  o serviço for pausado.
     */
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /*
     * Imagine que você tem uma loja online e um serviço de carrinho de compras. O método onBind()
     *  é como a caixa registradora da loja. Quando um cliente chega à caixa registradora para pagar
     *  suas compras, a caixa registradora lida com a transação (vinculação) e retorna o recibo
     * (objeto IBinder) para o cliente. Se o cliente apenas estiver olhando os produtos e não quiser
     *  comprar nada, não há necessidade de usar a caixa registradora (serviço), então não há vinculação.
     */
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /*
     * Agora, suponha que você seja o gerente dessa loja online e tem funcionários (serviço de entrega)
     * que entregam os produtos aos clientes. Quando um cliente faz um pedido (inicia o serviço), você
     * envia os detalhes do pedido (objeto Intent) para os funcionários e lhes diz como eles devem lidar
     * com o pedido (flags e startId). Dependendo da política da sua loja, você pode querer que os
     * funcionários sejam muito rápidos em entregar (START_STICKY) ou talvez não seja tão importante se
     * eles reiniciarem o processo de entrega se algo der errado (START_NOT_STICKY).
     */
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when(intent?.action) {
//            Actions.START.toString() -> start()
//            Actions.STOP.toString() -> stop()
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return START_STICKY
    }

//    private fun start() {
//        val notification = NotificationCompat.Builder(this, RUNNING_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Serviço em Execução")
//            .setContentText("O serviço foi iniciado e rodará em segundo plano.")
//            .build()
//
//        startForeground(1, notification)
//    }
    private fun start() {
        val notification = NotificationCompat.Builder(this, RUNNING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Serviço em Execução")
            .setContentText("O serviço foi iniciado e rodará em segundo plano.")
            .build()

        startForeground(1, notification)

        // Define o estado do serviço como em execução (true)
        sharedPreferences.edit().putBoolean(SERVICE_RUNNING_KEY, true).apply()

        // Agendar a notificação após 2 minutos
        Handler().postDelayed({
            sendNotification()
        }, 30 * 1000) // 2 minutos em milissegundos
    }

//    private fun stop() {
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        val notification = NotificationCompat.Builder(this, RUNNING_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Serviço Pausado")
//            .setContentText("O serviço foi finalizado com sucesso.")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Define a prioridade da notificação
//            .setAutoCancel(true)  // A notificação será removida quando clicada
//            .build()
//
//        notificationManager.notify(1, notification)
//
//        stopSelf()
//    }
    private fun stop() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, RUNNING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Serviço Pausado")
            .setContentText("O serviço foi finalizado com sucesso.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)

        stopForeground(true)
        stopSelf()

        // Define o estado do serviço como pausado/não em execução (false)
        sharedPreferences.edit().putBoolean(SERVICE_RUNNING_KEY, false).apply()
    }

    /*
     * Função p/Envio de Notificação
     */
    private fun sendNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, RUNNING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Dots Mobile - Apontamento #252548532")
            .setContentText("O apontamento de código #252548532 foi finalizado com sucesso.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    enum class Actions {
        START, STOP
    }
}