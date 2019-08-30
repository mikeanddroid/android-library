/* Copyright Urban Airship and Contributors */

package com.urbanairship.debug.deviceinfo

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout

import com.urbanairship.debug.R
import com.urbanairship.util.UAStringUtil

import com.urbanairship.debug.databinding.UaFragmentDeviceInfoTagsBinding
import com.urbanairship.debug.extensions.setupToolbarWithNavController

/**
 * Fragment that manages Urban Airship tags.
 */
class DeviceInfoTagsFragment : androidx.fragment.app.Fragment() {

    private lateinit var viewModel: DeviceInfoTagsViewModel
    private lateinit var addTagButton: ImageButton
    private lateinit var addTagEditText: EditText
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(DeviceInfoTagsViewModel::class.java)
        val binding = UaFragmentDeviceInfoTagsBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.setViewModel(viewModel)

        this.addTagButton = binding.addTagButton
        this.addTagEditText = binding.addTagText
        this.recyclerView = binding.recyclerView

        initAddTag()
        initTagList()

        return binding.getRoot()
    }

    private fun initTagList() {
        val tagAdapter = DeviceInfoTagAdapter()
        viewModel.getTags().observe(this, Observer<List<String>> { tagAdapter.submitList(it) })

        recyclerView.adapter = tagAdapter
        recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context!!, LinearLayout.VERTICAL))

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
                return ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, viewHolder1: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, i: Int) {
                val tag = (viewHolder as DeviceInfoTagAdapter.ViewHolder).tag
                viewModel.removeTag(tag)
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbarWithNavController(R.id.toolbar)
    }

    private fun initAddTag() {
        addTagButton.setOnClickListener {
            addTag()
            addTagEditText.text.clear()
        }

        addTagEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTag()
            }
            false
        }

        addTagEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    addTag()
                }
            }
            false
        }
    }

    private fun addTag() {
        val tag = addTagEditText.text.toString().trim { it <= ' ' }
        if (!UAStringUtil.isEmpty(tag)) {
            viewModel.addTag(tag)
        }
        addTagEditText.text.clear()
    }

}