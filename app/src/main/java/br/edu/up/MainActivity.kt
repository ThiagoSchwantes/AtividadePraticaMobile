package br.edu.up

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutMain()
        }
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
            DetalhesProduto(navController, produto)
        }
        composable("estatistica") { Estatistica(navController = navController)}
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
        modifier = Modifier.fillMaxSize().padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Nome do Produto:", fontSize = 15.sp, modifier = Modifier.fillMaxWidth().align(Alignment.Start));
        TextField(value = nome, onValueChange = { nome = it}, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth());
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Categoria:", fontSize = 15.sp, modifier = Modifier.fillMaxWidth().align(Alignment.Start));
        TextField(value = categoria, onValueChange = { categoria = it}, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth());
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Preço:", fontSize = 15.sp, modifier = Modifier.fillMaxWidth().align(Alignment.Start));
        TextField(value = preco, onValueChange = { preco = it}, label = { Text("Preço")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth());
        Spacer(modifier = Modifier.height(10.dp));

        Text(text = "Quantidade em Estoque:", fontSize = 15.sp, modifier = Modifier.fillMaxWidth().align(Alignment.Start));
        TextField(value = quantEstoque, onValueChange = { quantEstoque = it}, label = { Text("Quantidade em Estoque")}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth());
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

                    Estoque.adicionarProduto(produto);

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

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
        ){
            Button(onClick = {navController.popBackStack()}) {
                Text(text = "Voltar")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {navController.navigate("estatistica")}) {
                Text(text = "Estatísticas")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(Estoque.produtos) { produto ->
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


}

@Composable
fun ProdutoItem(produto: Produto, onClickDetalhes: () -> Unit) {

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Color.Black, shape = RoundedCornerShape(50)),
        shape = RoundedCornerShape(50)
    ){
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Nome: ${produto.nome} (${produto.quantEstoque})", fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onClickDetalhes) {
                Text(text = "Detalhes")
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp));
}

@Composable

fun Estatistica(navController: NavController){
    var quantProdutos = 0;

    for (produto in Estoque.produtos){
        quantProdutos += produto.quantEstoque;
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Button(onClick = {
            navController.popBackStack();
        }) {
            Text(text = "Voltar")
        }
        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.Black, shape = RoundedCornerShape(25)),
            shape = RoundedCornerShape(25),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Valor Total do Estoque: ${Estoque.calcularValorTotalEstoque()}", fontSize = 20.sp)
                Text(text = "Quantidade total de produtos: ${quantProdutos}", fontSize = 20.sp)
            }

        }
    }
}

@Composable
fun DetalhesProduto(navController: NavController, produto: Produto){
    Column (
        modifier = Modifier.fillMaxSize().padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Button(onClick = {
            navController.popBackStack();
        }) {
            Text(text = "Voltar")
        }
        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.Black, shape = RoundedCornerShape(10)),
            shape = RoundedCornerShape(10)
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row {
                    Text(text = "Nome: ${produto.nome}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Categoria: ${produto.categoria}", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Text(text = "Preço: ${produto.preco}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Quantidade em estoque: ${produto.quantEstoque}", fontSize = 20.sp)
                }


            }

        }



    }
}