package one.digitalinnovation.cigarrostock.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.dto.QuantityDTO;
import one.digitalinnovation.cigarrostock.exception.CigarroAlreadyRegisteredException;
import one.digitalinnovation.cigarrostock.exception.CigarroNotFoundException;
import one.digitalinnovation.cigarrostock.exception.CigarroStockExceededException;
import one.digitalinnovation.cigarrostock.service.CigarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cigarros")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CigarroController implements CigarroControllerDocs {

    private final CigarroService cigarroService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CigarroDTO createCigarro(@RequestBody @Valid CigarroDTO cigarroDTO) throws CigarroAlreadyRegisteredException {
        return cigarroService.createCigarro(cigarroDTO);
    }

    @GetMapping("/{name}")
    public CigarroDTO findByName(@PathVariable String name) throws CigarroNotFoundException {
        return cigarroService.findByName(name);
    }

    @GetMapping
    public List<CigarroDTO> listCigarros() {
        return cigarroService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws CigarroNotFoundException {
        cigarroService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public CigarroDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws CigarroNotFoundException, CigarroStockExceededException {
        return cigarroService.increment(id, quantityDTO.getQuantity());
    }
}
