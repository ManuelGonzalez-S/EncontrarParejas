package com.example.encontrarparejas

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.animation.Animator
import android.animation.AnimatorInflater
import android.widget.GridLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private val images = mutableListOf(
        R.drawable.perro,
        R.drawable.gato,
        R.drawable.nemo,
        R.drawable.tortuga,
        R.drawable.leon,
        R.drawable.aguila
    )

    private var matchedPairs = 0
    private val cardViews = mutableListOf<RelativeLayout>()
    private val imagePairs = (images + images).shuffled().toMutableList()
    private var flippedCardIndices = mutableListOf<Int>()
    private val pairedCards = mutableSetOf<Int>()

    private lateinit var gridLayout: GridLayout

    private lateinit var mediaController: MediaController

    private lateinit var notificacionGta: MediaPlayer
    private lateinit var oofRoblox : MediaPlayer
    private lateinit var felicitacionesTrompeta : MediaPlayer

    private var haySonido = true;

    private lateinit var iconoSonido : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)

        iconoSonido = findViewById(R.id.iconoSonido)
        iconoSonido.setImageResource(R.drawable.altavozon)

        notificacionGta = MediaPlayer.create(this, R.raw.gtanotificacion)
        oofRoblox = MediaPlayer.create(this, R.raw.oofroblox)
        felicitacionesTrompeta = MediaPlayer.create(this, R.raw.trompetas)

        mediaController = MediaController(this)

        for (i in 0 until gridLayout.childCount) {
            val relativeLayout = gridLayout.getChildAt(i) as RelativeLayout
            cardViews.add(relativeLayout)
            relativeLayout.setOnClickListener { girarCarta(i) }

            // Aplicar la animación al CardView
            val rotation = AnimatorInflater.loadAnimator(this, R.animator.flip_animation)
            rotation.setTarget(relativeLayout)
            relativeLayout.tag = rotation
        }

        empezarJuego()
    }

    private fun comprobarPareja() {
        val card1 = flippedCardIndices[0]
        val card2 = flippedCardIndices[1]

        val cartaView1 = cardViews[card1]
        val cartaView2 = cardViews[card2]

        val imagen1 = cartaView1.getChildAt(0) as ImageView

        val imagen2 = cartaView2.getChildAt(0) as ImageView

        if (imagePairs[card1] == imagePairs[card2]) {

            if(haySonido && !notificacionGta.isPlaying){
                // Si no está reproduciendo, inicia la reproducción
                notificacionGta.start()

            }

            matchedPairs++
            pairedCards.add(card1)
            pairedCards.add(card2)

            if (matchedPairs == images.size) {
                showCongratulationsDialog()
            }
        } else {

            if(haySonido && !oofRoblox.isPlaying){
                oofRoblox.start()
            }

            imagen1.setImageResource(R.drawable.estrella)
            imagen2.setImageResource(R.drawable.estrella)
        }

        flippedCardIndices.clear()
    }

    private fun showCongratulationsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Felicidades!")
        builder.setMessage("Has encontrado todas las parejas ¿Qué deseas hacer?")

        builder.setPositiveButton("Repetir") { _, _ ->
            empezarJuego()
        }
        builder.setNegativeButton("Salir") { _, _ ->
            finish()
        }

        val dialog = builder.create()
        dialog.show()
        if(haySonido && !felicitacionesTrompeta.isPlaying){
            felicitacionesTrompeta.start()
        }
    }

    private fun empezarJuego() {
        matchedPairs = 0
        pairedCards.clear()

        for (relativeLayout in cardViews) {

            val imageView = relativeLayout.getChildAt(0) as ImageView

            imageView.setImageResource(R.drawable.estrella)
            imageView.tag = null
            relativeLayout.isEnabled = true
        }

        imagePairs.shuffle()
    }

    fun cambiarSonido(view : View){

        if(haySonido){
            haySonido = false

            iconoSonido.setImageResource(R.drawable.altavozoff)

        }else{

            haySonido = true

            iconoSonido.setImageResource(R.drawable.altavozon)
        }

    }


    private fun girarCarta(posicion: Int) {

        if (!flippedCardIndices.contains(posicion) && flippedCardIndices.size < 2 && !pairedCards.contains(posicion)) {

            val carta = cardViews[posicion]
            val imagen = carta.getChildAt(0) as ImageView

            val flipAnimator = ObjectAnimator.ofFloat(imagen, View.ROTATION_Y, 0f, 180f)

            flipAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    imagen.setImageResource(imagePairs[posicion])
                    imagen.tag = "girada"
                    flippedCardIndices.add(posicion)

                    if (flippedCardIndices.size == 2) {
                        desactivarCartas()

                        Handler().postDelayed({
                            comprobarPareja()
                            activarCartas()
                        }, 500)
                    }
                }
            })

            flipAnimator.start()
        }
    }

    private fun desactivarCartas() {
        for (cardView in cardViews) {
            cardView.isEnabled = false
        }
    }

    private fun activarCartas() {
        for (cardView in cardViews) {
            cardView.isEnabled = true
        }
    }


}
