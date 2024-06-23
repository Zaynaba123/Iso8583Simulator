package com.example.isoserveurservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//@EnableDiscoveryClient
@SpringBootApplication
public class IsoServeurServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsoServeurServiceApplication.class, args);
    }
    @Bean
    public void salut(){

     /*   try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Iso_BASE","root","");
            Statement stmt  = conn.createStatement();
            String sql = "SELECT card_number, amount, code_currency " +
                    "FROM isomsg WHERE card_number=123456 ";

            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString("card_number") + "\t" +
                        rs.getString("amount")  + "\t" +
                        rs.getString("code_currency"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }*/

    }
}
