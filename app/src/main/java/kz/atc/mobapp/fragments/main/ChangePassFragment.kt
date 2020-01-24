package kz.atc.mobapp.fragments.main


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_change_pass.*

import kz.atc.mobapp.R
import kz.atc.mobapp.models.main.BlockUnblockDataModel
import kz.atc.mobapp.models.main.ChangePassModel
import kz.atc.mobapp.presenters.main.ChangePassPresenter
import kz.atc.mobapp.states.main.BlockUnblockDialogState
import kz.atc.mobapp.states.main.ChangePassState
import kz.atc.mobapp.views.main.ChangePassView

class ChangePassFragment : MviFragment<ChangePassView, ChangePassPresenter>(), ChangePassView {

    override fun render(state: ChangePassState) {
        when (state) {
            is ChangePassState.Processed -> {
                btnChangePass.isEnabled = true
                etCurrentPass.isEnabled = true
                etNewPass.isEnabled = true
                Toast.makeText(context, state.result, Toast.LENGTH_LONG).show()
            }
            is ChangePassState.Loading -> {
                btnChangePass.isEnabled = false
                etCurrentPass.isEnabled = false
                etNewPass.isEnabled = false
            }
            is ChangePassState.ValidationError -> {
                if (state.errCurrPass != null) {
                    layoutTextInputCurrentPass.error = state.errCurrPass
                }
                if (state.errNewPass != null) {
                    layoutTextInputNewPass.error = state.errNewPass
                }
            }
        }
    }

    override fun createPresenter() = ChangePassPresenter(context!!)

    override fun processIntent(): Observable<ChangePassModel> {
        return RxView.clicks(btnChangePass)
            .map<ChangePassModel> {
                Log.d("debug","test")
                ChangePassModel(etCurrentPass.text.toString(), etNewPass.text.toString())
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_pass, container, false)
    }


}
