package com.playsync.mirroring.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.playsync.mirroring.R
import com.playsync.mirroring.databinding.PointerClrDialogBinding
import com.playsync.mirroring.utils.SharedPrefs
import com.playsync.mirroring.utils.gone
import com.playsync.mirroring.utils.beInVisible
import com.playsync.mirroring.utils.visible

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

                    clrRedSelected.visible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_orange) -> {


                    clrRedSelected.beInVisible()
                    clrOrangeSelected.visible()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_yellow) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.visible()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_green) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.visible()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_teal) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.visible()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_blue) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.visible()
                    clrIndigoSelected.gone()
                }

                activity.getColor(R.color.pointer_clr_indigo) -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.visible()
                }

                else -> {

                    clrRedSelected.beInVisible()
                    clrOrangeSelected.gone()
                    clrYellowSelected.gone()
                    clrGreenSelected.gone()
                    clrTealSelected.gone()
                    clrNeonSelected.gone()
                    clrIndigoSelected.gone()


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

                clrRedSelected.visible()
                clrOrangeSelected.gone()
                clrYellowSelected.gone()
                clrGreenSelected.gone()
                clrTealSelected.gone()
                clrNeonSelected.gone()
                clrIndigoSelected.gone()

            }



            cardOrange.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_orange)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_orange)

                clrRedSelected.beInVisible()
                clrOrangeSelected.visible()
                clrYellowSelected.gone()
                clrGreenSelected.gone()
                clrTealSelected.gone()
                clrNeonSelected.gone()
                clrIndigoSelected.gone()

            }


            cardYellow.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_yellow)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_yellow)

                clrRedSelected.beInVisible()
                clrOrangeSelected.gone()
                clrYellowSelected.visible()
                clrGreenSelected.gone()
                clrTealSelected.gone()
                clrNeonSelected.gone()
                clrIndigoSelected.gone()

            }

            cardGreen.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_green)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_green)

                clrRedSelected.beInVisible()
                clrOrangeSelected.gone()
                clrYellowSelected.gone()
                clrGreenSelected.visible()
                clrTealSelected.gone()
                clrNeonSelected.gone()
                clrIndigoSelected.gone()

            }

            cardTeal.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_teal)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_teal)

                clrRedSelected.beInVisible()
                clrOrangeSelected.gone()
                clrYellowSelected.gone()
                clrGreenSelected.gone()
                clrTealSelected.visible()
                clrNeonSelected.gone()
                clrIndigoSelected.gone()

            }


            cardNeon.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_blue)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_blue)

                clrRedSelected.beInVisible()
                clrOrangeSelected.gone()
                clrYellowSelected.gone()
                clrGreenSelected.gone()
                clrTealSelected.gone()
                clrNeonSelected.visible()
                clrIndigoSelected.gone()
            }


            cardIndigo.setOnClickListener {
                currentSelectedColor = activity.getColor(R.color.pointer_clr_indigo)
                colorPickerViewDialog.color = activity.getColor(R.color.pointer_clr_indigo)

                clrRedSelected.beInVisible()
                clrOrangeSelected.gone()
                clrYellowSelected.gone()
                clrGreenSelected.gone()
                clrTealSelected.gone()
                clrNeonSelected.gone()
                clrIndigoSelected.visible()
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