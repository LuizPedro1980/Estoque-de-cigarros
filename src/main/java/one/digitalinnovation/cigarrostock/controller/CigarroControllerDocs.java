package one.digitalinnovation.cigarrostock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.exception.CigarroAlreadyRegisteredException;
import one.digitalinnovation.cigarrostock.exception.CigarroNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages Cigarro stock")
public interface CigarroControllerDocs {

    @ApiOperation(value = "Cigarro creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success cigarro creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    CigarroDTO createCigarro(CigarroDTO cigarroDTO) throws CigarroAlreadyRegisteredException;

    @ApiOperation(value = "Returns cigarro found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success cigarro found in the system"),
            @ApiResponse(code = 404, message = "cigarro with given name not found.")
    })
    CigarroDTO findByName(@PathVariable String name) throws CigarroNotFoundException;

    @ApiOperation(value = "Returns a list of all cigarros registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all cigarros registered in the system"),
    })
    List<CigarroDTO> listCigarros();

    @ApiOperation(value = "Delete a cigarro found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success cigarro deleted in the system"),
            @ApiResponse(code = 404, message = "Cigarro with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws CigarroNotFoundException;
}
