package org.jshobbysoft.simpleimageshow

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import org.jshobbysoft.simpleimageshow.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity() /* Activity context */)

        if (sharedPreferences.getString("userDirKey", null).isNullOrEmpty()) {
            sharedPreferences.edit()
                .putString("userDirKey", Environment.getExternalStorageDirectory().path).apply()
        }

//          https://stackoverflow.com/questions/68510202/uri-path-getting-a-file-list
        val directoryUriFromUser =
            Uri.parse(sharedPreferences.getString("userDirKey", null))
        val docRoot: DocumentFile? =
            DocumentFile.fromTreeUri(requireActivity().applicationContext, directoryUriFromUser)
        val imageFilesFromUser = docRoot?.listFiles()
        val docs: List<Uri>? = imageFilesFromUser?.map { it.uri }
        val docsMutable: MutableList<Uri>? = docs?.toMutableList()

//          Check if files in docsMutable are image files
//          https://stackoverflow.com/questions/13760269/android-how-to-check-if-file-is-image
        val validExtensions = listOf("jpg", "jpeg", "gif", "png")

//          https://stackoverflow.com/questions/69411299/kotlin-list-map-how-to-avoid-concurrentmodificationexception
        val iterator = docsMutable!!.iterator()

        while (iterator.hasNext()) {
            val item = iterator.next()
            val extensionName = item.toString().substring(item.toString().lastIndexOf(".") + 1)
            if (!validExtensions.contains(extensionName.lowercase())) {
                iterator.remove()
            }
        }

        binding.imageviewSecond.scaleType = ImageView.ScaleType.FIT_CENTER

//          update background color
        when (val userBgColor = sharedPreferences.getString("userBgColor", "white")) {
            "white" -> binding.CL1.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            "black" -> binding.CL1.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black
                )
            )
            "blue" -> binding.CL1.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue
                )
            )
            "red" -> binding.CL1.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            "green" -> binding.CL1.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            else -> println(userBgColor)
        }

//          Loop the images at the desired time interval
        var imageCounted = 0

        if (docsMutable.isNotEmpty()) {
//          https://stackoverflow.com/questions/67573348/how-to-change-picture-in-imageview-every-second-using-kotlin
            val userDelayTimeMillis = 1000 * sharedPreferences.getLong("userDelayTime", 1.toLong())
            mainHandler.post(object : Runnable {
                override fun run() {
                    binding.imageviewSecond.setImageURI(docsMutable.elementAt(imageCounted))
                    imageCounted++
                    if (imageCounted > docsMutable.size.minus(1)) {
                        imageCounted = 0
                    }
                    mainHandler.postDelayed(this, userDelayTimeMillis)
                }
            })
        } else {
            Toast.makeText(
                requireActivity().applicationContext,
                "No images in folder",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Kill looper to avoid NPE when navigating back to first fragment
        mainHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}