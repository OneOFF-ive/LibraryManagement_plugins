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
        var sql = xmlMapperParser.getSql("com.example.book.dao.BookDAO.selectByIsbn", "i");

        var conn = DbUtil.getConnection();
        var state = conn.createStatement();
        System.out.println(sql);
        state.execute(sql);
        var res = state.getResultSet();
        while (res.next()) {
            System.out.print(res.getString("title") + "\t");
            System.out.print(res.getString("isbn") + "\t");
            System.out.print(res.getString("author") + "\t");
            System.out.print(res.getInt("totalAmount") + "\t");
            System.out.print(res.getInt("currentAmount") + "\n");
        }
    }
}