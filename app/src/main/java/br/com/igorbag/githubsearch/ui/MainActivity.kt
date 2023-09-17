package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var etNomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView

    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
        setupListeners()
        showUserName()
        setupRetrofit()
    }

    override fun onStart() {
        super.onStart()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        etNomeUsuario = findViewById(R.id.et_nome_usuario);
        btnConfirmar = findViewById(R.id.btn_confirmar);
        listaRepositories = findViewById(R.id.rv_lista_repositories);
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val nomeUsuario = etNomeUsuario.text.toString()
            getAllReposByUserName(nomeUsuario)
            saveUserLocal(nomeUsuario)
            //openBrowser()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(nome: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)?: return
        with(sharedPref.edit()){
            putString( getString(R.string.saved_user), nome )
            apply()
        }
    }

    private fun showUserName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val nomeUsuarioSalvo = sharedPref.getString( getString(R.string.saved_user), "" )
        etNomeUsuario.setText(nomeUsuarioSalvo)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        //Documentacao oficial do retrofit - https://square.github.io/retrofit/
        val baseUrl = "https://api.github.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName(usuario: String) {
        githubApi.getAllRepositoriesByUser(usuario).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let{
                        setupAdapter(it)
                    }
                }else{
                    //Toast.makeText( getString(R.string.erro_response), Toast.LENGTH_SHORT ).show()
                    Log.e("Erro", getString(R.string.erro_response))
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Log.e("Error", "Falha na requisicao")
            }

        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val repoAdapter = RepositoryAdapter( baseContext, list)
        listaRepositories.apply {
            adapter = repoAdapter
        }
    }

    fun openBrowser(urlRepository: String = "https://www.google.com") {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }

}