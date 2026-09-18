package com.example.bknova.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bknova.R
import com.example.bknova.model.PieEntryData
import com.example.bknova.model.QuestionReport
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ReportKuesionerAdapter(private var reports: List<QuestionReport>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PG = 0
        private const val TYPE_ESAI = 1
    }

    override fun getItemViewType(position: Int): Int {
        val tipe = reports[position].tipe
        return if (tipe == "Pilihan Ganda" || tipe == "Sosiometrik" || tipe == "Sosiometrik_Positif" || tipe == "Sosiometrik_Negatif") TYPE_PG else TYPE_ESAI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PG) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_grafik, parent, false)
            PgViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_esai, parent, false)
            EsaiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val report = reports[position]
        if (holder is PgViewHolder) {
            holder.bind(report)
        } else if (holder is EsaiViewHolder) {
            holder.bind(report)
        }
    }

    override fun getItemCount(): Int = reports.size

    fun updateData(newReports: List<QuestionReport>) {
        reports = newReports
        notifyDataSetChanged()
    }

    class PgViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSoal: TextView = view.findViewById(R.id.tv_soal_text)
        private val pieChart: PieChart = view.findViewById(R.id.pie_chart)
        private val tvTotal: TextView = view.findViewById(R.id.tv_total_responden_soal)

        fun bind(report: QuestionReport) {
            tvSoal.text = report.pertanyaan
            tvTotal.text = "Total Responden: ${report.totalResponden}"
            setupChart(report.pgStats ?: emptyList())
        }

        private fun setupChart(stats: List<PieEntryData>) {
            val entries = stats.filter { it.count > 0 }.map { PieEntry(it.count.toFloat(), it.label) }
            
            if (entries.isEmpty()) {
                pieChart.visibility = View.GONE
                return
            } else {
                pieChart.visibility = View.VISIBLE
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.VORDIPLOM_COLORS.toList() + ColorTemplate.JOYFUL_COLORS.toList()
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 12f

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = true
            pieChart.legend.isWordWrapEnabled = true
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setUsePercentValues(true)
            pieChart.animateY(1000)
            pieChart.invalidate()
        }
    }

    class EsaiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSoal: TextView = view.findViewById(R.id.tv_soal_text_esai)
        private val tvToggle: TextView = view.findViewById(R.id.tv_toggle_esai)
        private val ivChevron: ImageView = view.findViewById(R.id.iv_chevron_esai)
        private val btnToggle: View = view.findViewById(R.id.btn_toggle_esai)
        private val layoutJawaban: LinearLayout = view.findViewById(R.id.layout_jawaban_esai)

        fun bind(report: QuestionReport) {
            tvSoal.text = report.pertanyaan
            val count = report.esaiAnswers?.size ?: 0
            tvToggle.text = "Lihat Jawaban ($count)"
            
            layoutJawaban.removeAllViews()
            report.esaiAnswers?.forEach { jawaban ->
                val tvJawaban = TextView(itemView.context).apply {
                    text = "• $jawaban"
                    setPadding(0, 8, 0, 8)
                    setTextColor(itemView.context.getColor(R.color.text_color_primary))
                }
                layoutJawaban.addView(tvJawaban)
            }

            btnToggle.setOnClickListener {
                if (layoutJawaban.visibility == View.VISIBLE) {
                    layoutJawaban.visibility = View.GONE
                    ivChevron.rotation = 0f
                } else {
                    layoutJawaban.visibility = View.VISIBLE
                    ivChevron.rotation = 90f
                }
            }
        }
    }
}
