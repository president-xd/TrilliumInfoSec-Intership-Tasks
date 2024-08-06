package com.task.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class MainActivity : AppCompatActivity() {
    private lateinit var secretKey: SecretKey
    private lateinit var iv: ByteArray
    private val requestCodeStoragePermission = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Request storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCodeStoragePermission)
        }

        // Generate AES key and IV
        generateAESKeyAndIV()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            } else {
                // Encrypt and store the data
                val encryptedData = encryptData(username, password)
                saveDataToFile(encryptedData)
                Toast.makeText(this, "Data encrypted and stored successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateAESKeyAndIV() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        secretKey = keyGen.generateKey()
        iv = ByteArray(16)
        java.security.SecureRandom().nextBytes(iv)
    }

    private fun encryptData(username: String, password: String): Map<String, String> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val usernameBytes = username.toByteArray(StandardCharsets.UTF_8)
        val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)
        val encryptedUsername = cipher.doFinal(usernameBytes)
        val encryptedPassword = cipher.doFinal(passwordBytes)

        val encryptedUsernameBase64 = Base64.encodeToString(encryptedUsername, Base64.DEFAULT)
        val encryptedPasswordBase64 = Base64.encodeToString(encryptedPassword, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        val keyBase64 = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)

        return mapOf(
            "username" to encryptedUsernameBase64,
            "password" to encryptedPasswordBase64,
            "iv" to ivBase64,
            "key" to keyBase64
        )
    }

    private fun saveDataToFile(data: Map<String, String>) {
        val jsonData = Gson().toJson(data)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "encrypted_data.json")
        FileOutputStream(file).use { fos ->
            fos.write(jsonData.toByteArray(StandardCharsets.UTF_8))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeStoragePermission) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Storage permission is required to save the file", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
