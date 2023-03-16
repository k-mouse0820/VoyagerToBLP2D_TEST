package jp.daisen_solution.voyagertoblp2d_test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.daisen_solution.voyagertoblp2d_test.model.Code
import jp.daisen_solution.voyagertoblp2d_test.databinding.ScanItemBinding.*
import jp.daisen_solution.voyagertoblp2d_test.databinding.ActivityMainBinding.*
import jp.daisen_solution.voyagertoblp2d_test.databinding.ScanItemBinding

class ScanAdapter(private val context: Context) : RecyclerView.Adapter<ScanAdapter.Holder>() {

    private var mCodes: MutableList<Code> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(ScanItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(mCodes[position])
    }

    fun setData(codes: List<Code>) {
        mCodes.clear()
        mCodes.addAll(codes)
        notifyDataSetChanged()
    }

    override fun getItemCount() =mCodes.size

    class Holder(val binding: ScanItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(code: Code) {
            binding.codeText.text = code.codeId
        }
    }

}