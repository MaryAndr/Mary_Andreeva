package ru.filit.motiv.app.fragments.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_help.*
import ru.filit.motiv.app.BuildConfig
import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.main.HelpPresenter
import ru.filit.motiv.app.states.main.HelpState
import ru.filit.motiv.app.views.main.HelpView

class HelpFragment: MviFragment<HelpView, HelpPresenter>(), HelpView{

    private var startHelp = BehaviorSubject.create<Boolean>()

    override fun createPresenter() = HelpPresenter(context)

    override fun getMessengersIntent(): Observable<Boolean> {
        return startHelp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        version_app.text = "${resources.getText(R.string.version_app)} ${BuildConfig.VERSION_NAME}"
    }
    override fun render(state: HelpState){
        when (state ){
            is HelpState.NoMessengers ->{
                visibilityNoMessengersText(View.VISIBLE)
                bt_whatsapp.visibility = View.GONE
                bt_viber.visibility = View.GONE
                delimiter.visibility = View.GONE
            }
            is HelpState.BothMessengers-> {
                visibilityNoMessengersText(View.GONE)
                bt_whatsapp.visibility = View.VISIBLE
                bt_viber.visibility = View.VISIBLE
                delimiter.visibility = View.VISIBLE
            }
            is HelpState.ViberMessengers->{
                visibilityNoMessengersText(View.GONE)
                bt_viber.visibility = View.VISIBLE
                bt_whatsapp.visibility = View.GONE
                delimiter.visibility = View.GONE
            }
            is HelpState.WhatsappMessengers-> {
                visibilityNoMessengersText(View.GONE)
                bt_viber.visibility = View.GONE
                delimiter.visibility = View.GONE
                bt_whatsapp.visibility = View.VISIBLE
            }
        }
        bt_whatsapp.setOnClickListener {onClick("https://api.whatsapp.com/send?phone=+79002111211")}
        bt_viber.setOnClickListener {onClick("viber://add?number=79536037033")}
    }

    override fun onResume() {
        super.onResume()
        startHelp.onNext(true)
    }

    private fun visibilityNoMessengersText(visibility:Int){
        request_install_app.visibility = visibility
        tv_phone_number_support_service.visibility = visibility
        tv_call_is_free.visibility = visibility
    }

    private fun onClick (uri:String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(uri))
        startActivity(intent)
    }

}