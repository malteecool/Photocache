package se.umu.cs.dv18mln.photocache

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException


class SignActivity : AppCompatActivity() {

    private lateinit var signInButton: SignInButton
    private lateinit var frombot: Animation
    private lateinit var zoomOut: Animation
    private lateinit var logoImg: ImageView
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            handleSignInResult(account)
            println("user signed in")
        } else {
            println("user not signed in.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_layout)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        logoImg = findViewById(R.id.signin_logo)
        zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoomout)
        logoImg.animation = zoomOut

        signInButton = findViewById(R.id.sign_in_button)
        frombot = AnimationUtils.loadAnimation(this, R.anim.frombot)
        signInButton.animation = frombot
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener { startSignin() }


    }

    private fun startSignin() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            handleSignInResult(account)
        } else {
            println("error signing in, error code $resultCode")
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        println(account?.id)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("account", account)
        startActivity(intent)
        this.finish()
    }

}