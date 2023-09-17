package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val context: Context,
    private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var carItemLister: (Repository) -> Unit = {
        Log.d("clickItem", it.htmlUrl)
        openBrowser(context, it.htmlUrl)
    }
    var btnShareLister: (Repository) -> Unit = {
        Log.d("click", it.htmlUrl)
        shareRepositoryLink(context, it.htmlUrl)
    }

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nomeRepo.text = repositories[position].name

        holder.cvRepo.setOnClickListener{
            carItemLister(repositories[position])
        }

        holder.ivShare.setOnClickListener {
            btnShareLister(repositories[position])
        }
    }

    fun shareRepositoryLink(context: Context, urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun openBrowser(context: Context, urlRepository: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeRepo: TextView
        val ivShare: ImageView
        val cvRepo: CardView

        init {
            view.apply {
                nomeRepo = findViewById(R.id.tv_name_repo)
                ivShare = findViewById(R.id.iv_share)
                cvRepo = findViewById(R.id.cv_repo)
            }
        }

    }
}



