package mg.itu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mg.itu.dto.CryptoCoursDTO;
import mg.itu.model.CryptoCours;
import mg.itu.repository.CryptoCoursRepository;
import mg.itu.service.CryptoCoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crypto/prices")
@CrossOrigin(origins = "*")
@Tag(name = "Crypto Prices", description = "Endpoints for managing cryptocurrency prices and history")
public class CryptoCoursController {

    @Autowired
    private CryptoCoursService cryptoCoursService;

    @Autowired
    private CryptoCoursRepository cryptoCoursRepository;

    @Operation(summary = "Get all current cryptocurrency prices", 
               description = "Retrieve a list of all current cryptocurrency prices.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of prices",
                     content = @Content(mediaType = "application/json", 
                                       schema = @Schema(implementation = CryptoCoursDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<CryptoCoursDTO>> getAllPrices() {
        return ResponseEntity.ok(cryptoCoursService.getAllCurrentPrices());
    }

    @Operation(summary = "Get current price for a specific cryptocurrency", 
               description = "Retrieve the current price of a specific cryptocurrency by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the price",
                     content = @Content(mediaType = "application/json", 
                                       schema = @Schema(implementation = CryptoCoursDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cryptocurrency not found", 
                     content = @Content)
    })
    @GetMapping("/{cryptoId}")
    public ResponseEntity<CryptoCoursDTO> getCryptoPrice(@PathVariable Long cryptoId) {
        CryptoCoursDTO price = cryptoCoursService.getCryptoPrice(cryptoId);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(price);
    }

    @Operation(summary = "Get price history for a specific cryptocurrency", 
               description = "Retrieve the historical prices of a specific cryptocurrency for a given number of days.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price history",
                     content = @Content(mediaType = "application/json", 
                                       schema = @Schema(implementation = CryptoCoursDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cryptocurrency not found", 
                     content = @Content)
    })
    @GetMapping("/{cryptoId}/history")
    public ResponseEntity<List<CryptoCoursDTO>> getCryptoPriceHistory(
            @PathVariable Long cryptoId,
            @RequestParam(defaultValue = "7") Integer days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<CryptoCours> history = cryptoCoursRepository.findPriceHistory(cryptoId, startDate);

        List<CryptoCoursDTO> dtos = history.stream()
            .map(cours -> new CryptoCoursDTO(
                cours.getCrypto().getId(),
                cours.getCrypto().getLabel(),
                cours.getCours(),
                cours.getDateCours()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}