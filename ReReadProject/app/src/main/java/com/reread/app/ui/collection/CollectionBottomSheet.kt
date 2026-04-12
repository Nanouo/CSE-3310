package com.reread.app.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reread.app.R
import com.reread.app.data.CollectionRepository
import com.reread.app.utils.SessionManager

class CollectionBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.bottom_sheet_collection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        val repo    = CollectionRepository(requireContext())
        val books   = repo.getPurchasedBooks(session.userId)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_collection)
        val tvEmpty      = view.findViewById<View>(R.id.tv_empty)

        if (books.isEmpty()) {
            tvEmpty.visibility    = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility    = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CollectionAdapter(books)
        }
    }
}