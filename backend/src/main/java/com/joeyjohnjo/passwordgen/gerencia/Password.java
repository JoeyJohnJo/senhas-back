package com.joeyjohnjo.passwordgen.gerencia;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.ResourceBundle.getBundle;

@Component
@Scope("prototype")
public class Password implements Comparable<Password> {

    public enum Type { NORMAL, PREFERENTIAL }
    private Type type;
    private Integer passwordNumber; //The number on the current password
    //Every password must be able to access this so that the numbers follow ascending order
    public static Integer lastNumber = Integer.valueOf(
            getBundle("static/password", Locale.ROOT).getString("last_pass"));

    public Password(Type type) {
        this.type = type;
        passwordNumber = lastNumber + 1; //The current password is one greater than the last
        lastNumber = passwordNumber; //This password now becomes the most recent generated
        PasswordManager.queue.add(this);
    }
    Password(Type type, int number) {
        this.type = type;
        passwordNumber = number;
    }

    //Return the password in the format N####
    public String get() {
        StringBuilder finalString = new StringBuilder(); //The string to be returned after processing
        finalString.append( type == Type.PREFERENTIAL ? "P" : "N" );
        int digitLength = valueOf(passwordNumber).length();
        int paddingSize = 4-digitLength; //Amount of zeros to be added to the left

        if (paddingSize > 0) {
            for (int i = 0; i < paddingSize; i++) {
                finalString.append("0");
            }
        }
        finalString.append(passwordNumber);
        return finalString.toString();
    }

    //This comparator puts all preferential passwords in the front of queue
    @Override
    public int compareTo(Password other) {
        if (this.type == other.type)
            return this.passwordNumber.compareTo(other.passwordNumber);

        if (this.type == Type.PREFERENTIAL) return -1;
        else return 1;
    }
}
