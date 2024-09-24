package br.edu.up

class Estoque (){
    companion object {
        var produtos = listOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            produtos += produto;
        }

        fun calcularValorTotalEstoque(): Double {
            var valorTotal = 0.0;
            for (produto in produtos) {
                valorTotal += (produto.quantEstoque * produto.preco);
            }

            return valorTotal
        }
    }
}