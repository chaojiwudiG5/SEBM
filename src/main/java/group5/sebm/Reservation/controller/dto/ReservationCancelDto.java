package group5.sebm.Reservation.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationCancelDto {
    @NotNull(message = "Reservation ID cannot be null")
    private String id;
}
