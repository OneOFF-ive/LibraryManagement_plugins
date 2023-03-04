package com.five.library;

import com.five.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        XMLMapperParser xmlMapperParser = new XMLMapperParser("book-mapper.xml");
        xmlMapperParser.paresXml();


        var book = new Book("lise", "i231441", "wosa", 12);
        var sql = xmlMapperParser.getSql("com.example.book.dao.BookDAO.selectAll", null);

        System.out.println(sql);
    }
}