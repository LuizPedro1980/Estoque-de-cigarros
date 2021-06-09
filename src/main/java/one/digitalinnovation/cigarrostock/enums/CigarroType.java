package one.digitalinnovation.cigarrostock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CigarroType {

    VIRGINIA("Virgínia"),
    LATAKIA("Latakia"),
    PERIQUE("Perique"),
    BURLEY("Burley"),
    ORIENTAL("Oriental");

    private final String description;
}
