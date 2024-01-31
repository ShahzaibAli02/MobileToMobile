package com.example.mobiletomobile.ui.main


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobiletomobile.BuildConfig.APPLICATION_ID
import com.example.mobiletomobile.R
import com.example.mobiletomobile.data.admob.AdmobBanner
import com.example.mobiletomobile.data.admob.AdmobInter
import com.example.mobiletomobile.databinding.ActivityDashboardBinding
import com.example.mobiletomobile.databinding.FeedbackDialogBinding
import com.example.mobiletomobile.ui.dialogs.AdmobLoadingDialog
import com.example.mobiletomobile.ui.dialogs.ColorPickerDialog
import com.example.mobiletomobile.ui.dialogs.ExitDialog
import com.example.mobiletomobile.ui.dialogs.FeedBackDialog
import com.example.mobiletomobile.utils.RemoteDataConfig
import com.example.mobiletomobile.utils.afterDelay
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beVisible
import com.example.mobiletomobile.utils.checkIfUserIsPro
import com.example.mobiletomobile.utils.singleClick
import com.google.android.material.navigation.NavigationView
import kotlin.system.exitProcess


class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListeners()
        if (RemoteDataConfig.remoteAdSettings.dash_banner.value == "on"
            && !checkIfUserIsPro()
        ) {
            AdmobBanner.getInstance()
                .loadAd(this@DashboardActivity, RemoteDataConfig.getAdmobBannerId(),
                    {
                        //load listener
                        binding.bannerFrame.beVisible()
                        AdmobBanner.getInstance()
                            .showBannerAd(this@DashboardActivity, binding.bannerFrame)
                    },
                    {
                        //failed listener
                    })
        } else {
            binding.bannerFrame.beGone()
        }

        binding.apply {

            toolbar.drawerIcon.setOnClickListener {
                drawerLayout.open()
            }

            val toggle = ActionBarDrawerToggle(
                this@DashboardActivity,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            if (checkIfUserIsPro()){
                navigationView.menu.removeItem(R.id.navPremium)
            }
            navigationView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navPointer -> {
                        drawerLayout.close()
                        singleClick {
                            ColorPickerDialog(this@DashboardActivity).show()
                        }

                    }

                    R.id.navPremium -> {
                        startActivity(Intent(this@DashboardActivity,PremiumActivity::class.java))
                    }

                    R.id.navFeedback -> {

                        drawerLayout.close()
                        FeedBackDialog(this@DashboardActivity).show()
                    }

                    R.id.navShareApp -> {

                        drawerLayout.close()
                        val appLink =
                            "Share and mirror content wirelessly with our user-friendly screen-sharing app.\nHere is our App link:\nhttps://play.google.com/store/apps/details?id=${APPLICATION_ID}"
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
                        shareIntent.setPackage(packageName)
                        startActivity(shareIntent)
                    }

                    R.id.navExit -> {

                        drawerLayout.close()
                        ExitDialog(this@DashboardActivity) {
                            finishAffinity()
                            finishAndRemoveTask()
                            exitProcess(0)
                        }.show()
                    }
                }

                true
            }
        }
    }

    override fun onBackPressed() {
        ExitDialog(this@DashboardActivity) {
            finishAffinity()
            finishAndRemoveTask()
            exitProcess(0)
        }.show()
    }

    override fun onClick(p0: View?) {
        binding.dashContent.apply {
            when (p0) {
                cardShareScreen -> {
                    singleClick {
                        if (RemoteDataConfig.remoteAdSettings.share_dash_inter.value == "on"
                            && !checkIfUserIsPro()
                        ) {
                            val loadingDialog = AdmobLoadingDialog(this@DashboardActivity)
                            loadingDialog.show()
                            afterDelay(2000){
                                loadingDialog.dismiss()
                                AdmobInter.showAdmobInter(this@DashboardActivity,
                                    {
                                        //impression listener
                                    },
                                    {
                                        // dismissed listener
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                ScreenSharingActivity::class.java
                                            )
                                        )
                                    },
                                    {
                                        //failed listener
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                ScreenSharingActivity::class.java
                                            )
                                        )
                                    })
                            }
                        } else {
                            startActivity(
                                Intent(
                                    this@DashboardActivity,
                                    ScreenSharingActivity::class.java
                                )
                            )
                        }

                    }
                }

                remoteScreen -> {
                    singleClick {
                        if (RemoteDataConfig.remoteAdSettings.share_dash_inter.value == "on"
                            && !checkIfUserIsPro()
                        ) {
                            val loadingDialog = AdmobLoadingDialog(this@DashboardActivity)
                            loadingDialog.show()
                            afterDelay(2000) {
                                loadingDialog.dismiss()
                                AdmobInter.showAdmobInter(this@DashboardActivity,
                                    {
                                        //impression listener
                                    },
                                    {
                                        // dismissed listener
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                RemoteCodeActivity::class.java
                                            )
                                        )
                                    },
                                    {
                                        //failed listener
                                        startActivity(
                                            Intent(
                                                this@DashboardActivity,
                                                RemoteCodeActivity::class.java
                                            )
                                        )
                                    })
                            }
                        } else {
                            startActivity(
                                Intent(
                                    this@DashboardActivity,
                                    RemoteCodeActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.dashContent.cardShareScreen.setOnClickListener(this@DashboardActivity)
        binding.dashContent.remoteScreen.setOnClickListener(this@DashboardActivity)
    }
}


