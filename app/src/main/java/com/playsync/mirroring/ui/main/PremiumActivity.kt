package com.playsync.mirroring.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.playsync.mirroring.R
import com.playsync.mirroring.data.billing.GoogleBilling
import com.playsync.mirroring.data.enums.PlanType
import com.playsync.mirroring.databinding.ActivityPremiumBinding
import com.playsync.mirroring.utils.afterDelay
import com.playsync.mirroring.utils.gone
import com.playsync.mirroring.utils.visible
import com.playsync.mirroring.utils.singleClick

@Suppress("DEPRECATION")
class PremiumActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPremiumBinding

    private lateinit var planType: PlanType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        planType = PlanType.UNLIMITED
        with(binding) {
            setUpClickListener()
            setPriceForSubscription()

            afterDelay(3000) {
                cross.visible()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun ActivityPremiumBinding.setPriceForSubscription() {
        yearlyPrice.text = GoogleBilling.subYearlyPrice
        monthlyPrice.text = GoogleBilling.subMonthlyPrice
        unlimitedPrice.text = GoogleBilling.inAppAllPrice
    }

    private fun ActivityPremiumBinding.setUpClickListener() {

        cross.setOnClickListener(this@PremiumActivity)
        continueBtn.setOnClickListener(this@PremiumActivity)
        unlimitedPlan.setOnClickListener(this@PremiumActivity)
        yearlyPlan.setOnClickListener(this@PremiumActivity)
        monthlyPlan.setOnClickListener(this@PremiumActivity)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(v: View?) {
        binding.apply {
            when (v) {

                cross -> {
                    singleClick {

                        onBackPressed()
                    }

                }

                continueBtn -> {
                    singleClick {

                        when (planType) {
                            PlanType.UNLIMITED -> {
                                GoogleBilling(this@PremiumActivity).launchInAppBillingFlow(this@PremiumActivity)
                            }

                            PlanType.YEARLY -> {
                                GoogleBilling(this@PremiumActivity).launchSubsBillingFlow(
                                    this@PremiumActivity,
                                    0
                                )
                            }

                            PlanType.MONTHLY -> {
                                GoogleBilling(this@PremiumActivity).launchSubsBillingFlow(
                                    this@PremiumActivity,
                                    1
                                )
                            }
                        }

                    }

                }

                unlimitedPlan -> {
                    saveNinty.visible()
                    saveMonthly.gone()
                    saveYearly.gone()


                    unlimitedPlan.setBackgroundResource(R.drawable.subs_selected_stroke_bg)
                    unlimitedConstraint.setBackgroundResource(R.drawable.subs_selected_solid_bg)

                    monthlyPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    monthlyConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)

                    yearlyPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    yearlyConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)


                    unlimitedPrice.setTextColor(resources.getColor(R.color.white))
                    monthlyPrice.setTextColor(resources.getColor(R.color.app_color))
                    yearlyPrice.setTextColor(resources.getColor(R.color.app_color))

                    planType = PlanType.UNLIMITED
                }

                monthlyPlan -> {
                    saveNinty.gone()
                    saveMonthly.visible()
                    saveYearly.gone()


                    unlimitedPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    unlimitedConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)

                    monthlyPlan.setBackgroundResource(R.drawable.subs_selected_stroke_bg)
                    monthlyConstraint.setBackgroundResource(R.drawable.subs_selected_solid_bg)

                    yearlyPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    yearlyConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)


                    unlimitedPrice.setTextColor(resources.getColor(R.color.app_color))
                    monthlyPrice.setTextColor(resources.getColor(R.color.white))
                    yearlyPrice.setTextColor(resources.getColor(R.color.app_color))

                    planType = PlanType.YEARLY
                }

                yearlyPlan -> {
                    saveNinty.gone()
                    saveMonthly.gone()
                    saveYearly.visible()


                    unlimitedPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    unlimitedConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)

                    monthlyPlan.setBackgroundResource(R.drawable.subs_unselected_stroke_bg)
                    monthlyConstraint.setBackgroundResource(R.drawable.subs_unselected_solid_bg)

                    yearlyPlan.setBackgroundResource(R.drawable.subs_selected_stroke_bg)
                    yearlyConstraint.setBackgroundResource(R.drawable.subs_selected_solid_bg)


                    unlimitedPrice.setTextColor(resources.getColor(R.color.app_color))
                    monthlyPrice.setTextColor(resources.getColor(R.color.app_color))
                    yearlyPrice.setTextColor(resources.getColor(R.color.white))

                    planType = PlanType.MONTHLY
                }


            }
        }

    }


    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (binding.cross.isVisible) {
            if (intent.hasExtra("isFromSplash")) {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            finish()
        }
    }
}