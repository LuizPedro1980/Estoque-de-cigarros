package one.digitalinnovation.cigarrostock.builder;

import lombok.Builder;
import one.digitalinnovation.cigarrostock.dto.CigarroDTO;
import one.digitalinnovation.cigarrostock.enums.CigarroType;

@Builder
public class CigarroDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private CigarroType type = CigarroType.LAGER;

    public CigarroDTO toCigarroDTO() {
        return new CigarroDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
