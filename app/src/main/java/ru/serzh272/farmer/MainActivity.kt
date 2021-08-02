package ru.serzh272.farmer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.serzh272.farmer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    companion object{
        const val REQUEST_CODE_PERMISSION_LOCATION = 1
    }
}