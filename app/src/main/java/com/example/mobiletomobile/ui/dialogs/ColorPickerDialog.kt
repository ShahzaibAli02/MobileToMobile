package com.example.mobiletomobile.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.mobiletomobile.R
import com.example.mobiletomobile.databinding.PointerClrDialogBinding
import com.example.mobiletomobile.utils.SharedPrefs
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beInVisible
import com.example.mobiletomobile.utils.beVisible

class ColorPickerDialog(val activity: Activity) : Dialog(activity) {


    var currentSelectedColor: Int? = null
    var selectedPointerSize: Int? = null


    lateinit var binding: PointerClrDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointerClrDialogBinding.inflate(layoutInflater)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)

        /*window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )*/

        with(binding){




            when (SharedPrefs(activity).getDrawBallViewColor()) {
                activity.getColor(R.color.pointer_clr_red) -> {

                    clrRedSelected.beVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_orange) -> {


                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beVisible()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_yellow) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beVisible()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_green) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beVisible()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_teal) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beVisible()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_blue) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beVisible()
                    clrIndigoSelected.beGone()
                }

                activity.getColor(R.color.pointer_clr_indigo) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beVisible()
                }

                else -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.beGone()
                    clrYellowSelected.beGone()
                    clrGreenSelected.beGone()
                    clrTealSelected.beGone()
                    clrNeonSelected.beGone()
                    clrIndigoSelected.beGone()


                }

            }


            seekBarPointerSize.progress =
                SharedPrefs(activity).getDrawBallSizeProgress().toInt()
            seekBarPointerSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    selectedPointerSize = progress

                    var width = progress * 25
                    var height = progress * 25
                    var radius = 35f

                    if (progress == 2) {
                        width = progress * 20
                        height = progress * 20
                    }


                    if (progress == 3) {
                        width = progress * 22
                        height = progress * 22
                    }
                    if (progress == 4) {
                        width = progress * 24
                        height = progress * 24
                    }


                    radius = (progress * 15).toFloat()
                    SharedPrefs(activity).setDrawBallRadius(radius.toString())
                    SharedPrefs(activity).setDrawBallWidth(width.toString())
                    SharedPrefs(activity).setDrawBallHeight(height.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.d("seekbar", "onStartTrackingTouch: $seekBar")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.d("seekbar", "onStopTrackingTouch: $seekBar")
                }

            })



            val hexColor =
                String.format("#%06X", 0xFFFFFF and SharedPrefs(activity).getDrawBallViewColor())

            colorPickerViewDialog.color = Color.parseColor(hexColor)
            colorPickerViewDialog.setOnColorChangedListener {

                currentSelectedColor = it

            }



            cardRed.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_red)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_red)

                clrRedSelected.beVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beGone()
                clrGreenSelected.beGone()
                clrTealSelected.beGone()
                clrNeonSelected.beGone()
                clrIndigoSelected.beGone()

            }



            cardOrange.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_orange)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_orange)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beVisible()
                clrYellowSelected.beGone()
                clrGreenSelected.beGone()
                clrTealSelected.beGone()
                clrNeonSelected.beGone()
                clrIndigoSelected.beGone()

            }


            cardYellow.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_yellow)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_yellow)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beVisible()
                clrGreenSelected.beGone()
                clrTealSelected.beGone()
                clrNeonSelected.beGone()
                clrIndigoSelected.beGone()

            }

            cardGreen.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_green)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_green)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beGone()
                clrGreenSelected.beVisible()
                clrTealSelected.beGone()
                clrNeonSelected.beGone()
                clrIndigoSelected.beGone()

            }

            cardTeal.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_teal)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_teal)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beGone()
                clrGreenSelected.beGone()
                clrTealSelected.beVisible()
                clrNeonSelected.beGone()
                clrIndigoSelected.beGone()

            }


            cardNeon.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_blue)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_blue)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beGone()
                clrGreenSelected.beGone()
                clrTealSelected.beGone()
                clrNeonSelected.beVisible()
                clrIndigoSelected.beGone()
            }


            cardIndigo.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_indigo)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_indigo)

                clrRedSelected.beInVisible()
                clrOrangeSelected.beGone()
                clrYellowSelected.beGone()
                clrGreenSelected.beGone()
                clrTealSelected.beGone()
                clrNeonSelected.beGone()
                clrIndigoSelected.beVisible()
            }



            btnDoneCard.setOnClickListener {
                SharedPrefs(activity).setDrawBallSizeProgress(seekBarPointerSize.progress.toString())
                currentSelectedColor?.let { it1 -> SharedPrefs(activity).setDrawBallViewColor(it1) }
                selectedPointerSize?.let { it1 -> SharedPrefs(activity).setDrawBallSizeProgress(it1.toString()) }
                dismiss()
            }

        }
    }
}