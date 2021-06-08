package one.digitalinnovation.cigarrostock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CigarroStockExceededException extends Exception {

    public CigarroStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Cigarros with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
