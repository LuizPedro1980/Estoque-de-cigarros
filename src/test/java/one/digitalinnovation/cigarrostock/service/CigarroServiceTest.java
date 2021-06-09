package one.digitalinnovation.cigarrostock.service;

import one.digitalinnovation.cigarrostock.builder.CigarroDTOBuilder;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.entity.Cigarro;
import one.digitalinnovation.cigarrostock.exception.CigarroAlreadyRegisteredException;
import one.digitalinnovation.cigarrostock.exception.CigarroNotFoundException;
import one.digitalinnovation.cigarrostock.exception.CigarroStockExceededException;
import one.digitalinnovation.cigarrostock.mapper.CigarroMapper;
import one.digitalinnovation.cigarrostock.repository.CigarroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CigarroServiceTest {

    private static final long INVALID_CIGARRO_ID = 1L;

    @Mock
    private CigarroRepository cigarroRepository;

    private CigarroMapper cigarroMapper = CigarroMapper.INSTANCE;

    @InjectMocks
    private CigarroService cigarroService;

    @Test
    void whenCigarroInformedThenItShouldBeCreated() throws CigarroAlreadyRegisteredException {
        // given
        CigarroDTO expectedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedSavedCigarro = cigarroMapper.toModel(expectedCigarroDTO);

        // when
        when(cigarroRepository.findByName(expectedCigarroDTO.getName())).thenReturn(Optional.empty());
        when(cigarroRepository.save(expectedSavedCigarro)).thenReturn(expectedSavedCigarro);

        //then
        CigarroDTO createdCigarroDTO = cigarroService.createCigarro(expectedCigarroDTO);

        assertThat(createdCigarroDTO.getId(), is(equalTo(expectedCigarroDTO.getId())));
        assertThat(createdCigarroDTO.getName(), is(equalTo(expectedCigarroDTO.getName())));
        assertThat(createdCigarroDTO.getQuantity(), is(equalTo(expectedCigarroDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredCigarroInformedThenAnExceptionShouldBeThrown() {
        // given
        CigarroDTO expectedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro duplicatedCigarro = cigarroMapper.toModel(expectedCigarroDTO);

        // when
        when(cigarroRepository.findByName(expectedCigarroDTO.getName())).thenReturn(Optional.of(duplicatedCigarro));

        // then
        assertThrows(CigarroAlreadyRegisteredException.class, () -> cigarroService.createCigarro(expectedCigarroDTO));
    }

    @Test
    void whenValidCigarroNameIsGivenThenReturnACigarro() throws CigarroNotFoundException {
        // given
        CigarroDTO expectedFoundCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedFoundCigarro = cigarroMapper.toModel(expectedFoundCigarroDTO);

        // when
        when(cigarroRepository.findByName(expectedFoundCigarro.getName())).thenReturn(Optional.of(expectedFoundCigarro));

        // then
        CigarroDTO foundCigarroDTO = cigarroService.findByName(expectedFoundCigarroDTO.getName());

        assertThat(foundCigarroDTO, is(equalTo(expectedFoundCigarroDTO)));
    }

    @Test
    void whenNotRegisteredCigarroNameIsGivenThenThrowAnException() {
        // given
        CigarroDTO expectedFoundCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        // when
        when(cigarroRepository.findByName(expectedFoundCigarroDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(CigarroNotFoundException.class, () -> cigarroService.findByName(expectedFoundCigarroDTO.getName()));
    }

    @Test
    void whenListCigarroIsCalledThenReturnAListOfCigarros() {
        // given
        CigarroDTO expectedFoundCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedFoundCigarro = cigarroMapper.toModel(expectedFoundCigarroDTO);

        //when
        when(cigarroRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundCigarro));

        //then
        List<CigarroDTO> foundListCigarrosDTO = cigarroService.listAll();

        assertThat(foundListCigarrosDTO, is(not(empty())));
        assertThat(foundListCigarrosDTO.get(0), is(equalTo(expectedFoundCigarroDTO)));
    }

    @Test
    void whenListCigarroIsCalledThenReturnAnEmptyListOfCigarros() {
        //when
        when(cigarroRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<CigarroDTO> foundListCigarrosDTO = cigarroService.listAll();

        assertThat(foundListCigarrosDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenACigarroShouldBeDeleted() throws CigarroNotFoundException {
        // given
        CigarroDTO expectedDeletedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedDeletedCigarro = cigarroMapper.toModel(expectedDeletedCigarroDTO);

        // when
        when(cigarroRepository.findById(expectedDeletedCigarroDTO.getId())).thenReturn(Optional.of(expectedDeletedCigarro));
        doNothing().when(cigarroRepository).deleteById(expectedDeletedCigarroDTO.getId());

        // then
        cigarroService.deleteById(expectedDeletedCigarroDTO.getId());

        verify(cigarroRepository, times(1)).findById(expectedDeletedCigarroDTO.getId());
        verify(cigarroRepository, times(1)).deleteById(expectedDeletedCigarroDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementCigarroStock() throws CigarroNotFoundException, CigarroStockExceededException {
        //given
        CigarroDTO expectedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedCigarro = cigarroMapper.toModel(expectedCigarroDTO);

        //when
        when(cigarroRepository.findById(expectedCigarroDTO.getId())).thenReturn(Optional.of(expectedCigarro));
        when(cigarroRepository.save(expectedCigarro)).thenReturn(expectedCigarro);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedCigarroDTO.getQuantity() + quantityToIncrement;

        // then
        CigarroDTO incrementedCigarroDTO = cigarroService.increment(expectedCigarroDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedCigarroDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedCigarroDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        CigarroDTO expectedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedCigarro = cigarroMapper.toModel(expectedCigarroDTO);

        when(cigarroRepository.findById(expectedCigarroDTO.getId())).thenReturn(Optional.of(expectedCigarro));

        int quantityToIncrement = 80;
        assertThrows(CigarroStockExceededException.class, () -> cigarroService.increment(expectedCigarroDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        CigarroDTO expectedCigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        Cigarro expectedCigarro = cigarroMapper.toModel(expectedCigarroDTO);

        when(cigarroRepository.findById(expectedCigarroDTO.getId())).thenReturn(Optional.of(expectedCigarro));

        int quantityToIncrement = 45;
        assertThrows(CigarroStockExceededException.class, () -> cigarroService.increment(expectedCigarroDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(cigarroRepository.findById(INVALID_CIGARRO_ID)).thenReturn(Optional.empty());

        assertThrows(CigarroNotFoundException.class, () -> cigarroService.increment(INVALID_CIGARRO_ID, quantityToIncrement));
    }
//
//    @Test
//    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//
//        int quantityToDecrement = 80;
//        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
//    }
}
