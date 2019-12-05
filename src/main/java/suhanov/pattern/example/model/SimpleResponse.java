package suhanov.pattern.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleResponse {
    private int userId;
    private int id;
    private String title;
    private Boolean completed;
}
