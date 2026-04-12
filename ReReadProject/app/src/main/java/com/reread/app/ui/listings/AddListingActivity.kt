package com.reread.app.ui.listings

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.reread.app.R
import com.reread.app.utils.SessionManager

class AddListingActivity : AppCompatActivity() {

    private lateinit var ivBookImage: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etIsbn: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCondition: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                ivBookImage.setImageURI(uri)
                ivBookImage.visibility = View.VISIBLE
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = cameraImageUri
            ivBookImage.setImageURI(cameraImageUri)
            ivBookImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Listing"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ivBookImage      = findViewById(R.id.iv_book_image)
        btnPickImage     = findViewById(R.id.btn_pick_image)
        etTitle          = findViewById(R.id.et_title)
        etAuthor         = findViewById(R.id.et_author)
        etIsbn           = findViewById(R.id.et_isbn)
        etPrice          = findViewById(R.id.et_price)
        etDescription    = findViewById(R.id.et_description)
        spinnerCondition = findViewById(R.id.spinner_condition)
        spinnerCategory  = findViewById(R.id.spinner_category)
        btnSubmit        = findViewById(R.id.btn_submit)
        progressBar      = findViewById(R.id.progress_bar)

        // Check if editing existing listing
        val editBookId = intent.getIntExtra("edit_book_id", -1)
        if (editBookId != -1) {
            supportActionBar?.title = "Edit Listing"
            btnSubmit.text = "Update Listing"
            etTitle.setText(intent.getStringExtra("edit_title"))
            etAuthor.setText(intent.getStringExtra("edit_author"))
            etIsbn.setText(intent.getStringExtra("edit_isbn"))
            etPrice.setText(intent.getDoubleExtra("edit_price", 0.0).toString())
            etDescription.setText(intent.getStringExtra("edit_description"))
        }

        setupSpinners()
        btnPickImage.setOnClickListener { showImagePickerDialog() }
        btnSubmit.setOnClickListener { submitListing(editBookId) }
    }

    private fun setupSpinners() {
        val conditions = arrayOf("New", "Like New", "Good", "Fair", "Poor")
        spinnerCondition.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, conditions
        )
        val categories = arrayOf("Academic", "Fiction", "Non-Fiction", "Biography", "Documentary")
        spinnerCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )
    }

    private fun showImagePickerDialog() {
        AlertDialog.Builder(this)
            .setTitle("Add Photo")
            .setItems(arrayOf("Take Photo", "Choose from Gallery")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "book_photo_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.DESCRIPTION, "Book listing photo")
        }
        cameraImageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        }
        cameraLauncher.launch(intent)
    }

    private fun submitListing(editBookId: Int) {
        val title       = etTitle.text.toString().trim()
        val author      = etAuthor.text.toString().trim()
        val isbn        = etIsbn.text.toString().trim()
        val priceStr    = etPrice.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val condition   = spinnerCondition.selectedItem.toString()
        val category    = spinnerCategory.selectedItem.toString()

        if (title.isBlank())  { etTitle.error  = "Required"; return }
        if (author.isBlank()) { etAuthor.error = "Required"; return }
        if (priceStr.isBlank()) { etPrice.error = "Required"; return }
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) { etPrice.error = "Enter a valid price"; return }

        progressBar.visibility = View.VISIBLE
        btnSubmit.isEnabled    = false

        val session   = SessionManager(this)
        val imagePath = selectedImageUri?.toString() ?: intent.getStringExtra("edit_image") ?: ""
        val repo      = com.reread.app.data.WritableBookRepository(this)

        if (editBookId != -1) {
            repo.updateBook(editBookId, title, author, isbn, price, condition, category, description, imagePath)
            Toast.makeText(this, "Listing updated!", Toast.LENGTH_SHORT).show()
        } else {
            repo.addBook(session.userId, title, author, isbn, price, condition, category, description, imagePath)
            Toast.makeText(this, "Listing posted!", Toast.LENGTH_SHORT).show()
        }

        progressBar.visibility = View.GONE
        setResult(Activity.RESULT_OK)
        finish()
    }
}