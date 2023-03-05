package com.five.library;

import com.five.Book;

import java.util.List;

public interface BookDAO {
    public void addBook(Book book);

    public void deleteBook(String isbn);

    public void updateBook(Book book);

    public Book getBook(String isbn);

    public List<Book> getAllBooks();
}