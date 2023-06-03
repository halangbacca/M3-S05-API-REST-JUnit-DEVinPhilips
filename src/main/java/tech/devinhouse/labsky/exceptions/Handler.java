package tech.devinhouse.labsky.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.devinhouse.labsky.records.response.Exceptions;

import java.util.List;

@RestControllerAdvice
public class Handler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> trataErro404NotFound(EntityNotFoundException exception) {
        if (exception.getMessage().contains("Passageiro")) {
            return ResponseEntity.status(404).body("Passageiro não encontrado no banco de dados!");
        } else if (exception.getMessage().contains("Assento")) {
            return ResponseEntity.status(404).body("Assento não encontrado no banco de dados!");
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> trataErro400BadRequest(RuntimeException exception) {
        if (exception.getMessage().contains("idade")) {
            return ResponseEntity.status(400).body("O passageiro é menor de idade e não pode sentar nas fileiras de emergência!");
        } else if (exception.getMessage().contains("malas")) {
            return ResponseEntity.status(400).body("O passageiro deve obrigatoriamente despachar suas malas nas fileiras de emergência!");
        } else if (exception.getMessage().contains("check-in")) {
            return ResponseEntity.status(400).body("O passageiro já realizou check-in!");
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> trataErro404Conflict(EntityExistsException exception) {
        if (exception.getMessage().contains("ocupado")) {
            return ResponseEntity.status(409).body("O assento já está ocupado por outro passageiro!");
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Exceptions>> trataErro400Constraints(MethodArgumentNotValidException exception) {
        List<FieldError> erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(Exceptions::new).toList());
    }

}
