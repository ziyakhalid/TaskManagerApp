package uk.ac.tees.mad.q2252114// Import necessary libraries
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class Search : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

}

