package com.reread.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reread.app.R

class AdminBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnViewAllListings).setOnClickListener {
            Toast.makeText(requireContext(), "View All Listings", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnDeleteListings).setOnClickListener {
            Toast.makeText(requireContext(), "Delete Listings", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnManageUsers).setOnClickListener {
            Toast.makeText(requireContext(), "Manage Users", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnBanUsers).setOnClickListener {
            Toast.makeText(requireContext(), "Ban Users", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnViewAllOrders).setOnClickListener {
            Toast.makeText(requireContext(), "View All Orders", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnApproveListings).setOnClickListener {
            Toast.makeText(requireContext(), "Approve Listings", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "AdminBottomSheet"
    }
}