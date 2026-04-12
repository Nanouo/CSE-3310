package com.reread.app.ui.listings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reread.app.R
import com.reread.app.data.BookRepository
import com.reread.app.data.WritableBookRepository
import com.reread.app.utils.SessionManager

class MyListingsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: View

    private val addListingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshListings()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_my_listings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_my_listings)
        tvEmpty      = view.findViewById(R.id.tv_empty_listings)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.btn_add_listing).setOnClickListener {
            addListingLauncher.launch(Intent(requireContext(), AddListingActivity::class.java))
        }

        refreshListings()
    }

    private fun refreshListings() {
        val session = SessionManager(requireContext())
        val books   = BookRepository(requireContext()).getBooksBySeller(session.userId).toMutableList()

        val adapter = MyListingsAdapter(
            books = books,
            onEdit = { book ->
                val intent = Intent(requireContext(), AddListingActivity::class.java).apply {
                    putExtra("edit_book_id",    book.id)
                    putExtra("edit_title",      book.title)
                    putExtra("edit_author",     book.author)
                    putExtra("edit_isbn",       book.isbn)
                    putExtra("edit_price",      book.price)
                    putExtra("edit_description",book.description)
                    putExtra("edit_condition",  book.condition)
                    putExtra("edit_category",   book.category)
                    putExtra("edit_image",      book.imagePath)
                }
                addListingLauncher.launch(intent)
            },
            onDelete = { book ->
                WritableBookRepository(requireContext()).deleteBook(book.id)
                refreshListings()
                Toast.makeText(requireContext(), "Listing deleted", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter       = adapter
        tvEmpty.visibility         = if (books.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility    = if (books.isEmpty()) View.GONE else View.VISIBLE
    }
}