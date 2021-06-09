package one.digitalinnovation.cigarrostock.controller;

import one.digitalinnovation.cigarrostock.builder.CigarroDTOBuilder;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.dto.QuantityDTO;
import one.digitalinnovation.cigarrostock.exception.CigarroNotFoundException;
import one.digitalinnovation.cigarrostock.service.CigarroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.cigarrostock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CigarroControllerTest {

    private static final String CIGARRO_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_CIGARRO_ID = 1L;
    private static final long INVALID_CIGARRO_ID = 2l;
    private static final String CIGARRO_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String CIGARRO_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private CigarroService cigarroService;

    @InjectMocks
    private CigarroController cigarroController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cigarroController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenACigarroIsCreated() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        // when
        when(cigarroService.createCigarro(cigarroDTO)).thenReturn(cigarroDTO);

        // then
        mockMvc.perform(post(CIGARRO_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cigarroDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(cigarroDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cigarroDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cigarroDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        cigarroDTO.setBrand(null);

        // then
        mockMvc.perform(post(CIGARRO_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cigarroDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        //when
        when(cigarroService.findByName(cigarroDTO.getName())).thenReturn(cigarroDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(CIGARRO_API_URL_PATH + "/" + cigarroDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cigarroDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cigarroDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cigarroDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        //when
        when(cigarroService.findByName(cigarroDTO.getName())).thenThrow(CigarroNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(CIGARRO_API_URL_PATH + "/" + cigarroDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithCigarrosIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        //when
        when(cigarroService.listAll()).thenReturn(Collections.singletonList(cigarroDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(CIGARRO_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(cigarroDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(cigarroDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(cigarroDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutCigarrosIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        //when
        when(cigarroService.listAll()).thenReturn(Collections.singletonList(cigarroDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(CIGARRO_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();

        //when
        doNothing().when(cigarroService).deleteById(cigarroDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(CIGARRO_API_URL_PATH + "/" + cigarroDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(CigarroNotFoundException.class).when(cigarroService).deleteById(INVALID_CIGARRO_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(CIGARRO_API_URL_PATH + "/" + INVALID_CIGARRO_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        CigarroDTO cigarroDTO = CigarroDTOBuilder.builder().build().toCigarroDTO();
        cigarroDTO.setQuantity(cigarroDTO.getQuantity() + quantityDTO.getQuantity());

        when(cigarroService.increment(VALID_CIGARRO_ID, quantityDTO.getQuantity())).thenReturn(cigarroDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(CIGARRO_API_URL_PATH + "/" + VALID_CIGARRO_ID + CIGARRO_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cigarroDTO.getName())))
                .andExpect(jsonPath("$.brand", is(cigarroDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(cigarroDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(cigarroDTO.getQuantity())));
    }

//    @Test
//    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }

//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        when(beerService.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
//                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
//                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
//                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
//    }
//
//    @Test
//    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(60)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        when(beerService.decrement(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
}
