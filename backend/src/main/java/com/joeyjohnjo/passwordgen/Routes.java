package com.joeyjohnjo.passwordgen;

import com.joeyjohnjo.passwordgen.gerencia.Password;
import com.joeyjohnjo.passwordgen.gerencia.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.util.Map;

@RestController
@CrossOrigin
public class Routes {
    @Autowired PasswordManager passwordManager;
    //Setting up the login endpoint like this allows for sending the credentials through JSON
    @PostMapping("/login")
    String login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        if (validCredentials(username, password)) {
            SecurityContext sc = SecurityContextHolder.getContext();
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
            sc.setAuthentication(auth);
            return "AUTHORIZED";
        }
        else {
            return "NOT VALID";
        }
    }

    //Generate a password with type normal
    @GetMapping("/cliente/senha/normal")
    String normalPassword() {
        return new Password(Password.Type.NORMAL).get();
    }
    //Generate a password with type preferential
    @GetMapping("/cliente/senha/preferencial")
    String preferentialPassword() {
        return new Password(Password.Type.PREFERENTIAL).get();
    }
    //Returns a stream that is updated each time a password is called
    @GetMapping(path = "/cliente/acompanhamento", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux streamFlux() {
        return passwordManager.getProcessor();
    }

    //Call a password and remove from queue
    @GetMapping("/gerente/senha/proxima")
    String nextPassword() {
        return passwordManager.next();
    }
    //Call the last password again
    @GetMapping("/gerente/senha/rechamado")
    String recallPassword() {
        return passwordManager.recall();
    }
    //Reset the password count, starting at 0 again
    @GetMapping("/gerente/senha/redefinir")
    int reset() {
        passwordManager.reset();
        return 0;
    }

    /*Check if both the username and password match one of those defined
    in the inMemoryAuthentication*/
    boolean validCredentials(String username, String password) {
        return ( username.equals("gerente") && password.equals("gerente") );
    }
}
