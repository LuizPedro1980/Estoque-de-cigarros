package one.digitalinnovation.cigarrostock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CigarroAlreadyRegisteredException extends Exception{

    public CigarroAlreadyRegisteredException(String cigarroName) {
        super(String.format("Cigarro with name %s already registered in the system.", cigarroName));
    }
}
