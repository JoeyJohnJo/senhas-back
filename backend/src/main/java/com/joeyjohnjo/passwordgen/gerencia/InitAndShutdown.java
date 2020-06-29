package com.joeyjohnjo.passwordgen.gerencia;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;
import static java.util.ResourceBundle.getBundle;

@Component
public class InitAndShutdown {

    @PostConstruct
    private void populateQueue() {
        System.out.println("Loading password queue...");
        ResourceBundle bundle = getBundle("static/password", Locale.ROOT);
        for (String k : bundle.keySet()) {
            try {
                if (parseInt(k) >= 0) {
                    String formattedPass = bundle.getString(k);
                    Password.Type type = formattedPass.charAt(0) == 'N' ?
                            Password.Type.NORMAL : Password.Type.PREFERENTIAL;

                    Integer number = parseInt(formattedPass.substring(1));
                    System.out.println("Number of password is: " + number);
                    PasswordManager.queue.add(new Password(type, number));
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    @PreDestroy
    public static void storePasswords() {
        System.out.println("Storing password queue before closing...");
        try {
            int counter = 0;
            StringBuilder content = new StringBuilder();
            content.append("last_pass=").append(Password.lastNumber).append("\n");
            System.out.println("Last password created: " + Password.lastNumber);

            for (Password pass : PasswordManager.queue) {
                content.append(counter).append("=").append(pass.get()).append("\n");
                System.out.println("Saving " + pass.get());
                counter++;
            }
            File file = new File("./src/main/resources/static/password.properties");
            file.createNewFile();
            System.out.println(file.getPath());
            PrintWriter writer = new PrintWriter("./src/main/resources/static/password.properties");
            writer.println(content.toString());
            writer.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

}