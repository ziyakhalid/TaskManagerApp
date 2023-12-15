package uk.ac.tees.mad.q2252114

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class Home : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var inProgressTasksAdapter: TasksAdapter
    private lateinit var taskCardAdapter: TaskCardAdapter
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imageView: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("isPermissionDialogShown", false)) {
            checkAndRequestPermissions()
        }


        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val taskRepository = TaskRepository(TaskDbHelper(requireContext(), uid), uid)
        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)



        val inProgressRecyclerView: RecyclerView = view.findViewById(R.id.progres_task_vertical)
        inProgressRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        inProgressTasksAdapter = TasksAdapter { selectedTask ->
            onTaskItemClick(selectedTask)
        }
        inProgressRecyclerView.adapter = inProgressTasksAdapter

        taskViewModel.allTasks.observe(viewLifecycleOwner) { inProgressTasks ->
            inProgressTasksAdapter.submitList(inProgressTasks)
            taskCardAdapter.submitList(inProgressTasks)
        }

        val inProgressItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedTask = inProgressTasksAdapter.currentList[position]
                taskViewModel.delete(deletedTask)

                Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        GlobalScope.launch {
                            taskViewModel.insert(deletedTask)
                        }
                    }
                    .show()
            }
        })

        inProgressItemTouchHelper.attachToRecyclerView(inProgressRecyclerView)

        val cardRecyclerView: RecyclerView = view.findViewById(R.id.Home_tasks_cards)
        cardRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        taskCardAdapter = TaskCardAdapter()
        cardRecyclerView.adapter = taskCardAdapter

        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            taskCardAdapter.submitList(tasks)
        }

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance()

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Firebase Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Get the current user
        currentUser = firebaseAuth.currentUser!!

        // Find the ImageView in your layout
        imageView= view.findViewById(R.id.profile_pic)

        // Set an OnClickListener for the ImageView
        imageView.setOnClickListener {
            // Open the camera to capture an image
            dispatchTakePictureIntent()
        }

        // Load the user's profile picture if available
        loadProfilePicture()

        return view
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGrantedPermissions = requiredPermissions.filter {
                checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }

            if (notGrantedPermissions.isNotEmpty()) {
                showPermissionExplanationDialog()
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permissions Required")
            .setMessage("This app requires certain permissions to function properly. Please grant the necessary permissions.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.CAMERA
                    ),
                    PERMISSION_REQUEST_CODE
                )
                sharedPreferences.edit().putBoolean("isPermissionDialogShown", true).apply()
            }
            .setNegativeButton("Exit App") { _, _ ->
                activity?.finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Upload the image to Firebase Storage
            uploadImageToFirebase(imageBitmap)
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val storageRef: StorageReference = firebaseStorage.reference
            .child("images/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL from the task snapshot
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUrl ->
                // Save the download URL to the Realtime Database
                saveProfileImageUrlToDatabase(downloadUrl.toString())

                // Set the downloaded image to the ImageView
                setDownloadedImageToImageView(downloadUrl.toString())
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

    private fun setDownloadedImageToImageView(imageUrl: String) {
        imageView?.let {
            Glide.with(requireContext())
                .load(imageUrl)
                .into(it)
        }
    }

    private fun saveProfileImageUrlToDatabase(profileImageUrl: String) {
        // Save the profile image URL to Firebase Realtime Database or Firestore
        val databaseReference = firebaseDatabase.reference.child("users").child(currentUser.uid)
        databaseReference.child("profileImageUrl").setValue(profileImageUrl)
    }

    private fun loadProfilePicture() {
        // Load the user's profile picture from the Realtime Database
        val databaseReference = firebaseDatabase.reference.child("users").child(currentUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)
                    if (!profileImageUrl.isNullOrEmpty()) {
                        // If profile image URL is available, load it into the ImageView
                        Glide.with(requireContext())
                            .load(profileImageUrl)
                            .into(imageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun onTaskItemClick(task: Task) {
        // Handle task item click
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
