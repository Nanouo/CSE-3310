package com.reread.app.data

object CartManager {

    private val items = mutableListOf<CartItem>()

    fun addItem(book: Book) {
        if (items.none { it.bookId == book.id }) {
            items.add(
                CartItem(
                    id = items.size + 1,
                    bookId = book.id,
                    title = book.title,
                    author = book.author,
                    price = book.price,
                    condition = book.condition,
                    sellerUsername = book.sellerUsername
                )
            )
        }
    }

    fun removeItem(bookId: Int) {
        items.removeAll { it.bookId == bookId }
    }

    fun getItems(): List<CartItem> = items.toList()

    fun isInCart(bookId: Int): Boolean = items.any { it.bookId == bookId }

    fun getTotal(): Double = items.sumOf { it.price }

    fun clear() = items.clear()

    fun count(): Int = items.size
}