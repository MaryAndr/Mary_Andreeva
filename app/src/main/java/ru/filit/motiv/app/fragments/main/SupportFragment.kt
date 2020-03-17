package ru.filit.motiv.app.fragments.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.hannesdorfmann.mosby3.mvi.MviFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_support.*
import ru.filit.motiv.app.BuildConfig
import ru.filit.motiv.app.R
import ru.filit.motiv.app.presenters.main.SupportPresenter
import ru.filit.motiv.app.states.main.SupportState
import ru.filit.motiv.app.views.main.SupportView

class SupportFragment: MviFragment<SupportView, SupportPresenter>(), SupportView{

    private var startHelp = BehaviorSubject.create<Boolean>()

    override fun createPresenter() = SupportPresenter(context)

    override fun getMessengersIntent(): Observable<Boolean> {
        return startHelp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.costs)))
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.abs_layout)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_backbutton_black)
            elevation = resources.getDimension(R.dimen.elevation)
        }
        val tvTitle: AppCompatTextView = activity!!.findViewById(R.id.tvTitle)
        tvTitle.setTextColor(resources.getColor(R.color.black))
        tvTitle.text = getString(R.string.support)
        version_app.text = "${resources.getText(R.string.version_app)} ${BuildConfig.VERSION_NAME}"
    }
    override fun render(state: SupportState){
        when (state ){
            is SupportState.NoMessengers ->{
                visibilityNoMessengersText(View.VISIBLE)
                bt_whatsapp.visibility = View.GONE
                bt_viber.visibility = View.GONE
                delimiter.visibility = View.GONE
            }
            is SupportState.BothMessengers-> {
                visibilityNoMessengersText(View.GONE)
                bt_whatsapp.visibility = View.VISIBLE
                bt_viber.visibility = View.VISIBLE
                delimiter.visibility = View.VISIBLE
            }
            is SupportState.ViberMessengers->{
                visibilityNoMessengersText(View.GONE)
                bt_viber.visibility = View.VISIBLE
                bt_whatsapp.visibility = View.GONE
                delimiter.visibility = View.GONE
            }
            is SupportState.WhatsappMessengers-> {
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