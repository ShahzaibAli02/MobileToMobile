package com.playsync.mirroring.ui.main


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.playsync.mirroring.BuildConfig
import com.playsync.mirroring.BuildConfig.APPLICATION_ID
import com.playsync.mirroring.R
import com.playsync.mirroring.data.admob.AdmobBanner
import com.playsync.mirroring.data.admob.AdmobInter
import com.playsync.mirroring.databinding.ActivityDashboardBinding
import com.playsync.mirroring.ui.dialogs.AdmobLoadingDialog
import com.playsync.mirroring.ui.dialogs.ColorPickerDialog
import com.playsync.mirroring.ui.dialogs.ExitDialog
import com.playsync.mirroring.ui.dialogs.FeedBackDialog
import com.playsync.mirroring.utils.RemoteDataConfig
import com.playsync.mirroring.utils.afterDelay
import com.playsync.mirroring.utils.gone
import com.playsync.mirroring.utils.visible
import com.playsync.mirroring.utils.checkIfUserIsPro
import com.playsync.mirroring.utils.singleClick
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
                        binding.bannerFrame.visible()
                        AdmobBanner.getInstance()
                            .showBannerAd(this@DashboardActivity, binding.bannerFrame)
                    },
                    {
                        //failed listener
                    })
        } else {
            binding.bannerFrame.gone()
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
                        val appLink = "Lets share screen together with awesome app: https://play.google.com/store/apps/details?id=${APPLICATION_ID}"
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
                        startActivity(shareIntent)
                    }
                    R.id.navPrivacyPolicy -> {

                        drawerLayout.close()
                         val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_POLICY))
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


