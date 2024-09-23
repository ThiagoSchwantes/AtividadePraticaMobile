package br.edu.up

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.up.MainActivity.Companion.produtos
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutMain()
        }
    }

    companion object {
        var produtos = listOf<Produto>()
    }
}

@Composable
fun LayoutMain(){
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = "cadastro"){
        composable("cadastro") { CadastroProduto(navController) }
        composable("lista") { ListaProdutos(navController) }
        composable("detalhes/{produtoJson}"){
        backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            DetalhesProduto(navController, produto) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProduto(navController: NavController){

    var nome by remember {  mutableStateOf("")  }
    var categoria by remember {  mutableStateOf("")  }
    var preco by remember {  mutableStateOf("")  }
    var quantEstoque by remember {  mutableStateOf("")  }
    val context = LocalContext.current;

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Nome do Produto:", fontSize = 15.sp);
        TextField(value = nome, onValueChange = { nome = it}, label = { Text("Nome") });
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Categoria:", fontSize = 15.sp);
        TextField(value = categoria, onValueChange = { categoria = it}, label = { Text("Categoria") });
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Preço:", fontSize = 15.sp);
        TextField(value = preco, onValueChange = { preco = it}, label = { Text("Preço")});
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Quantidade em Estoque:", fontSize = 15.sp);
        TextField(value = quantEstoque, onValueChange = { quantEstoque = it}, label = { Text("Quantidade em Estoque")});
        Spacer(modifier = Modifier.height(25.dp));
        
        Button(onClick = {
            var precoDouble = preco.toDoubleOrNull();
            var quantInt = quantEstoque.toIntOrNull();
            when{
                nome.isBlank() || categoria.isBlank() || preco.isBlank() || quantEstoque.isBlank() ->{
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                };
                precoDouble == null || quantInt == null ->{
                    Toast.makeText(context, "Preencha todos os campos Corretamente", Toast.LENGTH_SHORT).show()
                }
                quantInt < 1 ->{
                    Toast.makeText(context, "Quantidade em Estoque não pode ser menor que 1", Toast.LENGTH_SHORT).show()
                }
                precoDouble < 0 ->{
                    Toast.makeText(context, "Preço não pode ser menor que 0", Toast.LENGTH_SHORT).show()
                }
                else ->{
                    var produto = Produto(nome, categoria, precoDouble, quantInt)
                    produtos = produtos + produto;
                    navController.navigate("lista");
                }
            }
        }) {
            Text(text = "Cadastrar Produto")
        }
    }
}

@Composable
fun ListaProdutos(navController: NavController){
    val context = LocalContext.current;

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(produtos) { produto ->
            ProdutoItem(
                produto = produto,
                onClickDetalhes = {
//                    produtos = produtos.filter { it != produto }
//                    Toast.makeText(context, "produto excluido", Toast.LENGTH_SHORT).show()
                    val produtoJson = Gson().toJson(produto);
                    navController.navigate("detalhes/${produtoJson}")
                }
            )
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, onClickDetalhes: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Text(text = "Nome: ${produto.nome} (${produto.quantEstoque})", fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onClickDetalhes) {
            Text(text = "Detalhes")
        }
    }
}

@Composable
fun DetalhesProduto(navController: NavController, produto: Produto){
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(text = "Nome: ${produto.nome}", fontSize = 15.sp)
        Text(text = "Categoria: ${produto.categoria}", fontSize = 15.sp)
        Text(text = "Preço: ${produto.preco}", fontSize = 15.sp)
        Text(text = "Quantidade: ${produto.quantEstoque}", fontSize = 15.sp)
        Button(onClick = {
            navController.popBackStack();
        }) {
            Text(text = "Voltar")
        }
    }
}