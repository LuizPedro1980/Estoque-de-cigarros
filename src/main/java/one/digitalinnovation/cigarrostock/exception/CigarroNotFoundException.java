package one.digitalinnovation.cigarrostock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CigarroNotFoundException extends Exception {

    public CigarroNotFoundException(String cigarroName) {
        super(String.format("Cigarro with name %s not found in the system.", cigarroName));
    }

    public CigarroNotFoundException(Long id) {
        super(String.format("Cigarro with id %s not found in the system.", id));
    }
}
