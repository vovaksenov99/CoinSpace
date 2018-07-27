package com.example.alexmelnikov.coinspace.ui.add_new_account

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsContract
import javax.inject.Inject


class AddAccountFragment : Fragment(), AddAccountContract.View {

    @Inject
    override lateinit var presenter: AddAccountContract.Presenter

    private lateinit var mEditTextAccountName: EditText

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)

        val root = inflater.inflate(R.layout.fragment_add_account, container, false)

        mEditTextAccountName = root.findViewById(R.id.et_account_name)

        RevealCircleAnimatorHelper
                .create(this, container)
                .start(root)

        mEditTextAccountName.setInputType(InputType.TYPE_CLASS_TEXT)
        mEditTextAccountName.requestFocus()
        mEditTextAccountName.postDelayed({
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mEditTextAccountName, 0)
        }, 150)

        return root
    }

    companion object {
        fun newInstance(sourceView: View? = null) = AddAccountFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }
}