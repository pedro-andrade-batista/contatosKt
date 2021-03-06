package br.edu.ifsp.scl.ads.pdm.contatoskt.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.ads.pdm.contatoskt.AutenticacaoFirebase
import br.edu.ifsp.scl.ads.pdm.contatoskt.R
import br.edu.ifsp.scl.ads.pdm.contatoskt.adapter.ContatosAdapter
import br.edu.ifsp.scl.ads.pdm.contatoskt.adapter.OnContatoClickListener
import br.edu.ifsp.scl.ads.pdm.contatoskt.controller.ContatoController
import br.edu.ifsp.scl.ads.pdm.contatoskt.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.contatoskt.model.Contato

class MainActivity : AppCompatActivity(), OnContatoClickListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var contatosList: MutableList<Contato>
    private lateinit var contatosAdapter: ContatosAdapter
    private lateinit var contatosLayoutManager: LinearLayoutManager

    private  lateinit var novoContatoLauncher: ActivityResultLauncher<Intent>

    private lateinit var contatoController: ContatoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        contatoController = ContatoController(this)

        contatosList = contatoController.buscaContatos()

        contatosList = mutableListOf()
//        for (i in 1..5) {
//            contatosList.add(
//                Contato(
//                    "Nome $i",
//                    "Email $i",
//                    "Telefone $i",
//                    if (i % 2 == 0) false else true,
//                    "Celular $i",
//                    "Site $i"
//                )
//            )
//        }

        contatosAdapter = ContatosAdapter(contatosList, this)
        activityMainBinding.contatosRv.adapter = contatosAdapter
        contatosLayoutManager = LinearLayoutManager(this)
        activityMainBinding.contatosRv.layoutManager = contatosLayoutManager

        novoContatoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                val contato: Contato? = activityResult.data?.getParcelableExtra<Contato>(Intent.EXTRA_USER)
                if (contato != null) {
                    contatosList.add(contato)
                    contatosAdapter.notifyDataSetChanged()

                    contatoController.insereContato(contato)

                }
            }
        }

    }

    override fun onContatoClick(posicao: Int) {
        val contato: Contato = contatosList[posicao]
        Toast.makeText(this, contato.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.novoContatoMi -> {
            val novoContatoIntent = Intent(this, ContatoActivity::class.java)
            novoContatoLauncher.launch(novoContatoIntent)
            true
        }
        R.id.sairMi -> {
            AutenticacaoFirebase.firebaseAuth.signOut()
            true
        }
        else -> {
            false
        }
    }

    override fun onStart() {
        super.onStart()
        if(AutenticacaoFirebase.firebaseAuth.currentUser == null){
            finish()
        }
    }
}