package one.digitalinnovation.cigarrostock.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.entity.Cigarro;
import one.digitalinnovation.cigarrostock.exception.CigarroAlreadyRegisteredException;
import one.digitalinnovation.cigarrostock.exception.CigarroNotFoundException;
import one.digitalinnovation.cigarrostock.exception.CigarroStockExceededException;
import one.digitalinnovation.cigarrostock.mapper.CigarroMapper;
import one.digitalinnovation.cigarrostock.repository.CigarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CigarroService {

    private final CigarroRepository cigarroRepository;
    private final CigarroMapper cigarroMapper = CigarroMapper.INSTANCE;

    public CigarroDTO createCigarro(CigarroDTO cigarroDTO) throws CigarroAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(cigarroDTO.getName());
        Cigarro cigarro = cigarroMapper.toModel(cigarroDTO);
        Cigarro savedCigarro = cigarroRepository.save(cigarro);
        return cigarroMapper.toDTO(savedCigarro);
    }

    public CigarroDTO findByName(String name) throws CigarroNotFoundException {
        Cigarro foundCigarro = cigarroRepository.findByName(name)
                .orElseThrow(() -> new CigarroNotFoundException(name));
        return cigarroMapper.toDTO(foundCigarro);
    }

    public List<CigarroDTO> listAll() {
        return cigarroRepository.findAll()
                .stream()
                .map(cigarroMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws CigarroNotFoundException {
        verifyIfExists(id);
        cigarroRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws CigarroAlreadyRegisteredException {
        Optional<Cigarro> optSavedCigarro = cigarroRepository.findByName(name);
        if (optSavedCigarro.isPresent()) {
            throw new CigarroAlreadyRegisteredException(name);
        }
    }

    private Cigarro verifyIfExists(Long id) throws CigarroNotFoundException {
        return cigarroRepository.findById(id)
                .orElseThrow(() -> new CigarroNotFoundException(id));
    }

    public CigarroDTO increment(Long id, int quantityToIncrement) throws CigarroNotFoundException, CigarroStockExceededException {
        Cigarro cigarroToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + cigarroToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= cigarroToIncrementStock.getMax()) {
            cigarroToIncrementStock.setQuantity(cigarroToIncrementStock.getQuantity() + quantityToIncrement);
            Cigarro incrementedBeerStock = cigarroRepository.save(cigarroToIncrementStock);
            return cigarroMapper.toDTO(incrementedBeerStock);
        }
        throw new CigarroStockExceededException(id, quantityToIncrement);
    }
}
